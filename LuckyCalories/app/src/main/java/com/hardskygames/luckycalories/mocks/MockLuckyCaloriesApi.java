package com.hardskygames.luckycalories.mocks;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    private final ArrayList<Calorie> calories;

    public MockLuckyCaloriesApi(BehaviorDelegate<LuckyCaloriesApi> delegate){
        this.delegate = delegate;

        users = new HashMap<>(10);


        calories = new ArrayList<>(10);
        setCalories();
        setUsers();
    }

    private void setUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("Test1");
        user.setEmail("test1@test.test");
        user.setDailyCalories(2200f);
        user.setUserType(1);
        users.put(user.getEmail(), user);

        user = new User();
        user.setId(2L);
        user.setName("Test2");
        user.setEmail("test2@test.test");
        user.setDailyCalories(2500f);
        user.setUserType(1);
        users.put(user.getEmail(), user);

        user = new User();
        user.setId(3L);
        user.setName("Test3");
        user.setEmail("test3@test.test");
        user.setDailyCalories(2800f);
        user.setUserType(1);
        users.put(user.getEmail(), user);

        user = new User();
        user.setId(4L);
        user.setName("Test4");
        user.setEmail("test4@test.test");
        user.setDailyCalories(3000f);
        user.setUserType(1);
        users.put(user.getEmail(), user);

    }

    private void setCalories(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 14);

        calendar.set(Calendar.HOUR_OF_DAY, 22);
        Calorie calorie = new Calorie();
        calorie.setMeal("Meat");
        calorie.setAmount(400f);
        calorie.setEatTime(calendar.getTimeInMillis());
        calorie.setNote("Test1 Test");
        calorie.setId(1L);
        calories.add(calorie);

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calorie = new Calorie();
        calorie.setMeal("Milk");
        calorie.setAmount(200f);
        calorie.setEatTime(calendar.getTimeInMillis());
        calorie.setNote("Test2 Test");
        calorie.setId(2L);
        calories.add(calorie);

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calorie = new Calorie();
        calorie.setMeal("Eggs");
        calorie.setAmount(300f);
        calorie.setEatTime(calendar.getTimeInMillis());
        calorie.setNote("Test3 Test");
        calorie.setId(3L);
        calories.add(calorie);

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calorie = new Calorie();
        calorie.setMeal("Bred with butter");
        calorie.setAmount(100f);
        calorie.setEatTime(calendar.getTimeInMillis());
        calorie.setNote("Test4 Test");
        calorie.setId(4L);
        calories.add(calorie);

        calendar.add(Calendar.HOUR_OF_DAY, -2);
        calorie = new Calorie();
        calorie.setMeal("Beer");
        calorie.setAmount(200f);
        calorie.setEatTime(calendar.getTimeInMillis());
        calorie.setNote("Test5 Test");
        calorie.setId(5L);
        calories.add(calorie);
    }

    @Override
    public Call<User> createUser(@Body User user) {
        users.put(user.getEmail(), user);
        user.setId((long)users.size());
        return delegate.returningResponse(user).createUser(user);
    }

    @Override
    public Call<Calorie> createUserCalorie(@Path("id") Long id, @Body Calorie calorie) {
        calorie.setId((long)calories.size());

        boolean inserted = false;
        for(int i = 0; i < calories.size(); i++){
            if(calorie.getEatTime() > calories.get(i).getEatTime()){
                inserted = true;
                calories.add(i, calorie);
                break;
            }

        }
        if(!inserted){
            calories.add(calories.size(), calorie);
        }

        return delegate.returningResponse(calorie).createUserCalorie(id, calorie);
    }

    @Override
    public Call<List<Calorie>> getUserCaloriesFilter(@Path("id") Long id, @Query("last") final Long last, @Query("fromDate") Long fromDate, @Query("toDate") Long toDate, @Query("fromTime") Long fromTime, @Query("toTime") Long toTime) {
        List<Calorie> res;
        if(last == 0){
            res = calories;
        }
        else {
            res = new ArrayList<>(Collections2.filter(calories, new Predicate<Calorie>() {
                @Override
                public boolean apply(Calorie input) {
                    return input.getEatTime() < last;
                }
            }));
        }

        return delegate.returningResponse(res).getUserCaloriesFilter(id, last, fromDate, toDate, fromTime, toTime);
    }

    @Override
    public Call<Void> deleteUser(@Path("id") final Long id) {
        Iterables.removeIf(users.values(), new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.getId().equals(id);
            }
        });
        return delegate.returningResponse(null).deleteUser(id);
    }

    @Override
    public Call<Void> deleteUserCalorie(@Path("id") Long id, @Path("calorieId") final Long calorieId) {
        Iterables.removeIf(calories, new Predicate<Calorie>() {
            @Override
            public boolean apply(Calorie input) {
                return input.getId().equals(calorieId);
            }
        });
        return delegate.returningResponse(null).deleteUserCalorie(id, calorieId);
    }

    @Override
    public Call<User> getUser(@Path("id") Long id) {
        return null;
    }

    @Override
    public Call<List<Calorie>> getUserCaloriesList(@Path("id") Long id, @Query("last") final Long last) {
        List<Calorie> res;
        if(last == 0){
            res = calories;
        }
        else {
            res = new ArrayList<>(Collections2.filter(calories, new Predicate<Calorie>() {
                @Override
                public boolean apply(Calorie input) {
                    return input.getEatTime() < last;
                }
            }));
        }

        return delegate.returningResponse(res).getUserCaloriesList(id, last);
    }

    @Override
    public Call<List<User>> getUserList(@Query("last") String last) {

        return delegate.returningResponse(last == null ? new ArrayList<>(users.values()): new ArrayList<>()).getUserList(last);
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
        user.setDailyCalories(2200f);
        user.setUserType(1);

        users.put(user.getEmail(), user);
        user.setId((long)users.size());

        String res = "access_token";
        return delegate.returningResponse(res).signup(info);
    }

    @Override
    public Call<User> updateUser(@Body User user) {
        return delegate.returningResponse(user).updateUser(user);
    }

    @Override
    public Call<Calorie> updateUserCalorie(@Path("id") Long id, @Body Calorie calorie) {
        final Calorie tmp = calorie;
        Calorie existed = Iterables.find(calories, new Predicate<Calorie>() {
            @Override
            public boolean apply(Calorie input) {
                return tmp.getId().equals(input.getId());
            }
        });
        existed.setMeal(calorie.getMeal());
        existed.setAmount(calorie.getAmount());
        existed.setEatTime(calorie.getEatTime());
        existed.setNote(calorie.getNote());

        Collections.sort(calories, new Ordering<Calorie>() {
            @Override
            public int compare(Calorie left, Calorie right) {
                if(left.getEatTime() > right.getEatTime())
                    return 1;
                else if(left.getEatTime() < right.getEatTime())
                    return -1;
                return 0;
            }
        });

        return delegate.returningResponse(existed).updateUserCalorie(id, calorie);
    }
}
