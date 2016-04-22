package com.hardskygames.luckycalories.mocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.client.api.LuckyCaloriesApi;
import io.swagger.client.model.Calorie;
import io.swagger.client.model.LoginInfo;
import io.swagger.client.model.SignUpInfo;
import io.swagger.client.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 22.04.16.
 */
public class MockLuckyCaloriesApi implements LuckyCaloriesApi {
    private final BehaviorDelegate<LuckyCaloriesApi> delegate;
    private final Map<String, User> users;

    public MockLuckyCaloriesApi(BehaviorDelegate<LuckyCaloriesApi> delegate){
        this.delegate = delegate;

        users = new HashMap<>(10);
    }

    @Override
    public Call<User> createUser(@Body User user) {
        return null;
    }

    @Override
    public Call<Calorie> createUserCalorie(@Path("id") Long id, @Body Calorie calorie) {
        return null;
    }

    @Override
    public Call<Void> deleteUser(@Path("id") Long id) {
        return null;
    }

    @Override
    public Call<Void> deleteUserCalorie(@Path("id") Long id, @Path("calorieId") Long calorieId) {
        return null;
    }

    @Override
    public Call<User> getUser(@Path("id") Long id) {
        return null;
    }

    @Override
    public Call<List<Calorie>> getUserCaloriesList(@Path("id") Long id, @Query("last") Long last) {
        return null;
    }

    @Override
    public Call<List<User>> getUserList(@Query("last") String last) {
        return null;
    }

    @Override
    public Call<String> login(@Body LoginInfo info) {
        return null;
    }

    @Override
    public Call<String> signup(@Body SignUpInfo info) {
        User user = new User();
        user.setEmail(info.getEmail());
        user.setName(info.getName());
        user.setUserType(1);

        users.put(user.getEmail(), user);

        String res = "access_token";
        return delegate.returningResponse(res).signup(info);
    }

    @Override
    public Call<User> updateUser(@Body User user) {
        return null;
    }

    @Override
    public Call<Calorie> updateUserCalorie(@Path("id") Long id, @Body Calorie calorie) {
        return null;
    }
}
