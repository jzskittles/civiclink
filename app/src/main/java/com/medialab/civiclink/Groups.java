package com.medialab.civiclink;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.support.v7.widget.CardView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Groups extends AppCompatActivity {
    GridView mainGrid;
    GroupAdapter adapter;

    private RequestQueue requestQueue;

    private StringRequest request;

    SessionManagement session;

    String url = "http://civiclink.000webhostapp.com/groups.php?user=";
    String email="";

    String[] web= new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        mainGrid = (GridView) findViewById(R.id.mainGrid);

        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManagement(getApplication());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManagement.KEY_EMAIL);

        url = url+email;

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
                            if(ingroups.contains(",")){
                                web = ingroups.split(",");
                            }else{
                                web = new String[1];
                                web[0] = ingroups;
                            }
                            Log.e("GROUPS", ingroups);
                            Log.e("GROUPS", web[0]);

                            int[] imageId = {
                                    R.drawable.groups,
                                    R.drawable.groups,
                                    R.drawable.groups,
                                    R.drawable.groups
                            };

                            GroupAdapter adapter = new GroupAdapter(getApplicationContext(), web, imageId);
                            mainGrid.setAdapter(adapter);
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

        requestQueue.add(request);


    }
}
