package com.hardskygames.luckycalories.list.models;

import com.google.common.collect.Ordering;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 29.04.16.
 */
public class OrderingDailyCalorie extends Ordering<DailyCalorie> {
    @Override
    public int compare(DailyCalorie left, DailyCalorie right) {
        if(left == right)
            return 0;

        return left.getDate().compareTo(right.getDate());
    }
}
