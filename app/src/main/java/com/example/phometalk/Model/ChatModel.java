package com.example.phometalk.Model;

public class ChatModel {
    //채팅 정보
    private String userName; //유저 이름
    private String msg; //메세지 내용
    private String uID; //유저 아이디
    private String timestamp; //작성 시간
    private String msgType; //메세지 타입


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setUID(String userUID) {
        this.uID = userUID;
    }

    public String getUID() {
        return uID;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgType() {
        return msgType;
    }
}
