package com.hardskygames.luckycalories.settings;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.LuckyCaloriesAppModule;

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
