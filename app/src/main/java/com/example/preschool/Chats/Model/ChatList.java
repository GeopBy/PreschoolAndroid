package com.example.preschool.Chats.Model;

import java.util.ArrayList;

public class ChatList {
    private ArrayList<String> uidList;

    public ChatList() {
    }

    public ArrayList<String> getUidList() {
        return uidList;
    }

    public void setUidList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }

    public ChatList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }
}
