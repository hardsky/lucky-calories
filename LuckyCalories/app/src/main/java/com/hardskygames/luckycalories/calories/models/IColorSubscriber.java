package com.hardskygames.luckycalories.calories.models;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 28.04.16.
 */
public interface IColorSubscriber {

    int NORMAl_COLOR = 1;
    int ALERT_COLOR = 2;

    void notifyGreen();
    void notifyRed();
}
