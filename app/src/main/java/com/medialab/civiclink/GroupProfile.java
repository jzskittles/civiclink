package com.medialab.civiclink;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class GroupProfile extends AppCompatActivity{ //implements NavigationView.OnNavigationItemSelectedListener{
    SessionManagement session;

    String groupname, groupdetails, groupaddress, grouptype, groupmembers, groupadmin, groupcode, ingroup;

    TextView profilename, profiledetails, profileaddress, profiletype, profileadmin;

    Button join;

    ListView profilemembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        */

        session = new SessionManagement(getApplication());
        session.checkLogin();

        join = (Button)findViewById(R.id.joingroup);


        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                groupname = null;
                groupdetails = null;
                groupaddress = null;
                grouptype = null;
                groupmembers = null;
                groupadmin = null;
                groupcode = null;
                ingroup = null;
            }else{
                groupname = extras.getString("groupname");
                groupdetails = extras.getString("groupdetails");
                groupaddress = extras.getString("groupaddress");
                grouptype = extras.getString("grouptype");
                groupmembers = extras.getString("groupmembers");
                groupadmin = extras.getString("groupadmin");
                groupcode = extras.getString("groupcode");
                ingroup = extras.getString("ingroup");

                if(ingroup.equals("yes")){
                    join.setVisibility(View.GONE);
                }else{
                    join.setVisibility(View.VISIBLE);
                }
            }
        }

        profilename = (TextView)findViewById(R.id.profilename);
        profiledetails = (TextView)findViewById(R.id.profiledetails);
        profileaddress = (TextView)findViewById(R.id.profileaddress);
        profiletype = (TextView)findViewById(R.id.profiletype);
        profilemembers = (ListView)findViewById(R.id.profilemembers);
        profileadmin = (TextView)findViewById(R.id.profileadmin);



        profilename.setText(groupname);
        profiledetails.setText(groupdetails);
        profileaddress.setText(groupaddress);
        profiletype.setText(grouptype);
        String[] members = groupmembers.split(",");

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, members);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profilemembers.setAdapter(groupAdapter);
        profileadmin.setText(groupadmin);
    }

}
