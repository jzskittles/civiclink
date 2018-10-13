package com.medialab.civiclink;

import android.support.v4.app.FragmentActivity;

public class Item extends FragmentActivity  {
    private String name, date, time, length, details, distance, origin, address, uid;


    public Item() {
    }

    public Item(String name, String date, String time, String length, String details, String distance, String origin, String address, String uid) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.length = length;
        this.details = details;
        this.distance = distance;
        this.origin = origin;
        this.address = address;
        this.uid = uid;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}