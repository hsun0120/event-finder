package edu.ucsd.cse110.group50.eventfinder;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {

    @Rule
    public ActivityTestRule<MapView> mActivityTestRule = new ActivityTestRule<>(MapView.class);

    /**
     * Given the user has already signed in,
     * When he or she clicked on "create event button",
     * Then the create event page show up.
     *
     * Given that the create event page has shown up,
     * When he or she entered an event title
     * And selected an event date,
     * Then the view should reflect what he or she has entered and selected.
     */
    @Test
    public void createEventTest() {
        ViewInteraction bottomBarTab = onView(
                allOf(withId(R.id.list_item),
                        withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                                withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                        isDisplayed()));
        bottomBarTab.perform(click());

        ViewInteraction bottomBarTab2 = onView(allOf(withId(R.id.my_event_item),
                withParent(allOf(withId(R.id.bb_bottom_bar_item_container),
                        withParent(withId(R.id.bb_bottom_bar_outer_container)))),
                isDisplayed()));
        bottomBarTab2.perform(click());

        ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.action_add_event),
                withContentDescription("add event"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatEditText = onView(allOf(withId(R.id.eventName),
                withParent(allOf(withId(R.id.mainUI),
                        withParent(withId(R.id.activity_create_event)))), isDisplayed()));
        appCompatEditText.perform(replaceText("2234"), closeSoftKeyboard());


        ViewInteraction appCompatEditText2 = onView(allOf(withId(R.id.eventDescription),
                withParent(allOf(withId(R.id.mainUI),
                        withParent(withId(R.id.activity_create_event)))), isDisplayed()));
        appCompatEditText2.perform(replaceText("ggggggggg "), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(allOf(withId(R.id.doneButton),
                withText("Create event!"), withParent(allOf(withId(R.id.mainUI),
                        withParent(withId(R.id.activity_create_event)))), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatTextView = onView(allOf(withId(R.id.eventDate), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withClassName(is("android.support.v7.widget.AppCompatImageButton")),
                        withContentDescription("Next month"),
                        withParent(allOf(withClassName(is("android.widget.DayPickerView")),
                                withParent(withClassName(is
                                        ("com.android.internal.widget.DialogViewAnimator")))
                        )), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(allOf(withClassName(is
                ("android.support.v7.widget.AppCompatImageButton")),
                withContentDescription("Next month"),
                withParent(allOf(withClassName(is("android.widget.DayPickerView")),
                        withParent(withClassName(is
                                ("com.android.internal.widget.DialogViewAnimator"))))),
                isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatTextView3 = onView(allOf(withId(R.id.eventTime), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction appCompatButton4 = onView(allOf(withId(R.id.doneButton),
                withText("Create event!"), withParent(allOf(withId(R.id.mainUI),
                        withParent(withId(R.id.activity_create_event)))), isDisplayed()));
        appCompatButton4.perform(click());
    }

}