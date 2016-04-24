package com.hardskygames.luckycalories.models;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 23.04.16.
 */
public class User {
    private String name;
    private String email;
    private String accessToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isLoggedIn(){
        return accessToken != null;
    }
}
