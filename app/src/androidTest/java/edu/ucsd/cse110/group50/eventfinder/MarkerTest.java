package edu.ucsd.cse110.group50.eventfinder;

/**
 * Created by Haoran Sun on 2016/11/30.
 * Simple Google Map marker tests
 */

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
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MarkerTest {
    private UiDevice device;

    @Before
    public void setUp() {
        device = UiDevice.getInstance(getInstrumentation());

    }
    @Rule
    public ActivityTestRule<MapView> mActivityTestRule = new ActivityTestRule<>(MapView.class);

    @Test
    public void currentMarkerTest() {
        UiObject marker = device.findObject(new UiSelector().descriptionContains("You're Here"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            fail("Marker not found!");
        }
    }

    @Test
    public void nearbyEventMarkerTest() {
        //This event must exist before testing!
        UiObject marker =
                device.findObject(new UiSelector().descriptionContains("Project Demo"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            fail("Marker not found!");
        }
    }
}
