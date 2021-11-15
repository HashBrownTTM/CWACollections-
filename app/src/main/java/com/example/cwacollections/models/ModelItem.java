package com.example.cwacollections.models;

public class ModelItem {
    String uid, id, title, description, collectionId, url, dateObtained;
    long timestamp;

    //default constructor
    public ModelItem() {
    }

    //constructor with parameters
    public ModelItem(String uid, String id, String title, String description, String collectionId, String url, String dateObtained, long timestamp) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.collectionId = collectionId;
        this.url = url;
        this.dateObtained = dateObtained;
        this.timestamp = timestamp;
    }

    /*Getters and Setters*/

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDateObtained() {
        return dateObtained;
    }

    public void setDateObtained(String dateObtained) {
        this.dateObtained = dateObtained;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
