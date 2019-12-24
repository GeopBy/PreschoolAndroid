package com.example.preschool;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String fullnamefather,fullnamemother,fullnameteacher, address,phonenumber,username,idclass,classname,profileimage,userid,email,role,deleted;
    public ArrayList<String> myclass;

    public String getFullnamefather() {
        return fullnamefather;
    }

    public void setFullnamefather(String fullnamefather) {
        this.fullnamefather = fullnamefather;
    }

    public String getFullnamemother() {
        return fullnamemother;
    }

    public void setFullnamemother(String fullnamemother) {
        this.fullnamemother = fullnamemother;
    }

    public String getFullnameteacher() {
        return fullnameteacher;
    }

    public void setFullnameteacher(String fullnameteacher) {
        this.fullnameteacher = fullnameteacher;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdclass() {
        return idclass;
    }

    public void setIdclass(String idclass) {
        this.idclass = idclass;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public ArrayList<String> getMyclass() {
        return myclass;
    }

    public void setMyclass(ArrayList<String> myclass) {
        this.myclass = myclass;
    }

    public User(String fullnamefather, String fullnamemother, String fullnameteacher, String address, String phonenumber, String username, String idclass, String classname, String profileimage, String userid, String email, String role, String deleted, ArrayList<String> myclass) {
        this.fullnamefather = fullnamefather;
        this.fullnamemother = fullnamemother;
        this.fullnameteacher = fullnameteacher;
        this.address = address;
        this.phonenumber = phonenumber;
        this.username = username;
        this.idclass = idclass;
        this.classname = classname;
        this.profileimage = profileimage;
        this.userid = userid;
        this.email = email;
        this.role = role;
        this.deleted = deleted;
        this.myclass = myclass;
    }

    public User() {
    }
}
