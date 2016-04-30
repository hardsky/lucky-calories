package com.hardskygames.luckycalories.calories.events;

import com.hardskygames.luckycalories.calories.models.CalorieModel;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 27.04.16.
 */
public class EditCalorieEvent {
    public CalorieModel model;
    public Date origEatTime;
}
