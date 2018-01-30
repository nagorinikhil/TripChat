package com.inclass.tripchat.Activity.POJO;

import java.util.ArrayList;

/**
 * Created by Nikhil on 21/04/2017.
 */

public class TripChat {
    String uid;
    ArrayList<Chat> chatArrayList;

    public TripChat() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Chat> getChatArrayList() {
        return chatArrayList;
    }

    public void setChatArrayList(ArrayList<Chat> chatArrayList) {
        this.chatArrayList = chatArrayList;
    }
}
