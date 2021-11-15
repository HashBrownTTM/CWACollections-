package com.example.cwacollections.models;

public class ModelCollection {
    //using the same spelling for model variables as in firebase database

    String id, collection, uid;
    long timestamp;
    int Goal;

    //constructor empty required for firebase
    public ModelCollection() {
    }

    //parametrized constructor
    public ModelCollection(String id, String collection, String uid, long timestamp, int goal) {
        this.id = id;
        this.collection = collection;
        this.uid = uid;
        this.timestamp = timestamp;
        this.Goal = goal;
    }

    /* Getters and Setters*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getGoal() {
        return Goal;
    }

    public void setGoal(int goal)  {
        this.Goal = goal;
    }
}
