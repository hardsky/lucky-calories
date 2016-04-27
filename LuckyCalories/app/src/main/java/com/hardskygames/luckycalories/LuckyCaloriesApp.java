package com.hardskygames.luckycalories;

import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
public class LuckyCaloriesApp extends Application {
    private ObjectGraph applicationGraph;
    private boolean testMode = false;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        else{
            Timber.plant(new CrashReportingTree());
        }

        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);
    }

    @VisibleForTesting
    public void initTestMode(){
        testMode = true;
    }

    public boolean isTestMode(){
        return testMode;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new LuckyCaloriesAppModule(this));
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
