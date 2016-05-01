package com.hardskygames.luckycalories.calories;

import com.hardskygames.luckycalories.calories.models.IColorSubscriber;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public interface ICalorieListItem {
    int MEAL_CALORIE = 0;
    int DAILY_CALORIE = 1;

    int getType();

    void register(IColorSubscriber colorSubscriber);
    void unregister(IColorSubscriber colorSubscriber);
}