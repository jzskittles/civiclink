package com.medialab.civiclink;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class Transportation extends AppCompatActivity {
    //RadioGroup car_availability;
    RadioButton yescar, nocar;

    TextView fit, need, pickuptime, availabletime, departaddress, pickupaddress, totext;
    EditText numfit, numneed, time, start_time, end_time, address;

    CheckBox currentlocation;

    Button submit;

    StringRequest request;
    private RequestQueue requestQueue;

    SessionManagement session;

    String name, email, eventName, eventID, eventAddress, phone, eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportation);

        yescar = (RadioButton)findViewById(R.id.yescar);
        nocar = (RadioButton)findViewById(R.id.nocar);

        fit = (TextView)findViewById(R.id.fit);
        numfit = (EditText) findViewById(R.id.numfit);

        need = (TextView)findViewById(R.id.need);
        numneed = (EditText)findViewById(R.id.numneed);

        pickuptime = (TextView)findViewById(R.id.pickuptime);
        availabletime = (TextView)findViewById(R.id.availabletime);
        time = (EditText)findViewById(R.id.time);
        start_time = (EditText)findViewById(R.id.start_time);
        totext = (TextView)findViewById(R.id.TO);
        end_time = (EditText)findViewById(R.id.end_time);

        pickupaddress = (TextView)findViewById(R.id.pickupaddress);
        departaddress = (TextView)findViewById(R.id.departaddress);
        address = (EditText)findViewById(R.id.address);
        currentlocation = (CheckBox)findViewById(R.id.currentlocation);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        name = user.get(SessionManagement.KEY_NAME);
        email = user.get(SessionManagement.KEY_EMAIL);
        phone = user.get(SessionManagement.KEY_PHONE);

        submit = (Button)findViewById(R.id.transport_request);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                eventID = null;
                eventAddress = null;
                eventName = null;
                eventDate = null;
            }else{
                eventID = extras.getString("eventID");
                eventAddress = extras.getString("eventAddress");
                eventName = extras.getString("eventName");
                eventDate = extras.getString("eventDate");
            }
        }

        yescar.setChecked(true);
        fit.setVisibility(View.VISIBLE);
        numfit.setVisibility(View.VISIBLE);
        need.setVisibility(View.INVISIBLE);
        numneed.setVisibility(View.INVISIBLE);
        nocar.setChecked(false);
        availabletime.setVisibility(View.VISIBLE);
        pickuptime.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        start_time.setVisibility(View.VISIBLE);
        totext.setVisibility(View.VISIBLE);
        end_time.setVisibility(View.VISIBLE);
        pickupaddress.setVisibility(View.INVISIBLE);
        departaddress.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        currentlocation.setVisibility(View.VISIBLE);

        yescar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit.setVisibility(View.VISIBLE);
                numfit.setVisibility(View.VISIBLE);
                need.setVisibility(View.INVISIBLE);
                numneed.setVisibility(View.INVISIBLE);
                nocar.setChecked(false);
                availabletime.setVisibility(View.VISIBLE);
                pickuptime.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                start_time.setVisibility(View.VISIBLE);
                totext.setVisibility(View.VISIBLE);
                end_time.setVisibility(View.VISIBLE);
                pickupaddress.setVisibility(View.INVISIBLE);
                departaddress.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                currentlocation.setVisibility(View.VISIBLE);

            }
        });
        nocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fit.setVisibility(View.INVISIBLE);
                numfit.setVisibility(View.INVISIBLE);
                need.setVisibility(View.VISIBLE);
                numneed.setVisibility(View.VISIBLE);
                yescar.setChecked(false);
                availabletime.setVisibility(View.INVISIBLE);
                pickuptime.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
                start_time.setVisibility(View.INVISIBLE);
                totext.setVisibility(View.INVISIBLE);
                end_time.setVisibility(View.INVISIBLE);
                pickupaddress.setVisibility(View.VISIBLE);
                departaddress.setVisibility(View.INVISIBLE);
                address.setVisibility(View.VISIBLE);
                currentlocation.setVisibility(View.VISIBLE);
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/submission.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("success")){
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();

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
                        String phonenum=""+phone;
                        String dorr="";
                        String timing = "";
                        String seats = "";
                        String addresss = address.getText()+"";

                        if(yescar.isChecked()){
                            dorr = "driver";
                            timing = start_time.getText()+"-"+end_time.getText();
                            seats = numfit.getText()+"";
                        }else{
                            dorr = "rider";
                            timing = time.getText()+"";
                            seats = numneed.getText()+"";
                        }

                        hashMap.put("name",name);
                        hashMap.put("email",email);
                        hashMap.put("phone", phonenum);
                        hashMap.put("eventName", eventName);
                        hashMap.put("eventID", eventID);
                        hashMap.put("eventAddress", eventAddress);
                        hashMap.put("dorr", dorr);
                        hashMap.put("times", timing);
                        hashMap.put("date", eventDate);
                        hashMap.put("address", addresss);
                        hashMap.put("seats", seats);
                        Log.e("WHAT", name+" "+ email+ " "+phonenum+" "+ eventName+" "+eventID+" "+ eventAddress+" "+ dorr+" "+timing+" "+eventDate+" "+addresss+" "+seats);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }
}
