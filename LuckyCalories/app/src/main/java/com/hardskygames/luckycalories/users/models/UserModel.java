package com.hardskygames.luckycalories.users.models;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 23.04.16.
 */
public class UserModel {

    public static final int USER = 1;
    public static final int MANGER = 2;
    public static final int ADMIN = 3;

    private String name;
    private String email;
    private String accessToken;
    private long id;
    private int dailyCalories = 2200;
    private int userType = 1; //user

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }

    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public boolean isAdmin() {
        return userType == ADMIN;
    }

    public boolean isUser() {
        return userType == USER;
    }
}
