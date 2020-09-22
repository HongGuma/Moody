package com.example.Moody.Background.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity(tableName = "FriendInfo")
public class UserEntity {
    //친구 추가된 사용자 정보
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int num;
    private String uID; //사용자 id
    private String email; // 사용자 이메일
    private String name; //사용자 이름
    private String password; //사용자 비밀번호
    private String birth; //사용자 생일
    private String profile; //사용자 프로필
    private String range;// 프로필 공개 여부
    private Boolean connection; //사용자 접속 여부

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUID() {
        return uID;
    }

    public void setUID(String UID) {
        this.uID = UID;
    }

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Boolean getConnection() {
        return connection;
    }

    public void setConnection(Boolean connection) {
        this.connection = connection;
    }

}
