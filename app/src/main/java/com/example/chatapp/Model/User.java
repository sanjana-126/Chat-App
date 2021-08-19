package com.example.chatapp.Model;

public class User {
    private String id;
    private String name;
    private String status;
    private String image;
    private String currentStatus;
    private String search;


    public User(String id, String name, String status, String image, String currentStatus, String search) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.image = image;
        this.currentStatus = currentStatus;
        this.search = search;

    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }


}
