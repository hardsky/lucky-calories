package com.hardskygames.luckycalories.list.models;

import com.google.common.collect.Ordering;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 29.04.16.
 */
public class OrderingCalorie extends Ordering<CalorieModel> {
    @Override
    public int compare(CalorieModel left, CalorieModel right) {
        if(left == right)
            return 0;

        int res = left.getEatTime().compareTo(right.getEatTime());
        if(res != 0)
            return res;

        res = left.getMeal().compareTo(right.getMeal());
        if(res != 0)
            return res;

        return Float.compare(left.getAmount(), right.getAmount());
    }
}
