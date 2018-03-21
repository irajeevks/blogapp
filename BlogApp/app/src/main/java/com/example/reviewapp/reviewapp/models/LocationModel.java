package com.example.reviewapp.reviewapp.models;

public class LocationModel {
    public String location;
    public double lon;
    public double lat;

    public LocationModel() {
    }

    public LocationModel(String location, double lon, double lat) {
        this.location = location;
        this.lon = lon;
        this.lat = lat;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
