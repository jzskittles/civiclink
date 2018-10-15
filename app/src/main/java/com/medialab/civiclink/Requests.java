package com.medialab.civiclink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SessionManagement session;

    private RequestQueue requestQueue;

    private StringRequest request;
    private static final String TAG = Requests.class.getSimpleName();

    private String URL = "";
    private String username, email, name;

    private TabHost tabhost;

    private ListView tab1, tab2, tab3;

    private RelativeLayout view1, view2, view3;

    List<Map<String, String>> approvedList, deniedList, pendingList;

    private ArrayList approvedID, deniedID, pendingID;

    private String[] from = {"uid","name", "date", "eventName", "eventAddress", "times", "address", "seats"};
    private int[] to = {R.id.number, R.id.rname_logitem, R.id.date_logitem, R.id.event_logitem, R.id.event_address_logitem, R.id.pickup_time_logitem, R.id.pickup_address_logitem, R.id.seats_logitem};

    private BaseAdapter simpleAdapterPending, simpleAdapterApproved, simpleAdapterDenied;
    private ArrayList<String> uids;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_requests);

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

        session = new SessionManagement(getApplication());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(SessionManagement.KEY_NAME);

        pendingList = new ArrayList<Map<String, String>>();
        approvedList = new ArrayList<Map<String, String>>();
        deniedList = new ArrayList<Map<String, String>>();

        pendingID = new ArrayList<String>();
        approvedID = new ArrayList<String>();
        deniedID = new ArrayList<String>();

        tabhost = (TabHost) findViewById(R.id.tabHost);
        tabhost.setup();
        TabHost.TabSpec pending = tabhost.newTabSpec("Pending");
        TabHost.TabSpec approved = tabhost.newTabSpec("Approved");
        TabHost.TabSpec denied = tabhost.newTabSpec("Denied");

        TabWidget t = (TabWidget) findViewById(android.R.id.tabs);
        for(int i=0;i<t.getChildCount();i++){
            t.getChildAt(i).setBackgroundResource(R.drawable.log_tabs);
        }

        requestQueue = Volley.newRequestQueue(this);

        tab1 = (ListView) findViewById(R.id.tab1);
        tab2 = (ListView) findViewById(R.id.tab2);
        tab3 = (ListView) findViewById(R.id.tab3);

        view1 = new RelativeLayout(getApplicationContext());
        view2 = new RelativeLayout(getApplicationContext());
        view3 = new RelativeLayout(getApplicationContext());

        simpleAdapterPending = new SimpleAdapter(this, pendingList,
                R.layout.pending_list_items,
                from, to);
        tab1.setAdapter(simpleAdapterPending);

        simpleAdapterApproved = new SimpleAdapter(this, approvedList,
                R.layout.approved_list_items,
                from, to);
        tab2.setAdapter(simpleAdapterApproved);

        simpleAdapterDenied = new SimpleAdapter(this, deniedList,
                R.layout.denied_list_items,
                from, to);
        tab3.setAdapter(simpleAdapterDenied);


        String pending_url = "http://civiclink.000webhostapp.com/pending.php?user=" + name;
        String approved_url = "http://civiclink.000webhostapp.com/approved.php?user=" + name;
        String denied_url = "http://civiclink.000webhostapp.com/denied.php?user=" + name;
        getPendingContent(pending_url);
        getApprovedContent(approved_url);
        getDeniedContent(denied_url);

        pending.setIndicator("Pending");
        pending.setContent(tab1.getId());
        approved.setIndicator("Approved");
        approved.setContent(tab2.getId());
        denied.setIndicator("Denied");
        denied.setContent(tab3.getId());

        tabhost.addTab(pending);
        tabhost.addTab(approved);
        tabhost.addTab(denied);

        for(int i = 0; i <tabhost.getTabWidget().getChildCount(); i++)
        {
            if(i==0)
            {
                tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#00000000"));
                TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.log_tabs);
                TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                for(int i=0;i<tabhost.getTabWidget().getChildCount();i++)
                {
                    tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.log_tabs); //unselected
                }
                tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#00000000")); // selected
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            Toast.makeText(getApplicationContext(), "Requests", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Requests.class);
                /*Bundle bundle = new Bundle();
                bundle.putString("username", username1);
                i.putExtras(bundle);*/

            startActivity(i);
            //startActivity(new Intent(getApplicationContext(), ProfileDonor.class));
            /*}else
                startActivity(new Intent(getApplicationContext(), ProfileReceiver.class));*/
        } else if (id == R.id.nav_groups_events) {
            //startActivity(new Intent(getApplicationContext(), LocationListView.class));
        } else if (id == R.id.nav_logout_events) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getApprovedContent(String url) {

        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    approvedList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        int length = jsonObject.getInt("length");
                        for(int i = 0; i < length; i++) {
                            JSONObject row = jsonObject.getJSONObject(i+"");

                            String uniqueid = row.getString("uid");
                            approvedID.add(uniqueid);
                            String name = row.getString("rname");
                            String eventName = row.getString("eventName");
                            String eventID = row.getString("eventID");
                            String eventAddress = row.getString("eventAddress");
                            String date = row.getString("date");
                            /*String email = row.getString("email");
                            int phone = row.getInt("phone");*/
                            String pickup_address = row.getString("pickup_address");
                            //String dorr = row.getString("dorr");
                            String times = row.getString("pickup_time");
                            //String address = row.getString("address");
                            int seats = row.getInt("seats");
                            Log.e("Approved", name+" "+date+" "+eventName+" "+eventID+" "+pickup_address);

                            approvedList.add(createForm(uniqueid, name, date, eventName, eventID, eventAddress, times, pickup_address, seats));
                            simpleAdapterApproved.notifyDataSetChanged();
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

        //perform listView item click event
        tab2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(getApplicationContext(), ViewForm.class);
                intent.putExtra("com.example.angela.MESSAGE", "approved " + approvedID.get(i).toString());
                startActivity(intent);*/
            }
        });
    }

    public void getDeniedContent(String url) {

        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    deniedList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        int length = jsonObject.getInt("length");
                        for(int i = 0; i < length; i++) {

                            JSONObject row = jsonObject.getJSONObject(i+"");

                            String uniqueid = row.getString("uniqueid");
                            deniedID.add(uniqueid);
                            String name = row.getString("rname");
                            String eventName = row.getString("eventName");
                            String eventID = row.getString("eventID");
                            String eventAddress = row.getString("eventAddress");
                            String date = row.getString("date");
                            /*String email = row.getString("email");
                            int phone = row.getInt("phone");*/
                            String pickup_address = row.getString("pickup_address");
                            //String dorr = row.getString("dorr");
                            String times = row.getString("pickup_time");
                            //String address = row.getString("address");
                            int seats = row.getInt("seats");
                            Log.e("Denied", name+" "+date+" "+eventName+" "+eventID+" "+pickup_address);

                            deniedList.add(createForm(uniqueid, name, date, eventName, eventID, eventAddress, times, pickup_address, seats));
                            simpleAdapterDenied.notifyDataSetChanged();
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

        //perform listView item click event
        tab3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*(Intent intent = new Intent(getApplicationContext(), ViewForm.class);
                intent.putExtra(EXTRA_MESSAGE, "denied " + deniedID.get(i).toString());
                startActivity(intent);*/
            }
        });
    }

    public void getPendingContent(String url) {
        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    pendingList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("success")){
                        int length = jsonObject.getInt("length");
                        for(int i = 0; i < length; i++) {

                            JSONObject row = jsonObject.getJSONObject(i+"");

                            String uniqueid = row.getString("uniqueid");
                            pendingID.add(uniqueid);
                            String name = row.getString("name");
                            String date = row.getString("date");
                            String email = row.getString("email");
                            int phone = row.getInt("phone");
                            String eventName = row.getString("eventName");
                            String eventID = row.getString("eventID");
                            String eventAddress = row.getString("eventAddress");
                            String dorr = row.getString("dorr");
                            String times = row.getString("times");
                            String address = row.getString("address");
                            int seats = row.getInt("seats");
                            Log.e("Pending", email+" "+phone+" "+eventName+" "+eventID+" "+eventAddress+" "+dorr+" "+times+" "+address);

                            pendingList.add(createForm(uniqueid, name, date, eventName, eventID, eventAddress, times, address, seats));
                            simpleAdapterPending.notifyDataSetChanged();
                        }
                        // Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
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

        //perform listView item click event
        /*tab1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ViewForm.class);
                intent.putExtra(EXTRA_MESSAGE, "submission " + pendingID.get(i).toString());
                startActivity(intent);
            }
        });*/
    }

    public void approveRequest(View v){
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout parentparent = (LinearLayout) vwParentRow.getParent();
        RelativeLayout child1 = (RelativeLayout) parentparent.getChildAt(1);
        TextView tv = (TextView) child1.getChildAt(2);
        final String uid = tv.getText().toString();
        Log.e("approve",uid);
        request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/claim.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                try {
                    //Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("successUpdate")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successUpdate"), Toast.LENGTH_SHORT).show();
                        String urlpending = "http://civiclink.000webhostapp.com/pending.php?user=" + name;
                        String urlapproved = "http://civiclink.000webhostapp.com/approved.php?user=" + name;
                        for(int i=0;i<pendingList.size();i++){
                            if(pendingList.get(i).get("uid").equals(uid)){
                                approvedList.add(pendingList.get(i));
                                pendingList.remove(pendingList.get(i));
                            }
                        }
                        simpleAdapterPending.notifyDataSetChanged();
                        getPendingContent(urlpending);
                        simpleAdapterApproved.notifyDataSetChanged();
                        getApprovedContent(urlapproved);

                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: " + name + ", " + uid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("uid", uid);
                hashMap.put("driver", name);
                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void denyRequest(View v){
        LinearLayout vwParentRow = (LinearLayout) v.getParent();
        LinearLayout parentparent = (LinearLayout) vwParentRow.getParent();
        RelativeLayout child1 = (RelativeLayout) parentparent.getChildAt(1);
        TextView tv = (TextView) child1.getChildAt(2);
        final String uid = tv.getText().toString();
        Log.e("deny", uid);
        request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/deny.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                try {
                    //Toast.makeText(getApplicationContext(), "successfully inside the try", Toast.LENGTH_SHORT).show();
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("successInsert")) {
                        String urlpending = "http://civiclink.000webhostapp.com/pending.php?user=" + name;
                        String urldenied = "http://civiclink.000webhostapp.com/denied.php?user=" + name;
                        Log.e("what's", uid+" "+pendingList.size());
                        //pendingList.remove(uid);
                        for(int i=0;i<pendingList.size();i++){
                            if(pendingList.get(i).get("uid").equals(uid)){
                                deniedList.add(pendingList.get(i));
                                pendingList.remove(pendingList.get(i));
                            }
                        }
                        //Log.e("left", pendingList.get(0)+" "+pendingList.size());
                        simpleAdapterPending.notifyDataSetChanged();
                        getPendingContent(urlpending);
                        simpleAdapterDenied.notifyDataSetChanged();
                        getDeniedContent(urldenied);
                        Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("successInsert"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "ERROR: " + name + ", " + uid, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("uid", uid);
                hashMap.put("driver", name);
                return hashMap;
            }

        };

        requestQueue.add(request);
    }


    private HashMap<String, String> createForm(String uniqueid, String name, String date, String eventName, String eventID, String eventAddress, String times, String address, int seats) {
        HashMap<String, String> formNameID = new HashMap<String, String>();
        formNameID.put("uid",uniqueid);
        formNameID.put("name", name);
        formNameID.put("date", date);
        formNameID.put("eventName", eventName);
        formNameID.put("eventID", eventID);
        formNameID.put("eventAddress", eventAddress);
        formNameID.put("times", times);
        formNameID.put("address", address);
        formNameID.put("seats", ""+seats);
        return formNameID;
    }
}