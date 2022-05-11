package com.parovsky.shop.model;

public class Location {

    private Long id;

    private String name;

    private String subtitle;

    private String picture;

    private String description;

    private String coordinates;

    public Location(Long id, String name, String picture, String subtitle, String description, String coordinates) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.subtitle = subtitle;
        this.description = description;
        this.coordinates = coordinates;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
