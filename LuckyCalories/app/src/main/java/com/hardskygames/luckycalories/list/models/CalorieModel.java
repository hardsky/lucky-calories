package com.hardskygames.luckycalories.list.models;

import com.hardskygames.luckycalories.list.ICalorieListItem;

import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 25.04.16.
 */
public class CalorieModel implements ICalorieListItem, IColorSubscriber {
    private long id = 0L;

    private String meal = null;

    private String note = null;

    private float amount =0f;

    private Date eatTime = null;
    private DailyCalorie daily;
    private IColorSubscriber colorSubscriber;
    private int currentColor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getEatTime() {
        return eatTime;
    }

    public void setEatTime(Date eatTime) {
        this.eatTime = eatTime;
    }


    public void setDaily(DailyCalorie daily) {
        this.daily = daily;
    }

    public void remove(){
        daily.remove(this);
        daily = null;
    }

    @Override
    public int getType() {
        return MEAL_CALORIE;
    }

    @Override
    public void register(IColorSubscriber colorSubscriber) {
        this.colorSubscriber = colorSubscriber;

        if(currentColor == IColorSubscriber.NORMAl_COLOR){
            colorSubscriber.notifyGreen();
            return;
        }

        colorSubscriber.notifyRed();
    }

    @Override
    public void unregister(IColorSubscriber colorSubscriber) {
        this.colorSubscriber = null;
    }

    @Override
    public void notifyGreen() {
        currentColor = IColorSubscriber.NORMAl_COLOR;
        if(colorSubscriber != null)
            colorSubscriber.notifyGreen();
    }

    @Override
    public void notifyRed() {
        currentColor = IColorSubscriber.ALERT_COLOR;
        if(colorSubscriber != null)
            colorSubscriber.notifyRed();
    }
}
