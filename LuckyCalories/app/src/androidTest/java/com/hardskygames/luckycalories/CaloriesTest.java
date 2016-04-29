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
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
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

        String firstMeal = "Beer";

        onView(withId(R.id.progressBar))
                .check(matches(not(isDisplayed())));


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(1, R.id.txtMeal))
                .check(matches(withText(firstMeal)));

        onView(withId(R.id.listCalories))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeLeft()));

        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(1, R.id.txtMeal))
                .check(matches(not(withText(firstMeal))));

    }

    @Test
    public void afterSwipeTotalReduced(){

        String totalBefore = "1200 kcal";
        String totalAfter = "1000 kcal";

        onView(withId(R.id.progressBar))
                .check(matches(not(isDisplayed())));


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtTotal))
                .check(matches(withText(totalBefore)));

        onView(withId(R.id.listCalories))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeLeft()));

        try {
            Thread.sleep(500, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtTotal))
                .check(matches(withText(totalAfter)));

    }

    @Test
    public void whenChangeItemCalorieAmountThanTotalChanged(){
        onView(withId(R.id.progressBar))
                .check(matches(not(isDisplayed())));


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(1, R.id.txtMeal))
                .perform(longClick());

        onView(withId(R.id.txtKCal))
                .perform(clearText())
                .perform(typeText("220"));

        onView(withId(R.id.save))
                .perform(click());

        try {
            Thread.sleep(500, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtTotal))
                .check(matches(withText("1220 kcal")));
    }

    @Test
    public void afterAddCalorieTotalIncreased(){

        String totalBefore = "1200 kcal";
        String totalAfter = "1400 kcal";

        onView(withId(R.id.progressBar))
                .check(matches(not(isDisplayed())));


        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtTotal))
                .check(matches(withText(totalBefore)));

        onView(withId(R.id.btnAdd))
                .perform(click());

        onView(withId(R.id.txtMeal))
                .perform(typeText("test"));

        onView(withId(R.id.txtKCal))
                .perform(clearText())
                .perform(typeText("200"));

        onView(withId(R.id.save))
                .perform(click());

        try {
            Thread.sleep(500, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(new RecyclerViewMatcher(R.id.listCalories)
                .atPositionOnView(0, R.id.txtTotal))
                .check(matches(withText("1400 kcal")));

    }

}
