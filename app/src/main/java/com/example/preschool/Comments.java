package com.example.preschool;

public class Comments {
    public String comment, date, time, username, avatar;

    public Comments(String comment, String date, String time, String username, String avatar) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
        this.avatar = avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Comments() {
    }
}
