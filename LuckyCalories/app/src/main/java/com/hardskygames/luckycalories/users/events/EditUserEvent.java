package com.hardskygames.luckycalories.users.events;

import com.hardskygames.luckycalories.users.models.UserModel;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 30.04.16.
 */
public class EditUserEvent {
    public UserModel model;
    public int position;
}
