package com.hardskygames.luckycalories.launch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class LaunchActivity extends BaseActivity {

    @Inject
    LoginFragment loginFragment;

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
}
