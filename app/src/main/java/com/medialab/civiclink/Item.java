package com.medialab.civiclink;

import android.graphics.drawable.Drawable;

/**
 * Created by jenny on 12/23/2017.
 */

public class Item{
    CharSequence label; //package name
    CharSequence name; //app name
    Drawable icon; //app icon
    int position;

    public Item(){

    }

    //gets app characteristics
    public String getName(){
        return name.toString();
    }

    public void setName(String appName){
        name = appName;
    }

    public Drawable getIcon(){return icon;}

    public void setIcon(Drawable appIcon){icon = appIcon;}

}
