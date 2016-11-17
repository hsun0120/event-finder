package edu.ucsd.cse110.group50.eventfinder;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by Haoran Sun on 2016/11/17.
 */

@RunWith(AndroidJUnit4.class)
public class ListCheck {
    @Rule
    public ActivityTestRule<MapView> mActivityTestRule = new ActivityTestRule<>(MapView.class);

    @Test
    public void checkCards() {
        onView(withId(R.id.my_toolbar)).perform(click());
        onView(withId(R.id.container)).perform(click());
        onView(withText("Party"));
        onView(withText("Swims"));
    }
}
