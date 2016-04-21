package com.hardskygames.luckycalories;

import android.app.Application;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
@Module(
        injects = {
                LuckyCaloriesApp.class,
        },
        library = true
)public class LuckyCaloriesAppModule {
    private final LuckyCaloriesApp mApplication;

    public LuckyCaloriesAppModule(LuckyCaloriesApp application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Bus provideAppBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }
}