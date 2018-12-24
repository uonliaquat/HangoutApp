package com.example.uonliaquatarain.hangoutapp;

public class MemoryItem {

    String background;
    String profileName;
    String profilePhoto;
    String username;

    public MemoryItem(){

    }

    public MemoryItem(String background, String profileName, String profilePhoto, String username) {
        this.background = background;
        this.profileName = profileName;
        this.profilePhoto = profilePhoto;
        this.username = username;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
