package com.hardskygames.luckycalories.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.calories.CaloriesFilterListFragment;
import com.hardskygames.luckycalories.calories.CaloriesListFragment;
import com.hardskygames.luckycalories.main.MainActivityModule;
import com.hardskygames.luckycalories.settings.SettingsActivity;
import com.hardskygames.luckycalories.users.UserListFragment;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.otto.Bus;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AdminActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    UserModel user;
    @Inject
    Bus bus;

    @Inject
    UserListFragment userListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.menu_main_users);
        setSupportActionBar(toolbar);

        createUsersScreen();
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new AdminActivityModule(this));
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

    private void createUsersScreen(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userListFragment);
        transaction.commit();
    }

}
