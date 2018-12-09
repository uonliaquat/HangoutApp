package com.example.uonliaquatarain.hangoutapp;

public class User {

    private String NAME;
    private String USERNAME;
    private String PHOTO;

    public User(String NAME, String USERNAME, String PHOTO) {

        this.NAME = NAME;
        this.USERNAME = USERNAME;
        this.PHOTO = PHOTO;
    }

    public String getNAME() {
        return NAME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getPHOTO() {
        return PHOTO;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public void setPHOTO(String PHOTO) {
        this.PHOTO = PHOTO;
    }
}
