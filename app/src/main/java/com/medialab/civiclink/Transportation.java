package com.medialab.civiclink;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Transportation extends AppCompatActivity {
    //RadioGroup car_availability;
    RadioButton yescar, nocar;

    TextView fit, need, pickuptime, availabletime, departaddress, pickupaddress, totext;
    EditText numfit, numneed, time, start_time, end_time, address;

    private static final String TAG = Transportation.class.getSimpleName();

    CheckBox currentlocation;

    Button submit;

    StringRequest request;
    private RequestQueue requestQueue;

    SessionManagement session;

    String name, email, eventName, eventID, eventAddress, phone, eventDate;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    private LocationManager mLocationManager;


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                //Log.d(TAG, String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                //drawMarker(location, "Current Location", 0);
                mLocationManager.removeUpdates(mLocationListener);
            }/*else{

                //Log.d(TAG, "location is null");
            }*/
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    Location loc=null;


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

        currentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Construct a GeoDataClient.

                mGeoDataClient = Places.getGeoDataClient(Transportation.this, null);

                // Construct a PlaceDetectionClient.
                mPlaceDetectionClient = Places.getPlaceDetectionClient(Transportation.this, null);

                // Construct a FusedLocationProviderClient.
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Transportation.this);

                mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                // Prompt the user for permission.
                getLocationPermission();

                getDeviceLocation();
            }
        });
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    private Location getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        final boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        Location location = null;
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation!=null){
                                /*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));*/
                                location = mLastKnownLocation;
                                //drawMarker(location);
                                Log.e(TAG, "last known location not null");
                                //Toast.makeText(getApplicationContext(), "last known location not null", Toast.LENGTH_LONG).show();
                            }else {
                                if(isNetworkEnabled) {
                                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, mLocationListener);
                                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    Log.e(TAG, "Network Enabled "+location);
                                    //Toast.makeText(getApplicationContext(), "Network Enabled "+location, Toast.LENGTH_LONG).show();
                                }
                                if(isGPSEnabled){
                                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);
                                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    Log.e(TAG, "GPS Enabled "+location);
                                    //Toast.makeText(getApplicationContext(), "GPS Enabled "+location, Toast.LENGTH_LONG).show();
                                }
                            }
                            if(location!=null){
                                Log.e(TAG, "Location found"+location.getLatitude()+" "+location.getLongitude());

                                //Toast.makeText(getApplicationContext(), "location not null, draw marker", Toast.LENGTH_LONG).show();
                                loc = location;

                                Log.e(TAG, loc.getLatitude()+", "+loc.getLongitude());

                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    String addresss = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();// Only if available else return NULL
                                    address.setText(addresss+" "+city+" "+state+" "+country+" "+postalCode);
                                    Log.e(TAG, addresss+" "+city+" "+state+" "+country+" "+postalCode);
                                }catch(IOException e){
                                    Log.e("Exception: %s", e.getMessage());
                                }
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //Toast.makeText(getApplicationContext(), "current location", Toast.LENGTH_LONG).show();
                            /*gMaps.get(position).moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            gMaps.get(event_locations.size()-1).getUiSettings().setMyLocationButtonEnabled(false);*/
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
        return loc;
    }
}
