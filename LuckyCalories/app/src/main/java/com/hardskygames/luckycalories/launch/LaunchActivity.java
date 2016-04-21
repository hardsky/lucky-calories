package com.hardskygames.luckycalories.launch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.launch.events.ShowSignUpScreen;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class LaunchActivity extends BaseActivity {

    @Inject
    LoginFragment loginFragment;
    @Inject
    SignUpFragment signUpFragment;
    @Inject
    Bus bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, loginFragment);
        transaction.commit();
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new LaunchActivityModule(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void onSignUpClick(ShowSignUpScreen ev){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(loginFragment);
        transaction.add(R.id.container, signUpFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
