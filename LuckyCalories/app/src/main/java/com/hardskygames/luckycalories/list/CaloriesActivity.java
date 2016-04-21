package com.hardskygames.luckycalories.list;

import android.os.Bundle;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;

import java.util.List;

public class CaloriesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }
}
