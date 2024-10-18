package com.example.android.models;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private String photoUrl;
    private String name;
    private Double price;
    private boolean available;
    private String description;

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public MenuItem(String photoUrl, String name, Double price, boolean available, String description) {
        this.photoUrl = photoUrl;
        this.name = name;
        this.price = price;
        this.available = available;
        this.description = description;
    }
}
