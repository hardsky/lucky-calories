package com.hardskygames.luckycalories.common;

import com.google.common.base.Predicate;

import java.util.List;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 27.04.16.
 */
public final class Utils {
    public static <T> int addToSortedList(List<T> lst, T item, Predicate<T> predicate){
        int idx = 0;
        for(; idx < lst.size(); idx++){
            if(predicate.apply(lst.get(idx))){
                break;
            }
        }
        lst.add(idx, item);

        return idx;
    }
}
