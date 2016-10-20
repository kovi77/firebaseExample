package com.example.ak.firebaseauthdemo;

/**
 * Created by Ak on 2016.10.16..
 */

public class UserInformation {
    public String name;
    public int age;
    public String imageUrl;

    public UserInformation(){
        this.name = "default";
        this.age = 0;
        this.imageUrl = "default";

    }
    public UserInformation(String name,int age){
        this.name = name;
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
