package com.tasks.earthQuake.Model;

public class EarthQuake {
    private  String place;
    private  double mag;
    private long time;
    private String url;
    private String type;
    private double lat;
    private double lon;

    public EarthQuake(String place, double mag, long time, String url, String type, double lat, double lon) {
        this.place = place;
        this.mag = mag;
        this.time = time;
        this.url = url;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
    }

    public EarthQuake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getMag() {
        return mag;
    }

    public void setMag(double mag) {
        this.mag = mag;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
