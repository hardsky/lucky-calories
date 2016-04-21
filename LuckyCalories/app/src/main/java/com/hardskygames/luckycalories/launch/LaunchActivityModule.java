package com.hardskygames.luckycalories.launch;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.launch.models.Login;
import com.hardskygames.luckycalories.launch.models.SignUp;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
@Module(
        injects = {
                LaunchActivity.class,
                LoginFragment.class,
                SignUpFragment.class
        },
        addsTo = com.hardskygames.luckycalories.LuckyCaloriesAppModule.class,
        library = true
)
public class LaunchActivityModule {

    private final BaseActivity activity;

    public LaunchActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    Login provideLoginModel() {
        return new Login();
    }

    @Provides
    @Singleton
    SignUp provideSignUpModel() {
        return new SignUp();
    }
}
