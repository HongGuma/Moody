package com.example.Moody.Model;

public class UserModel {
    //사용자 정보
    private String email;
    private String name;
    private String password;
    private String birth;
    private String uID;
    private Boolean check;
    private String profile;

    public UserModel(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setUID(String uID) {
        this.uID = uID;
    }

    public String getUID() {
        return uID;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }
}
