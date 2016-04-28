package com.hardskygames.luckycalories.list.models;

import com.hardskygames.luckycalories.list.ICalorieListItem;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public class DailyCalorie implements ICalorieListItem {

    private Date date;
    private float calories;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    @Override
    public int getType() {
        return DAILY_CALORIE;
    }

    public static boolean isNewDate(long prev, long next){
        Calendar cl1 = Calendar.getInstance();
        cl1.setTimeInMillis(prev);

        Calendar cl2 = Calendar.getInstance();
        cl2.setTimeInMillis(next);

        return cl1.get(Calendar.YEAR) > cl2.get(Calendar.YEAR)
                || cl1.get(Calendar.MONTH) > cl2.get(Calendar.MONTH)
                || cl2.get(Calendar.DAY_OF_MONTH) > cl2.get(Calendar.DAY_OF_MONTH);
    }
}
