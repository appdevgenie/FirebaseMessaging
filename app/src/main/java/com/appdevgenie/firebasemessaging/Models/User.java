package com.appdevgenie.firebasemessaging.Models;

public class User {

    private String name;
    private String user_id;
    private String token;

    public User() {
    }

    public User(String name, String user_id, String token) {
        this.name = name;
        this.user_id = user_id;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
