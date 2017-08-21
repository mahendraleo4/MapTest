package com.example.leometric.maptest;

/**
 * Created by leometric on 11/8/17.
 */

public class User {

    public String name;
    public String lat;
    public String lng;
    public String location_name;
    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User(){

}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public User(String name, String lat, String lng, String location_name) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.location_name = location_name;
    }
}
