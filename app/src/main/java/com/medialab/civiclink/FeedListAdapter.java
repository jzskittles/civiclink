package com.medialab.civiclink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.MapView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class FeedListAdapter extends BaseAdapter{//} implements OnMapReadyCallback {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Item> feedItems;
    private Context mContext;

    MapView mapView;

    private static final String TAG = Events.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

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

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private List<Location> event_locations;
    private List<Marker> event_markers;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                Log.d(TAG, String.format("%f, %f", location.getLatitude(), location.getLongitude()));
                drawMarker(location, "Current Location", 0);
                mLocationManager.removeUpdates(mLocationListener);
            }else{
                Log.d(TAG, "location is null");
            }
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

    private LocationManager mLocationManager;

    private TextView name, date, time, length, details, distance, address;

    List<GoogleMap> gMaps = new ArrayList<>();

    List<View> views = new ArrayList<>();
    String uid = "";

    public FeedListAdapter(Context context, Activity activity, List feedItems) {
        this.mContext = context;
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            try {
                convertView = inflater.inflate(R.layout.event_layout, null);
                Item item = feedItems.get(position);

                //RelativeLayout rl = convertView.findViewById(R.id.times);

                name = (TextView) convertView.findViewById(R.id.name);
                date = (TextView) convertView.findViewById(R.id.date);
                time = (TextView) convertView.findViewById(R.id.time);
                length = (TextView) convertView.findViewById(R.id.length);

                //RelativeLayout rl2 = convertView.findViewById(R.id.event_detail);
                details = (TextView) convertView.findViewById(R.id.details);
                distance = (TextView) convertView.findViewById(R.id.distance);

                //RelativeLayout rl3 = convertView.findViewById(R.id.mapss);
                address = (TextView) convertView.findViewById(R.id.address);

                name.setText(item.getName());
                date.setText(item.getDate());
                time.setText(item.getTime());
                details.setText(item.getDetails());
                address.setText(item.getAddress());
                length.setText(item.getLength());
                distance.setText(item.getDistance());
                uid = item.getUid();

                final String mapdest = item.getAddress();

                // Construct a GeoDataClient.
                mGeoDataClient = Places.getGeoDataClient(activity, null);

                // Construct a PlaceDetectionClient.
                mPlaceDetectionClient = Places.getPlaceDetectionClient(activity, null);

                // Construct a FusedLocationProviderClient.
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

                mLocationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
                event_locations = new ArrayList<>();
                event_markers = new ArrayList<>();

                final Button transport = (Button)convertView.findViewById(R.id.transport);
                transport.setTag(position);

                transport.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // Get the position
                        Intent intent = new Intent(mContext, Transportation.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("eventID", feedItems.get((Integer)transport.getTag()).getUid());
                        bundle.putString("eventAddress", feedItems.get((Integer)transport.getTag()).getAddress());
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);

                    }
                });

                // Build the map.
                mapView = (MapView)convertView.findViewById(R.id.mapView);
                mapView.onCreate(null);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        gMaps.add(googleMap);

                        //event_locations.clear();

                        // Use a custom info window adapter to handle multiple lines of text in the
                        // info window contents.
                        gMaps.get(position).setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            // Return null here, so that getInfoContents() is called next.
                            public View getInfoWindow(Marker arg0) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {
                                // Inflate the layouts for the info window, title and snippet.
                                View infoWindow = activity.getLayoutInflater().inflate(R.layout.custom_info_contents,
                                        (FrameLayout) activity.findViewById(R.id.mapView), false);

                                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                                title.setText(marker.getTitle());

                                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                                snippet.setText(marker.getSnippet());

                                return infoWindow;
                            }
                        });

                        // Prompt the user for permission.
                        getLocationPermission();

                        // Turn on the My Location layer and the related control on the map.
                        updateLocationUI();

                        // Get the current location of the device and set the position of the map.
                        Log.e(TAG, "async "+mapdest+" "+position);
                        getDeviceLocation(mapdest, position);
                    }
                });
                mapView.onResume();
            }catch(InflateException e){

            }
        }

        views.add(convertView);

        return convertView;

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation(final String destination, final int position) {
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
                                //Toast.makeText(getApplicationContext(), "last known location not null", Toast.LENGTH_LONG).show();
                            }else {
                                if(isNetworkEnabled) {
                                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, mLocationListener);
                                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    //Toast.makeText(getApplicationContext(), "Network Enabled "+location, Toast.LENGTH_LONG).show();
                                }
                                if(isGPSEnabled){
                                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, mLocationListener);
                                    location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    //Toast.makeText(getApplicationContext(), "GPS Enabled "+location, Toast.LENGTH_LONG).show();
                                }
                            }
                            if(location!=null){
                                //Toast.makeText(getApplicationContext(), "location not null, draw marker", Toast.LENGTH_LONG).show();
                                getEvent_Locations(location, destination, position);
                                drawMarker(location,"Current Location", position);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //Toast.makeText(getApplicationContext(), "current location", Toast.LENGTH_LONG).show();
                            gMaps.get(position).moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            gMaps.get(event_locations.size()-1).getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getEvent_Locations(Location location, String place, int position){
        //String place = address.getText().toString();
        //Log.e(TAG, address.getText().toString());

        Address eventLocation = getEventLocations(place);

        Location loca = new Location("");
        loca.setLatitude(eventLocation.getLatitude());
        loca.setLongitude(eventLocation.getLongitude());
        event_locations.add(loca);
        Log.e(TAG, ""+event_locations.size()+" "+place);
        drawMarker(loca, place, position);


        String url = getRequestUrl(location, loca);
        Wrapper w = new Wrapper();
        w.position = position;
        w.str = url;

        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(w);
    }

    private void drawMarker(Location location, String title, int position){
        LatLng gps = new LatLng(location.getLatitude(), location.getLongitude());
        Marker loc = gMaps.get(position).addMarker(new MarkerOptions().position(gps).title(title));
        event_markers.add(loc);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : event_markers) {
            builder.include(marker.getPosition());
            Log.d(TAG,marker.getPosition().latitude+" "+marker.getPosition().longitude);
        }
        LatLngBounds bounds = builder.build();

        int padding = 40; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        gMaps.get(position).animateCamera(cu);
    }

    private Address getEventLocations(String eventAddress){
        Geocoder coder = new Geocoder(activity.getApplicationContext());
        List<Address> addresses;
        try {
            addresses = coder.getFromLocationName(eventAddress, 1);
            if (addresses == null) {
                return null;
            }
            Address location = addresses.get(0);
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            return location;
        } catch (Exception e) {
            return null;
        }
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
        if (ContextCompat.checkSelfPermission(activity
                        .getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    /**
     * Handles the result of the request for location permissions.
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (gMaps.get(event_locations.size()-1) == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];

                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                                // Show a dialog offering the user the list of likely places, and add a
                                // marker at the selected place.
                                openPlacesDialog();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                gMaps.get(event_locations.size()-1).addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                gMaps.get(event_locations.size()-1).moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private String getRequestUrl(Location origin, Location dest) {
        //Value of origin
        String str_org = "origin=" + origin.getLatitude() +","+origin.getLongitude();
        //Value of destination
        String str_dest = "destination=" + dest.getLatitude()+","+dest.getLongitude();
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode+"&key=AIzaSyDiwngGmaormOj3PZr4K43hpYDR6xulZqA";
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<Wrapper, Void, Wrapper> {

        @Override
        protected Wrapper doInBackground(Wrapper... w) {
            String responseString = "";
            String strings = w[0].str;
            try {
                responseString = requestDirection(strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Wrapper neww = new Wrapper();
            neww.str = responseString;
            neww.position = w[0].position;
            return neww;
        }

        @Override
        protected void onPostExecute(Wrapper w) {
            //super.onPostExecute(w);

            Log.d("Feed", w.str);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(w);
        }
    }

    public class Wrapper{
        public List<List<HashMap<String, String>>> listss;
        public String str;
        public int position;
    }

    public class TaskParser extends AsyncTask<Wrapper, Void, Wrapper > {

        @Override
        protected Wrapper doInBackground(Wrapper... w) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(w[0].str);
                DirectionParser directionsParser = new DirectionParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            w[0].listss = routes;
            return w[0];
        }

        @Override
        protected void onPostExecute(Wrapper w) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : w.listss) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    String dist = point.get("dist");
                    String duration = point.get("duration");

                    feedItems.get(w.position).setLength(duration);
                    feedItems.get(w.position).setDistance(dist);

                    TextView lengthsss = views.get(w.position).findViewById(R.id.length);
                    lengthsss.setText(duration);

                    TextView distancess = views.get(w.position).findViewById(R.id.distance);
                    distancess.setText(dist);

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                (gMaps.get(w.position)).addPolyline(polylineOptions);
            } else {
                Toast.makeText(activity.getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
