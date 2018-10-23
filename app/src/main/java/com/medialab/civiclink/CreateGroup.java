package com.medialab.civiclink;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.opencsv.CSVReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CreateGroup extends AppCompatActivity {

    EditText groupname, groupdetails, groupaddress;
    Button submit, getFile;

    private RequestQueue requestQueue;

    private StringRequest request;
    SessionManagement session;

    String host;

    ArrayList csvval;

    Spinner groupType;
    String groupt="";
    EditText others;

    RadioButton open, closed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_group);
        getFile = (Button)findViewById(R.id.pick_file);
        getFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        host = user.get(SessionManagement.KEY_EMAIL);
        Toast.makeText(getApplicationContext(), host, Toast.LENGTH_LONG).show();

        groupname = (EditText)findViewById(R.id.groupname);
        groupdetails = (EditText)findViewById(R.id.groupdetails);
        groupaddress = (EditText)findViewById(R.id.groupaddress);
        submit = (Button)findViewById(R.id.make_group);
        csvval = new ArrayList();

        others = (EditText)findViewById(R.id.newtype);

        groupType = (Spinner)findViewById(R.id.grouptype);

        requestQueue = Volley.newRequestQueue(this);

        open = (RadioButton)findViewById(R.id.open);
        closed = (RadioButton)findViewById(R.id.closed);

        String[] groupArray = {"Church", "School", "Bike club", "MIT", "Cult", "Other"};

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, groupArray);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupType.setAdapter(groupAdapter);
        groupType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                groupt = parent.getItemAtPosition(position).toString();
                if(parent.getItemAtPosition(position).toString().equals("Other")){
                    others.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request = new StringRequest(Request.Method.POST, "http://civiclink.000webhostapp.com/creategroup.php", new Response.Listener<String>() {
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
                        hashMap.put("groupname",groupname.getText().toString());
                        hashMap.put("groupdetails",groupdetails.getText().toString());
                        hashMap.put("groupaddress",groupaddress.getText().toString());
                        String groupmembers = "";
                        for(int i=0;i<csvval.size();i++){
                            groupmembers+= csvval.get(i);
                            groupmembers+= ", ";
                        }
                        String otherstext = others.getText().toString();
                        if(!otherstext.equals(""))
                            hashMap.put("grouptype",others.getText().toString());
                        else
                            hashMap.put("grouptype", groupt);
                        if(open.isChecked()){
                            hashMap.put("permissions", "open");
                            hashMap.put("code", "");
                        }else{
                            hashMap.put("permissions", "closed");
                            int rand = new Random().nextInt(9000)+1000;
                            hashMap.put("code",""+rand);
                        }
                        hashMap.put("groupmembers", groupmembers.substring(0,groupmembers.length()-2));
                        hashMap.put("admin",host);
                        Log.e("CreateGroup", groupname.getText().toString()+" "+groupdetails.getText().toString()+" "+groupaddress.getText().toString()+" "+groupt+" "+groupmembers+" "+host);
                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    private static final int READ_REQUEST_CODE = 42;
    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        csvval.clear();
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //Log.e("CreateGroup", uri.getPath());
                try{
                    File f = new File(Environment.getExternalStorageDirectory(), uri.getPath());
                    final String[] split = f.getPath().split(":");//split the path.
                    String fsplit = split[1];
                    //String fpaths = f.getAbsolutePath();
                    //Log.e("CreateGroup", f.getAbsolutePath()+" "+f.getPath());
                    CSVReader reader = new CSVReader(new FileReader(fsplit));
                    String [] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        // nextLine[] is an array of values from the line
                        csvval.add(nextLine[0]);
                        System.out.println(nextLine[0]);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
                }
                Log.i("CreateGroup", "Uri: " + uri.toString());
            }
        }
    }

}