package com.hardskygames.luckycalories.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.settings.SettingsActivity;
import com.hardskygames.luckycalories.calories.CaloriesFilterListFragment;
import com.hardskygames.luckycalories.calories.CaloriesListFragment;
import com.hardskygames.luckycalories.calories.EditCalorieFragment;
import com.hardskygames.luckycalories.calories.events.AddCalorieEvent;
import com.hardskygames.luckycalories.users.models.AdminContext;
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
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    UserModel user;
    @Inject
    Bus bus;
    @Inject
    AdminContext adminContext;

    @Inject
    CaloriesListFragment caloriesListFragment;
    @Inject
    CaloriesFilterListFragment caloriesFilterListFragment;

    private Fragment cur = null;
    private Drawer drawler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.menu_main_calories);
        setSupportActionBar(toolbar);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.primary_dark)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail())//.withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        drawler = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_main_calories)
                                .withIcon(R.drawable.ic_assignment_black_24dp),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_main_filters)
                                .withIcon(R.drawable.ic_filter_list_black_24dp),
                        new PrimaryDrawerItem()
                                .withName(R.string.menu_main_settings)
                                .withIcon(R.drawable.ic_settings_black_24dp)
                                .withSetSelected(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1:
                                toolbar.setTitle(R.string.menu_main_calories);
                                createCaloriesScreen();
                                break;
                            case 2:
                                toolbar.setTitle(R.string.menu_main_filters);
                                createFilterScreen();
                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                break;
                            default:
                                return false;
                        }
                        drawler.closeDrawer();
                        return true;
                    }
                }).build();

        if(adminContext.getAdminModel() != null){
            drawler.addItem(new PrimaryDrawerItem()
                    .withName(R.string.menu_main_main_user)
                    .withIcon(R.drawable.ic_face_black_24dp)
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            UserModel admin = adminContext.getAdminModel();

                            user.setId(admin.getId());
                            user.setName(admin.getName());
                            user.setEmail(admin.getEmail());
                            user.setUserType(admin.getUserType());
                            user.setDailyCalories(admin.getDailyCalories());

                            adminContext.setAdminModel(null);

                            finish();
                            return true;
                        }
                    })
            );
        }

        createCaloriesScreen();
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new MainActivityModule(this));
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

    private void createCaloriesScreen(){
        if(cur == caloriesListFragment)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(cur != null){
            transaction.remove(cur);
        }
        transaction.add(R.id.container, caloriesListFragment);
        transaction.commit();

        cur = caloriesListFragment;
    }

    private void createFilterScreen(){
        if(cur == caloriesFilterListFragment)
            return;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(cur != null){
            transaction.remove(cur);
        }
        transaction.add(R.id.container, caloriesFilterListFragment);
        transaction.commit();

        cur = caloriesFilterListFragment;
    }

    @Subscribe
    public void onAddCalorieClick(AddCalorieEvent ev){
        FragmentManager fm = getSupportFragmentManager();
        EditCalorieFragment editCalorieDialog = new EditCalorieFragment();
        if(ev.model != null){
            editCalorieDialog.setModel(ev.model);
            ev.model = null;
        }
        editCalorieDialog.show(fm, "fragment_edit_name");
    }

}
