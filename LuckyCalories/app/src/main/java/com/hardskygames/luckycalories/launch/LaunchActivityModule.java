package com.hardskygames.luckycalories.launch;

import com.hardskygames.luckycalories.BaseActivity;

import dagger.Module;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
@Module(
        injects = {
                LaunchActivity.class,
                LoginFragment.class
        },
        addsTo = com.hardskygames.luckycalories.LuckyCaloriesAppModule.class,
        library = true
)
public class LaunchActivityModule {

    private final BaseActivity activity;

    public LaunchActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
