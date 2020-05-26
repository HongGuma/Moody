package com.example.phometalk.Items;

import android.graphics.drawable.Drawable;

public class UserItems {

    //private static String email;
    //private static String name;
    private String email;
    private String name;
    private String state;
    //private String photo;

    public UserItems(){}

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = this.email;
    }

    public String getName(){
        return name;
    }

    public void setName(String userName) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String userState) {
        this.state = userState;
    }
/*
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String userPhoto) {
        this.photo = userPhoto;
    }*/
}
