package com.example.phometalk.Items;

import android.graphics.drawable.Drawable;

public class UserItems {

    private String userId;
    private String userName;
    private String userState;
    private String userPhoto;

    public String getUserId(){
        return userId;
    }

    public void setUserId(){
        this.userId =userId;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}
