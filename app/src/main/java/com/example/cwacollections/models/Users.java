package com.example.cwacollections.models;

public class Users {
    String fullName, email, uid;

    //constructor empty required for firebase
    public Users() {
    }

    //parametrized constructor
    public Users(String fullName, String email, String uid) {
        this.fullName = fullName;
        this.email = email;
        this.uid = uid;
    }

    /* Getters and Setters*/
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
