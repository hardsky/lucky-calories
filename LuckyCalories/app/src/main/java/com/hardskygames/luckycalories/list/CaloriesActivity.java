package com.hardskygames.luckycalories.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.models.User;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CaloriesActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    User user;
    @Inject
    CaloriesListFragment caloriesListFragment;

    int[] menuTitles;

    private Fragment cur = null;
    private Drawer drawler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.menu_main_calories);
        setSupportActionBar(toolbar);

        menuTitles = new int[]{
                R.string.menu_main_calories,
                R.string.menu_main_filters,
                R.string.menu_main_settings
        };

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
                                .withName(menuTitles[0])
                                .withIcon(R.drawable.ic_assignment_black_24dp),
                        new PrimaryDrawerItem()
                                .withName(menuTitles[1])
                                .withIcon(R.drawable.ic_filter_list_black_24dp),
                        new PrimaryDrawerItem()
                                .withName(menuTitles[2])
                                .withIcon(R.drawable.ic_settings_black_24dp)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        toolbar.setTitle(menuTitles[position - 1]);
                        switch (position){
                            case 1:
                                createCaloriesScreen();
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            default:
                                return false;
                        }
                        drawler.closeDrawer();
                        return true;
                    }
                })
                .build();

        createCaloriesScreen();
    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new CaloriesActivityModule(this));
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
}
