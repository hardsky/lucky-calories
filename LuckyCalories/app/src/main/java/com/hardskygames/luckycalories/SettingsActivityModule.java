package com.hardskygames.luckycalories;

import com.hardskygames.luckycalories.list.CaloriesActivity;
import com.hardskygames.luckycalories.list.CaloriesFilterListFragment;
import com.hardskygames.luckycalories.list.CaloriesListFragment;
import com.hardskygames.luckycalories.list.EditCalorieFragment;

import dagger.Module;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 23.04.16.
 */
@Module(
        injects = {
                SettingsActivity.class,
        },
        addsTo = LuckyCaloriesAppModule.class,
        library = true
)
public class SettingsActivityModule {
    private final BaseActivity activity;

    public SettingsActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
