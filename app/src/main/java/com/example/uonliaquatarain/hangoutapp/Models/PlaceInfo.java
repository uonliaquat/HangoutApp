package com.example.uonliaquatarain.hangoutapp.Models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String name;
    private String address;
    private String PhoneNo;
    private String id;
    private Uri uri;
    private LatLng latLng;
    private float rating;
    private String attributes;

    public PlaceInfo(){

    }

    public PlaceInfo(String name, String address, String phoneNo, String id, Uri uri, LatLng latLng, float rating, String attributes) {
        this.name = name;
        this.address = address;
        PhoneNo = phoneNo;
        this.id = id;
        this.uri = uri;
        this.latLng = latLng;
        this.rating = rating;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", PhoneNo='" + PhoneNo + '\'' +
                ", id='" + id + '\'' +
                ", uri=" + uri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributes='" + attributes + '\'' +
                '}';
    }
}
