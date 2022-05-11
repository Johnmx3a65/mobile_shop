package com.parovsky.shop.model;

import java.util.List;
import java.util.Set;

public class User {

    private int id;

    private String name;

    private String mail;

    private String password;

    private String role;

    private String verifyCode;

    private List<Location> favoriteLocations;

    public User(int id, String name, String mail, String password, String role, String verifyCode, List<Location> favoriteLocations) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.role = role;
        this.verifyCode = verifyCode;
        this.favoriteLocations = favoriteLocations;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public List<Location> getFavoriteLocations() {
        return favoriteLocations;
    }

    public void setFavoriteLocations(List<Location> favoriteLocations) {
        this.favoriteLocations = favoriteLocations;
    }
}
