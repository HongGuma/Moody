package com.example.phometalk.Items;

import android.graphics.drawable.Drawable;

public class UserItems {

    private String email;
    private String name;
    private String password;
    private String birth;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String userState) {
        this.password = userState;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String userPhoto) {
        this.birth = userPhoto;
    }
}
