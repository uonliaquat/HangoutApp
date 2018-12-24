package com.example.uonliaquatarain.hangoutapp;

import com.google.android.gms.maps.model.LatLng;

public class EventRequestItem {
    private String title;
    private int image;
    private LatLng latLng;
    private String event_id;

    public EventRequestItem(LatLng latLng, String title, int image, String event_id) {
        this.title = title;
        this.image = image;
        this.latLng = latLng;
        this.event_id = event_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
