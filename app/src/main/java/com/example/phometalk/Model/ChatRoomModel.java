package com.example.phometalk.Model;

import java.util.HashMap;

public class ChatRoomModel {
    //채팅방 정보
    private String roomID; //채팅방 id
    private HashMap<String,Boolean> users = new HashMap<String,Boolean>(); //사용자 id
    private String lastMsg; //마지막 메세지
    private String lastTime; //마지막 시간

    public void setUsers(HashMap<String, Boolean> users) {
        this.users = users;
    }

    public HashMap<String, Boolean> getUsers() {
        return users;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsg() {
        return lastMsg;
    }


}
