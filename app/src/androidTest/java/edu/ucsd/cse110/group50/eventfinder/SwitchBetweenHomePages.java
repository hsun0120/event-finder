package edu.ucsd.cse110.group50.eventfinder;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

/**
 * Simple test to test switch between tabs
 * Given I am logged in as a user, when I want to check the event I created, I can switch to
 * MyEvent page; when I want to check my current location, then i can click a
 * button to mapView page.
 */
@RunWith(AndroidJUnit4.class)
public class SwitchBetweenHomePages {

    @Rule
    public ActivityTestRule<MapView> mActivityTestRule = new ActivityTestRule<>(MapView.class);

    @Test
    public void switchBetweenHomePages() {
        //Check MyEVent page is displayed
        ViewInteraction bottomBarTab = onView(
                allOf(withId(R.id.my_event_item),
                        withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                                withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                        isDisplayed()));
        bottomBarTab.perform(click());

        pressBack();

        //Check MapView is displayed
        ViewInteraction bottomBarTab2 = onView(
                allOf(withId(R.id.location_item),
                        withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                                withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                        isDisplayed()));
        bottomBarTab2.perform(click());
    }

}
