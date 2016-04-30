package com.hardskygames.luckycalories.main;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.calories.CaloriesFilterListFragment;
import com.hardskygames.luckycalories.calories.CaloriesListFragment;
import com.hardskygames.luckycalories.calories.EditCalorieFragment;

import dagger.Module;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 23.04.16.
 */
@Module(
        injects = {
                MainActivity.class,
                CaloriesListFragment.class,
                EditCalorieFragment.class,
                CaloriesFilterListFragment.class
        },
        addsTo = com.hardskygames.luckycalories.LuckyCaloriesAppModule.class,
        library = true
)
public class MainActivityModule {
    private final BaseActivity activity;

    public MainActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
