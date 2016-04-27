package com.hardskygames.luckycalories;

import android.app.Application;
import android.os.AsyncTask;

import com.hardskygames.luckycalories.mocks.MockLuckyCaloriesApi;
import com.hardskygames.luckycalories.models.User;
import com.squareup.otto.Bus;

import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.swagger.client.api.LuckyCaloriesApi;
import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

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

    @Provides
    @Singleton
    LuckyCaloriesApi provideApi() {
        //if(mApplication.isTestMode()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost:4003/api/v1/")
                    .build();

            // Create a MockRetrofit object with a NetworkBehavior which manages the fake behavior of calls.
            NetworkBehavior behavior = NetworkBehavior.create();
            MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
                    //.backgroundExecutor((ThreadPoolExecutor)AsyncTask.THREAD_POOL_EXECUTOR)
                    .networkBehavior(behavior)
                    .build();

            BehaviorDelegate<LuckyCaloriesApi> delegate = mockRetrofit.create(LuckyCaloriesApi.class);
            return new MockLuckyCaloriesApi(delegate);
        /*}
        else {
            throw new UnsupportedOperationException("Forgotten service for production.");
        }*/
    }

    @Provides
    @Singleton
    User userProvider(){
        return new User();
    }
}