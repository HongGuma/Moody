package com.example.Moody.Background.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;

@Entity(tableName = "ChatRoom")
public class ChatRoomEntity {
    @NonNull
    @PrimaryKey
    private String roomID; //채팅방 id
    private String users; //사용자 id
    private String lastMsg; //마지막 메세지
    private long lastTime; //마지막 시간
    private String roomName; //채팅방 이름
    private String msgCount; //안읽은 메시지 수
    private Boolean check = false; //채팅방 선택 여부 DB에 저장 x

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
