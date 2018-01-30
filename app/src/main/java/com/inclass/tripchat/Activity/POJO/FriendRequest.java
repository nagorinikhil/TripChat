package com.inclass.tripchat.Activity.POJO;

/**
 * Created by Nikhil on 19/04/2017.
 */

public class FriendRequest {

    String reqId, sender, reciever;

    public FriendRequest() {
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }
}
