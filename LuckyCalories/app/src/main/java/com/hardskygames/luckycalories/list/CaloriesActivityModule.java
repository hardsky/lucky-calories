package com.hardskygames.luckycalories.list;

import com.hardskygames.luckycalories.BaseActivity;

import dagger.Module;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 23.04.16.
 */
@Module(
        injects = {
                CaloriesActivity.class,
                CaloriesListFragment.class
        },
        addsTo = com.hardskygames.luckycalories.LuckyCaloriesAppModule.class,
        library = true
)
public class CaloriesActivityModule {
    private final BaseActivity activity;

    public CaloriesActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
