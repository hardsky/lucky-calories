package com.hardskygames.luckycalories.list.models;

import com.hardskygames.luckycalories.list.ICalorieListItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public class DailyCalorie implements ICalorieListItem {

    private final float dailyNorm;
    private Date date;
    private float calories;
    private List<IColorSubscriber> listeners = new ArrayList<>(10);

    private int currentColor = IColorSubscriber.NORMAl_COLOR;


    public DailyCalorie(float dailyNorm){
        this.dailyNorm = dailyNorm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCalories() {
        return calories;
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

    public void add(CalorieModel calorieModel) {
        calorieModel.setDaily(this);
        this.calories += calorieModel.getAmount();
        this.listeners.add(calorieModel);

        if(calories > dailyNorm && currentColor == IColorSubscriber.NORMAl_COLOR){
            currentColor = IColorSubscriber.ALERT_COLOR;
            for(IColorSubscriber sub: listeners){
                sub.notifyRed();
            }

            return;
        }

        if(currentColor == IColorSubscriber.NORMAl_COLOR){
            calorieModel.notifyGreen();
            return;
        }

        calorieModel.notifyRed();
    }

    public void remove(CalorieModel calorieModel){
        calorieModel.setDaily(null);
        this.calories -= calorieModel.getAmount();
        this.listeners.remove(calorieModel);

        if(calories <= dailyNorm && currentColor == IColorSubscriber.ALERT_COLOR){
            currentColor = IColorSubscriber.NORMAl_COLOR;
            for(IColorSubscriber sub: listeners){
                sub.notifyGreen();
            }
        }
    }

    @Override
    public int getType() {
        return DAILY_CALORIE;
    }

    @Override
    public void register(IColorSubscriber colorSubscriber) {
        this.listeners.add(colorSubscriber);

        if(currentColor == IColorSubscriber.NORMAl_COLOR)
            colorSubscriber.notifyGreen();
        else
            colorSubscriber.notifyRed();
    }

    @Override
    public void unregister(IColorSubscriber colorSubscriber) {
        this.listeners.remove(colorSubscriber);
    }
}
