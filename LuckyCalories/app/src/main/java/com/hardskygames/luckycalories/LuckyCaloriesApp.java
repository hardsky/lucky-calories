package com.hardskygames.luckycalories;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
public class LuckyCaloriesApp extends Application {
    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        else{
            Timber.plant(new CrashReportingTree());
        }

        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new LuckyCaloriesAppModule(this));
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
