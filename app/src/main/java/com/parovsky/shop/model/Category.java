package com.parovsky.shop.model;

import java.util.List;

public class Category {

    private Long id;

    private String name;

    private String picture;

    private List<Location> locations;

    public Category(Long id, String name, String picture, List<Location> locations) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.locations = locations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
}
