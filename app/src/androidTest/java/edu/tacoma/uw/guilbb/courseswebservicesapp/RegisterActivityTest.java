package edu.tacoma.uw.guilbb.courseswebservicesapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.tacoma.uw.guilbb.courseswebservicesapp.model.RegisterActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
            RegisterActivity.class);


    @Test
    public void testLoginInvalidEmail() {
        // Type text and then press the button.
        onView(withId(R.id.add_email))
                .perform(typeText("mmuppauw.edu"));
        onView(withId(R.id.add_password))
                .perform(typeText("test1@#"));
        onView(withId(R.id.btn_register))
                .perform(click());

        onView(withText("Enter valid email address"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().
                        getWindow().getDecorView())))).check(matches(isDisplayed()));

    }

    @Test
    public void testRegisterInvalidPassword() {
        // Type text and then press the button.

        onView(withId(R.id.add_email))
                .perform(typeText("mmuppa@uw.edu"));
        onView(withId(R.id.add_password))
                .perform(typeText(""));
        onView(withId(R.id.btn_register))
                .perform(click());

        onView(withText("Enter a valid password (at least 6 characters)"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));

    }


}