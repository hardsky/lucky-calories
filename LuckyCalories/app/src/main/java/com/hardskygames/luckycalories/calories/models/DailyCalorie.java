package com.hardskygames.luckycalories.calories.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public class DailyCalorie {

    private Date date;
    private int total;

    private List<CalorieModel> calories = new ArrayList<>(10);

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void add(CalorieModel calorieModel) {
        calorieModel.setDaily(this);
        this.total += calorieModel.getAmount();
        this.calories.add(calorieModel);
    }

    public void remove(CalorieModel calorieModel){
        calorieModel.setDaily(null);
        this.total -= calorieModel.getAmount();
        this.calories.remove(calorieModel);
    }

    public void change(){
        total = 0;
        for(CalorieModel calorie: calories){
            total += calorie.getAmount();
        }
    }

    //should be consistent with comparator
    //see http://google.github.io/guava/releases/snapshot/api/docs/com/google/common/collect/TreeMultimap.html
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(! (o instanceof DailyCalorie))
            return false;

        DailyCalorie other = (DailyCalorie)o;
        return date.equals(other.date);
    }
}
