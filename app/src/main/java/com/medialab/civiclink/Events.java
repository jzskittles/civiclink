package com.medialab.civiclink;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Events extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{// implements OnMapReadyCallback {
    Button transport, new_event;

    TextView length, distance;

    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<Item> feedItems;

    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String TAG = Events.class.getSimpleName();

    SessionManagement session;

    private String URL_FEED = "https://civiclink.000webhostapp.com/status.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_events);

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

        Cache cache1 = new DiskBasedCache(getCacheDir(), 1024 * 1024);

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache1, network);

        requestQueue.start();

        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<Item>();

        getFeed();
        //Log.e(TAG, "number of items after getFeed"+feedItems.size());


        new_event = (Button)findViewById(R.id.new_event);

        new_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateEvent.class));
                listAdapter.notifyDataSetChanged();
            }
        });
    }


    public void getFeed(){
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    Log.d(TAG, "getFeed called");
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        Log.d(TAG, "new getFeed called");
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }
    }

    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                Item item = new Item();
                item.setName(feedObj.getString("name"));
                item.setDate(feedObj.getString("date"));
                item.setTime(feedObj.getString("time"));
                item.setDetails(feedObj.getString("details"));
                item.setAddress(feedObj.getString("address"));
                item.setUid(feedObj.getString("uid"));

                Log.e(TAG, i+"");
                Log.e(TAG,item.getName()+","+item.getDate()+","+item.getTime()+","+item.getDetails()+","+item.getAddress()+","+item.getUid());


                feedItems.add(item);

                // notify data changes to list adapter
                //listAdapter.notifyDataSetChanged();
            }

            listAdapter = new FeedListAdapter(getApplicationContext(), this, feedItems);
            Log.e(TAG, ""+feedItems.size());
            listView.setAdapter(listAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            //startActivity(new Intent(getApplicationContext(), LocationListView.class));
        }
        else if (id == R.id.nav_logout_events) {
            session.logoutUser();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}