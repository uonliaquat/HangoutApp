package com.example.uonliaquatarain.hangoutapp;

import com.google.android.gms.maps.model.LatLng;

public class UpcomingEventModel {

    private int image;
    private String title;
    private String creator;
    private String place;
    private String date;
    private String time;
    private LatLng latLng;

    public UpcomingEventModel(int image, String title, String creator, String place, String date, String time, LatLng latLng) {
        this.image = image;
        this.title = title;
        this.creator = creator;
        this.place = place;
        this.date = date;
        this.time = time;
        this.latLng = latLng;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
