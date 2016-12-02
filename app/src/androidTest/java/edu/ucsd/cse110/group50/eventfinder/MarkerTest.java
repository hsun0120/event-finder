package edu.ucsd.cse110.group50.eventfinder;

/**
 * Created by Haoran Sun on 2016/11/30.
 * Simple Google Map marker tests
 */

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MarkerTest {
    private UiDevice device;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(getInstrumentation());

    }
    @Rule
    public ActivityTestRule<MapView> mActivityTestRule = new ActivityTestRule<>(MapView.class);

    /**
     * Given the user has already signed in,
     * When he or she switched to mapView page,
     * Then a marker showed his or her current location shows up.
     */
    @Test
    public void currentMarkerTest() {
        UiObject marker = device.findObject(new UiSelector().descriptionContains("You're Here"));
        try {
            marker.click(); //Click the marker to test if that marker exists
        } catch (UiObjectNotFoundException e) {
            fail("Marker not found!");
        }
    }

    /**
     * Given the user has already signed in and there is a event nearby called "Project Demo,"
     * when he or she switched to mapView page,
     * then markers will show up indicating the "Project Demo" event.
     */
    @Test
    public void nearbyMarkerTest() {
        //Switch to MyEvent
        ViewInteraction bottomBarTab = onView(
                allOf(withId(R.id.my_event_item),
                        withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                                withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                        isDisplayed()));
        bottomBarTab.perform(click());

        pressBack();

        //Switch to MapView to refresh current location
        ViewInteraction bottomBarTab2 = onView(
                allOf(withId(R.id.location_item),
                        withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                                withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                        isDisplayed()));
        bottomBarTab2.perform(click());

        //Check nearby event
        UiObject nearby =
                device.findObject(new UiSelector().descriptionContains("Project Demo"));
        try {
            nearby.click(); //Click to test whether the event "Project Demo" exists.
        } catch (UiObjectNotFoundException e) {
            fail("Marker not found!");
        }
    }
}
