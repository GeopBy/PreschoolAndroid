package com.example.preschool.PhotoAlbum;


import java.util.ArrayList;

public class Album {
    private String name;
    private ArrayList<String> imageUrlList;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(ArrayList<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Album(String name, ArrayList<String> imageUrlList, String date) {
        this.name = name;
        this.imageUrlList = imageUrlList;
        this.date = date;
    }

    public Album() {
    }
}

