package com.hardskygames.luckycalories.users.models;

import com.hardskygames.luckycalories.users.models.UserModel;

/** Store admin user info during impersonating other users.
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 30.04.16.
 */
public class AdminContext {
    private UserModel adminModel;

    public UserModel getAdminModel() {
        return adminModel;
    }

    public void setAdminModel(UserModel adminModel) {
        this.adminModel = adminModel;
    }
}
