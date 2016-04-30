package com.hardskygames.luckycalories.calories.models;

import com.hardskygames.luckycalories.calories.ICalorieListItem;

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
    private float total;
    private List<IColorSubscriber> listeners = new ArrayList<>(10);

    private int currentColor = IColorSubscriber.NORMAl_COLOR;
    private List<CalorieModel> calories = new ArrayList<>(10);


    public DailyCalorie(float dailyNorm){
        this.dailyNorm = dailyNorm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTotal() {
        return total;
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
        this.total += calorieModel.getAmount();
        this.listeners.add(calorieModel);
        this.calories.add(calorieModel);

        if(total > dailyNorm && currentColor == IColorSubscriber.NORMAl_COLOR){
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
        this.total -= calorieModel.getAmount();
        this.listeners.remove(calorieModel);
        this.calories.remove(calorieModel);

        if(total <= dailyNorm && currentColor == IColorSubscriber.ALERT_COLOR){
            currentColor = IColorSubscriber.NORMAl_COLOR;
            for(IColorSubscriber sub: listeners){
                sub.notifyGreen();
            }
        }
    }

    private void notifyColor(){
        if(total > dailyNorm && currentColor == IColorSubscriber.NORMAl_COLOR){
            currentColor = IColorSubscriber.ALERT_COLOR;
            for(IColorSubscriber sub: listeners){
                sub.notifyRed();
            }

            return;
        }

        if(total <= dailyNorm && currentColor == IColorSubscriber.ALERT_COLOR){
            currentColor = IColorSubscriber.NORMAl_COLOR;
            for(IColorSubscriber sub: listeners){
                sub.notifyGreen();
            }
        }
    }

    public void change(){
        total = 0f;
        for(CalorieModel calorie: calories){
            total += calorie.getAmount();
        }

        notifyColor();
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
