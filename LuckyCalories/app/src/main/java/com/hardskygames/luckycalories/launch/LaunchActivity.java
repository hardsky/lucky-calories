package com.hardskygames.luckycalories.launch;

import android.os.Bundle;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;

import java.util.Collections;
import java.util.List;

public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new LaunchActivityModule(this));
    }
}
