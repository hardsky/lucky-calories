package com.hardskygames.luckycalories.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hardskygames.luckycalories.BaseActivity;
import com.hardskygames.luckycalories.R;
import com.hardskygames.luckycalories.users.models.UserModel;
import com.mobandme.android.transformer.Transformer;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by HardSkyGames on 05.05.15.
 */
public class SettingsActivity extends BaseActivity {

    @Inject
    UserModel user;
    @Inject
    LuckyCaloriesApi api;

    private Transformer userTransformer = new Transformer
            .Builder()
            .build(io.swagger.client.model.User.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = this.getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);

        findViewById(R.id.settingsCalories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsCaloriesDialog dialog = new SettingsCaloriesDialog();
                dialog.setCalories(String.format(Locale.US, "%d", user.getDailyCalories()));
                dialog.setListener(new SettingsCaloriesDialog.OnSaveListener() {
                    @Override
                    public void onSave(String calorie) {
                        try {
                            user.setDailyCalories(Integer.parseInt(calorie));
                            api.updateUser(userTransformer.transform(user, User.class))
                                    .enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            if(!response.isSuccessful()){
                                                Timber.e("Error on saving settings: " + response.errorBody());
                                                return;
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<User> call, Throwable t) {
                                            Timber.e(t, "Error on saving settings.");
                                        }
                                    });

                        }
                        catch (NumberFormatException ex){
                            Timber.e(ex, "Error on setting daily calories.");
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "fragment_daily_calories");
            }
        });

    }

    @Override
    protected List<Object> getModules() {
        return Collections.<Object>singletonList(new SettingsActivityModule(this));
    }
}
