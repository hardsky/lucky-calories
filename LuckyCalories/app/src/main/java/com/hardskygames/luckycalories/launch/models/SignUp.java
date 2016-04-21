package com.hardskygames.luckycalories.launch.models;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
public class SignUp extends Login {
    private String name;
    private String repeatedPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
