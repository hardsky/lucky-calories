package com.hardskygames.luckycalories.list;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public interface ICalorieListItem {
    int MEAL_CALORIE = 0;
    int DAILY_CALORIE = 1;

    int getType();
}
