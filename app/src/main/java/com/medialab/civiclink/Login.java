package com.medialab.civiclink;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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

public class Login extends AppCompatActivity {

    SessionManagement session;
    Button login, register, login_button, register_button;
    EditText name, phone, email, password, street, num_seats, email_login, password_login;
    Spinner transportation, car;
    CheckBox driving;

    String transport="";
    String car_type = "";

    private RequestQueue requestQueue;

    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.login);
        register = (Button)findViewById(R.id.register);
        login_button = (Button)findViewById(R.id.login_button);
        register_button = (Button)findViewById(R.id.register_button);

        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        street = (EditText)findViewById(R.id.street);
        num_seats = (EditText)findViewById(R.id.num_seats);
        driving = (CheckBox)findViewById(R.id.driving);
        email_login = (EditText)findViewById(R.id.email_login);
        password_login = (EditText)findViewById(R.id.password_login);

        transportation = (Spinner)findViewById(R.id.transportation);
        car = (Spinner)findViewById(R.id.car_type);

        final String[] textArray = {"Car", "Bike", "Walk", "Need a ride"};
        //Integer[] imageArray = { R.drawable.clouds, R.drawable.mark, R.drawable.techcrunch, R.drawable.times };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_login.setVisibility(View.VISIBLE);
                password_login.setVisibility(View.VISIBLE);
                login_button.setVisibility(View.VISIBLE);
                register_button.setVisibility(View.INVISIBLE);
                name.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.INVISIBLE);
                email.setVisibility(View.INVISIBLE);
                password.setVisibility(View.INVISIBLE);
                street.setVisibility(View.INVISIBLE);
                num_seats.setVisibility(View.INVISIBLE);
                transportation.setVisibility(View.INVISIBLE);
                car.setVisibility(View.INVISIBLE);
                driving.setVisibility(View.INVISIBLE);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_login.setVisibility(View.INVISIBLE);
                password_login.setVisibility(View.INVISIBLE);
                login_button.setVisibility(View.INVISIBLE);
                register_button.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                street.setVisibility(View.VISIBLE);
                transportation.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Login.this,
                        android.R.layout.simple_spinner_item, textArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                transportation.setAdapter(adapter);
                transportation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        transport = parent.getItemAtPosition(position).toString();

                        if(parent.getItemAtPosition(position).toString().equals("Car")){
                            car.setVisibility(View.VISIBLE);
                            num_seats.setVisibility(View.VISIBLE);
                            driving.setVisibility(View.VISIBLE);
                            String[] carArray = {"Acura", "Audi", "BMW", "Buick", "Cadillac"};
                            ArrayAdapter<String> carAdapter = new ArrayAdapter<String>(Login.this,
                                    android.R.layout.simple_spinner_item, carArray);

                            carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            car.setAdapter(carAdapter);
                            car.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view,
                                                           int position, long id) {
                                    Log.v("item", (String) parent.getItemAtPosition(position));
                                    car_type = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // TODO Auto-generated method stub
                                }
                            });
                        }else{
                            car.setVisibility(View.GONE);
                            num_seats.setVisibility(View.GONE);
                            driving.setVisibility(View.GONE);
                        }
                        Log.v("item", (String) parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        login_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/login.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("success")){
                                Toast.makeText(getApplicationContext(), "SUCCESS: " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                                int phone = 0;
                                if(jsonObject.getString("phone")!=null)
                                    phone = Integer.parseInt(jsonObject.getString("phone"));

                                String email = jsonObject.getString("email");
                                String street = jsonObject.getString("street");

                                String transportation = jsonObject.getString("transportation");
                                String cartype = "";
                                if(jsonObject.getString("cartype")!=null)
                                    cartype = jsonObject.getString("cartype");
                                int numseats = 0;
                                if(jsonObject.getString("numseats")!=null)
                                    numseats = Integer.parseInt(jsonObject.getString("numseats"));
                                String driving = "";
                                if(jsonObject.getString("driving")!=null)
                                    jsonObject.getString("driving");

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
                        hashMap.put("email",email_login.getText().toString());
                        hashMap.put("password",password_login.getText().toString());
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/register.php", new Response.Listener<String>() {
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
                        hashMap.put("name",name.getText().toString());
                        hashMap.put("phone",phone.getText().toString());
                        hashMap.put("email",email.getText().toString());
                        hashMap.put("password",password.getText().toString());
                        hashMap.put("street",street.getText().toString());
                        hashMap.put("transportation", transport);
                        hashMap.put("cartype",car_type);
                        if(num_seats.toString().equals(""))
                            num_seats.setText("0");
                        hashMap.put("numseats",num_seats.getText().toString());
                        if(driving.isChecked())
                            hashMap.put("driving", "True");
                        else
                            hashMap.put("driving", "False");
                        return hashMap;
                    }

                };

                requestQueue.add(request);
            }
        });
    }
}
