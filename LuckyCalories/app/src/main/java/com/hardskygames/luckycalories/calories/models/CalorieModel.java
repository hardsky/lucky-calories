package com.hardskygames.luckycalories.calories.models;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 25.04.16.
 */
public class CalorieModel {
    private long id = 0L;

    private String meal = null;

    private String note = null;

    private int amount =0;

    private Date eatTime = null;
    private DailyCalorie daily;
    private Date eatDate;

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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;

        if(daily != null){
            daily.change();
        }
    }

    public Date getEatTime() {
        return eatTime;
    }

    public void setEatTime(Date eatTime) {
        this.eatTime = eatTime;

        Calendar cl = Calendar.getInstance();
        cl.setTime(eatTime);
        cl.set(Calendar.HOUR_OF_DAY, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);

        this.eatDate = cl.getTime();
    }


    public void setDaily(DailyCalorie daily) {
        this.daily = daily;
    }

    //should be consistent with comparator
    //see http://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/TreeMultimap.html
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(! (o instanceof CalorieModel))
            return false;

        CalorieModel other = (CalorieModel)o;
        return meal.equals(other.meal)
                && Math.abs(amount- other.amount) < 0.01
                && eatTime.equals(other.eatTime);
    }

    /**
     * return eat time without hours, minutes etc.
     * allow to link with eat day
     */
    public Date getEatDate(){
        return eatDate;
    }
}
