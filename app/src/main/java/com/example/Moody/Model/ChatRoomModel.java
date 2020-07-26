package com.example.Moody.Model;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomModel {
    //채팅방 정보
    private String roomID; //채팅방 id
    private HashMap<String,Boolean> users = new HashMap<String,Boolean>(); //사용자 id
    private String lastMsg; //마지막 메세지
    private Object lastTime; //마지막 시간
    private String roomName; //채팅방 이름
    private String msgCount; //안읽은 메시지 수

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

    public void setLastTime(Object lastTime) {
        this.lastTime = lastTime;
    }

    public Object getLastTime() {
        return lastTime;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getMsgCount() {
        return msgCount;
    }
}