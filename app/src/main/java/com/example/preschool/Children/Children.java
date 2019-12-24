package com.example.preschool.Children;

public class Children {
    String birthday, name, sex ;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Children(String birthday, String name, String sex) {
        this.birthday = birthday;
        this.name = name;
        this.sex = sex;
    }

    public Children() {
    }
}
