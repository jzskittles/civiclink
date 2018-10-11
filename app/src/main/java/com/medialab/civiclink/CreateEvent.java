package com.medialab.civiclink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateEvent extends AppCompatActivity {

    EditText name, date, time, details, address;
    Button submit;

    private RequestQueue requestQueue;

    private StringRequest request;
    SessionManagement session;

    String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        host = user.get(SessionManagement.KEY_EMAIL);
        Toast.makeText(getApplicationContext(), host, Toast.LENGTH_LONG).show();

        name = (EditText)findViewById(R.id.createname);
        date = (EditText)findViewById(R.id.createdate);
        time = (EditText)findViewById(R.id.createtime);
        details = (EditText)findViewById(R.id.createdetails);
        address = (EditText)findViewById(R.id.createaddress);
        submit = (Button)findViewById(R.id.make_event);

        requestQueue = Volley.newRequestQueue(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/new_event.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("success")){
                                startActivity(new Intent(getApplicationContext(),Events.class));
                            }else{
                                Toast.makeText(getApplicationContext(),"ERROR: "+jsonObject.getString("error"),Toast.LENGTH_SHORT).show();
                            }
                        }catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap=new HashMap<String,String>();
                        hashMap.put("name",name.getText().toString());
                        hashMap.put("date",date.getText().toString());
                        hashMap.put("time",time.getText().toString());
                        hashMap.put("details",details.getText().toString());
                        hashMap.put("address",address.getText().toString());
                        hashMap.put("host",host);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }
}
