package com.hardskygames.luckycalories.list.events;

import com.hardskygames.luckycalories.list.models.CalorieModel;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 27.04.16.
 */
public class EditCalorieEvent {
    public CalorieModel model;
    public Date origEatTime;
}
