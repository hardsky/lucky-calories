package com.hardskygames.luckycalories;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hardskygames.luckycalories.list.CaloriesActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 27.04.16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CaloriesTest {

    @Rule
    public ActivityTestRule<CaloriesActivity> mActivityRule = new ActivityTestRule<>(CaloriesActivity.class);

    @Test
    public void afterSwipeRemoveItem(){

        String firstMeal = "Meat";

        onView(withId(R.id.progressBar))
                .check(matches(not(isDisplayed())));


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtMeal))
                .check(matches(withText(firstMeal)));

        onView(withId(R.id.listCalories))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));

        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtMeal))
                .check(matches(not(withText(firstMeal))));

    }

}
