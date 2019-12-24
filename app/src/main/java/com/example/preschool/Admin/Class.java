package com.example.preschool.Admin;

public class Class {
    String classname,teacher,year,room;

    public Class() {
    }

    public Class(String classname, String teacher) {
        this.classname = classname;
        this.teacher = teacher;
    }

    public Class(String classname, String teacher, String year, String room) {
        this.classname = classname;
        this.teacher = teacher;
        this.year = year;
        this.room = room;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
