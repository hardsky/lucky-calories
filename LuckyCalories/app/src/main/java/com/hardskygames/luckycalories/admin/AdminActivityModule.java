package com.hardskygames.luckycalories.admin;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.users.EditUserFragment;
import com.hardskygames.luckycalories.users.UserListFragment;

import dagger.Module;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 30.04.16.
 */
@Module(
        injects = {
                AdminActivity.class,
                UserListFragment.class,
                EditUserFragment.class
        },
        addsTo = com.hardskygames.luckycalories.LuckyCaloriesAppModule.class,
        library = true
)
public class AdminActivityModule {
    private final BaseActivity activity;

    public AdminActivityModule(BaseActivity activity) {
        this.activity = activity;
    }
}
