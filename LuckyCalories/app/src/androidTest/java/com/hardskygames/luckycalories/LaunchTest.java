package com.hardskygames.luckycalories;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.hardskygames.luckycalories.launch.LaunchActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by Nikolay Mihailov <hardsky@yandex.ru>  on 21.04.16.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LaunchTest {
    @Rule
    public ActivityTestRule<LaunchActivity> mActivityRule = new ActivityTestRule<>(LaunchActivity.class);

    @Test
    public void afterClickSignUpBtnMovedToSignUpScreen(){

        onView(withId(R.id.btnSignUp))
                .perform(click());

        //check, that we moved on 'sign-up' screen
        onView(withId(R.id.txtRepeatPassword))
                .check(matches(withHint(R.string.fragment_sign_up_repeat_password)));
    }

    @Test
    public void afterSignUpShowCaloriesScreen(){

        onView(withId(R.id.btnSignUp))
                .perform(click());

        onView(withId(R.id.btnSignUp))
                .perform(click());

        //check, that we moved on 'calories' screen
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(R.string.menu_main_calories)));
    }
}
