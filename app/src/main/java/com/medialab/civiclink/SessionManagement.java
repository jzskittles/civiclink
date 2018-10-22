package com.medialab.civiclink;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {

    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_STREET = "street";
    public static final String KEY_TRANSPORTATION = "transportation";
    public static final String KEY_CARTYPE = "cartype";
    public static final String KEY_GROUPS = "groups";
    public static final String KEY_NUMSEATS = "numseats";

    public static final String KEY_DRIVING = "driving";

    // Constructor
    public SessionManagement (Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, int phone, String email, String password, String street, String transportation, String cartype, String groups, int numseats, String driving) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PHONE, String.valueOf(phone));
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_STREET, street);
        editor.putString(KEY_TRANSPORTATION, transportation);
        editor.putString(KEY_CARTYPE, cartype);
        editor.putString(KEY_GROUPS, groups);
        editor.putString(KEY_NUMSEATS, String.valueOf(numseats));
        editor.putString(KEY_DRIVING, driving);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, "null"));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, "null"));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, "null"));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, "null"));
        user.put(KEY_STREET, pref.getString(KEY_STREET, "null"));
        user.put(KEY_TRANSPORTATION, pref.getString(KEY_TRANSPORTATION, "null"));
        user.put(KEY_CARTYPE, pref.getString(KEY_CARTYPE, "null"));
        user.put(KEY_GROUPS, pref.getString(KEY_GROUPS, "null"));
        user.put(KEY_NUMSEATS, pref.getString(KEY_NUMSEATS, "null"));
        user.put(KEY_DRIVING, pref.getString(KEY_DRIVING, "null"));
        return user;
    }

    public void checkLogin() {
        if(!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }



    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}