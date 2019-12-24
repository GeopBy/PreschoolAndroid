package com.example.preschool.TimeLine;

import java.util.ArrayList;

public class Posts {
    private String uid, time, date, description, profileimage, fullname;
    private ArrayList<String> postimage;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public ArrayList<String> getPostimage() {
        return postimage;
    }

    public void setPostimage(ArrayList<String> postimage) {
        this.postimage = postimage;
    }

    public Posts() {
    }

    public Posts(String uid, String time, String date, String description, String profileimage, String fullname, ArrayList<String> postimage) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.description = description;
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.postimage = postimage;
    }
}