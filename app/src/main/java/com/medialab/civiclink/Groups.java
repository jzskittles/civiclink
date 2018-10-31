package com.medialab.civiclink;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.support.v7.widget.CardView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Groups extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    GridView mainGrid, yourGrid, totalGrid;
    GroupAdapter adapter, adapter2, adaptertotal;

    private RequestQueue requestQueue;

    private StringRequest request, request2;

    SessionManagement session;

    String url = "http://civiclink.000webhostapp.com/groups.php?user=";
    String url2 = "http://civiclink.000webhostapp.com/othergroups.php?user=";
    String email="";

    ArrayList<String> web = new ArrayList<>();
    ArrayList<String> web2 = new ArrayList<>();
    ArrayList<String> webtotal = new ArrayList<>();


    Button new_group, join_group;

    int previousLength;
    boolean backSpace;

    TextView yourgroup, allgroup, totalgroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        mainGrid = (GridView) findViewById(R.id.mainGrid);
        yourGrid = (GridView) findViewById(R.id.yourGrid);
        totalGrid = (GridView) findViewById(R.id.totalGrid);

        yourgroup = (TextView)findViewById(R.id.yourGroups);
        allgroup = (TextView)findViewById(R.id.allGroups);
        totalgroup = (TextView)findViewById(R.id.totalGroups);

        //INITIALIZE GRIDVIEW AND MATERIAL SEARCHBAR
        MaterialSearchBar searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        searchBar.setHint("Search..");
        searchBar.setSpeechMode(true);

        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManagement(getApplication());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManagement.KEY_EMAIL);

        new_group = (Button)findViewById(R.id.new_group);
        new_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateGroup.class));
            }
        });

        url = url+email;
        url2 = url2+email;

        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        int length = jsonObject.getInt("length");
                        for(int i = 0; i < length; i++) {
                            JSONObject row = jsonObject.getJSONObject(i+"");

                            String ingroups = row.getString("groups");
                            String [] temp;
                            if(ingroups.contains(",")){
                                temp = ingroups.split(",");
                            }else{
                                temp = new String[1];
                                temp[0] = ingroups;
                            }
                            for(int j=0;j<temp.length;j++){
                                web.add(temp[j]);
                                webtotal.add(temp[j]);
                            }

                            Log.e("GROUPS", ingroups);
                            Log.e("GROUPS", web.get(0));

                            int[] imageId = new int[web.size()];
                            for(int j=0;j<imageId.length;j++) {
                                imageId[j] = R.drawable.groups;
                            }

                            adapter = new GroupAdapter(getApplicationContext(), web, imageId);
                            yourGrid.setAdapter(adapter);
                            yourGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Toast.makeText(getApplicationContext(), "You Clicked at " +position, Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        //Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    }else{
                        if(jsonObject.has("empty")) {
                            Toast.makeText(getApplicationContext(),"EMPTY: "+jsonObject.getString("empty"),Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }

        });

        requestQueue.add(request);



        request2 = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        int length = jsonObject.getInt("length");
                        for(int i = 0; i < length-1; i++) {
                            JSONObject row = jsonObject.getJSONObject(i+"");

                            String groupname = row.getString("groupname");
                            String groupdetails = row.getString("groupdetails");
                            String address = row.getString("groupaddress");
                            String grouptype = row.getString("grouptype");
                            String groupmembers = row.getString("groupmembers");
                            String code = row.getString("code");
                            String admin = row.getString("admin");
                            web2.add(groupname);
                            webtotal.add(groupname);

                            Log.e("GROUPS", web2.get(0));

                            int[] imageId = new int[web2.size()];
                            for(int j=0;j<imageId.length;j++) {
                                imageId[j] = R.drawable.groups;
                            }

                            adapter2 = new GroupAdapter(getApplicationContext(), web2, imageId);
                            mainGrid.setAdapter(adapter2);
                            mainGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    Toast.makeText(getApplicationContext(), "You Clicked at " +position, Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        //Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    }else{
                        if(jsonObject.has("empty")) {
                            Toast.makeText(getApplicationContext(),"EMPTY: "+jsonObject.getString("empty"),Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "ERROR: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }

        });

        requestQueue.add(request2);

        //final ArrayList<String> webtotal = new ArrayList<>();

        final List<String> grouplist = new ArrayList<>();

        for(int x=0;x<web.size();x++){
            grouplist.add(web.get(x));
            //webtotal.add(web.get(x));
        }
        for(int x=0;x<web2.size();x++){
            grouplist.add(web2.get(x));
            //webtotal.add(web2.get(x));
        }

        //webtotal.addAll(web);
        //webtotal.addAll(web2);
        //Log.e("NUMBER", webtotal.size()+"");

        for(int x=0;x<webtotal.size();x++){
            Log.e("GROUPS ??", webtotal.get(x));
        }

        int[] imageIdtotal = new int[webtotal.size()];
        for(int j=0;j<imageIdtotal.length;j++) {
            imageIdtotal[j] = R.drawable.groups;
        }

        adaptertotal = new GroupAdapter(getApplicationContext(), webtotal, imageIdtotal);
        totalGrid.setAdapter(adaptertotal);

        //TEXT CHANGE LISTENER
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                previousLength = charSequence.length();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //SEARCH FILTER
                //adapter.getFilter().filter(charSequence);
                backSpace = previousLength > charSequence.length();

                if(charSequence.length()==0){
                    yourgroup.setVisibility(View.VISIBLE);
                    allgroup.setVisibility(View.VISIBLE);
                    yourGrid.setVisibility(View.VISIBLE);
                    mainGrid.setVisibility(View.VISIBLE);
                    totalGrid.setVisibility(View.GONE);
                    totalgroup.setVisibility(View.GONE);
                    int[] imageId = new int[web.size()];
                    for(int j=0;j<imageId.length;j++) {
                        imageId[j] = R.drawable.groups;
                    }

                    adapter = new GroupAdapter(getApplicationContext(), web, imageId);
                    int[] imageId2 = new int[web2.size()];
                    for(int j=0;j<imageId2.length;j++) {
                        imageId2[j] = R.drawable.groups;
                    }

                    adapter2 = new GroupAdapter(getApplicationContext(), web2, imageId2);
                    //adapter.setWeb(web);
                    //adapter2.setWeb(web2);
                }else{
                    yourgroup.setVisibility(View.GONE);
                    allgroup.setVisibility(View.GONE);
                    yourGrid.setVisibility(View.GONE);
                    mainGrid.setVisibility(View.GONE);
                    totalGrid.setVisibility(View.VISIBLE);
                    totalgroup.setVisibility(View.VISIBLE);

                    //Toast.makeText(getApplicationContext(), "ontextchanged "+charSequence, Toast.LENGTH_LONG).show();
                    Log.e("GROUPS ??", "ontextchanged "+charSequence);
                    //Log.e("GROUPS? ?", "1 "+adapter.getCount()+" 2 "+adapter2.getCount());

                    if (backSpace) {
                        adaptertotal.setWeb(webtotal);
                        //adapter2.setWeb(new ArrayList<String>());
                        //Log.e("GROUPS? ? ?", "1 "+adapter.getCount()+" 2 "+adapter2.getCount());
                        adaptertotal.getFilter().filter(charSequence);
                        //Log.e("GROUPS after ?", "1 "+adapter.getCount()+" 2 "+adapter2.getCount());
                    }else{
                        adaptertotal.setWeb(webtotal);
                        //adapter2.setWeb(new ArrayList<String>());
                        //Log.e("GROUPS? ? ?", "1 "+adapter.getCount()+" 2 "+adapter2.getCount());
                        adaptertotal.getFilter().filter(charSequence);
                        //Log.e("GROUPS after", "1 "+adapter.getCount()+" 2 "+adapter2.getCount());
                    }
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home_events) {
            /*if(dorr.equals("donor")){
                Toast.makeText(getApplicationContext(),"Welcome Donor",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), WelcomeDonor.class));
            }else
                startActivity(new Intent(getApplicationContext(), WelcomeReceiver.class));*/
            startActivity(new Intent(getApplicationContext(), Events.class));
        } else if (id == R.id.nav_requests_events) {
            /*Toast.makeText(getApplicationContext(),"Profile Receiver",Toast.LENGTH_SHORT).show();
            if(dorr.equals("donor")){*/
            Toast.makeText(getApplicationContext(),"Requests",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Requests.class);
                /*Bundle bundle = new Bundle();
                bundle.putString("username", username1);
                i.putExtras(bundle);*/

            startActivity(i);
            //startActivity(new Intent(getApplicationContext(), ProfileDonor.class));
            /*}else
                startActivity(new Intent(getApplicationContext(), ProfileReceiver.class));*/
        } else if (id == R.id.nav_groups_events){
            startActivity(new Intent(getApplicationContext(), Groups.class));
        }
        else if (id == R.id.nav_logout_events) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
