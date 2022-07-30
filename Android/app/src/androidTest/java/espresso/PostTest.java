package espresso;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321.f5.MainUI;
import com.cpen321.f5.PostActivity;
import com.cpen321.f5.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostTest
{
    @Rule
    public IntentsTestRule<PostActivity> intentsRule = new IntentsTestRule<>(PostActivity.class);


    @Test
    public void m8Test1(){
        onView(withId(R.id.post_cancel)).perform(ViewActions.click());
        onView(withId(R.id.post_button)).perform(ViewActions.click());
        intended(hasComponent(PostActivity.class.getName()));
    }

    @Test
    public void m8Test2(){
        onView(withId(R.id.post_post)).perform(ViewActions.click());
        onView(withText("Fail, Start Price is not set yet")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void m8Test3(){
        onView(withId(R.id.post_cancel)).perform(ViewActions.click());
        intended(hasComponent(MainUI.class.getName()));
    }

    @Test
    public void m8Test4(){
        onView(withId(R.id.post_title)).perform(typeText("title"));
        onView(withId(R.id.post_post)).perform(ViewActions.click());
        onView(withText("Fail, Start Price is not set yet")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void m8Test5(){
        onView(withId(R.id.post_start_price)).perform(ViewActions.click());
        onView(withId(R.id.post_start_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());
        onView(withId(R.id.post_post)).perform(scrollTo(), ViewActions.click());
        onView(withText("Fail, Deposit is not set yet")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void m8Test6(){
        onView(withId(R.id.post_start_price)).perform(ViewActions.click());
        onView(withId(R.id.post_start_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_deposit)).perform(ViewActions.click());
        onView(withId(R.id.post_deposit)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_post)).perform(scrollTo(), ViewActions.click());
        onView(withText("Fail, Step Price is not set yet")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void m8Test7(){
        onView(withId(R.id.post_start_price)).perform(ViewActions.click());
        onView(withId(R.id.post_start_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_deposit)).perform(ViewActions.click());
        onView(withId(R.id.post_deposit)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_step_price)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.post_step_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_post)).perform(scrollTo(), ViewActions.click());
        onView(withText("Fail, the post last hour is not set yet")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }


    @Test
    public void m8Test8(){
        onView(withId(R.id.post_start_price)).perform(ViewActions.click());
        onView(withId(R.id.post_start_price)).perform(pressKey(KeyEvent.KEYCODE_A), closeSoftKeyboard());
        onView(withId(R.id.post_start_price)).check(matches(withText("")));
    }

    @Test
    public void m8Test9(){

        onView(withId(R.id.post_start_price)).perform(ViewActions.click());
        onView(withId(R.id.post_start_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_deposit)).perform(ViewActions.click());
        onView(withId(R.id.post_deposit)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_step_price)).perform(scrollTo(), ViewActions.click());
        onView(withId(R.id.post_step_price)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_time_last)).perform(ViewActions.click());
        onView(withId(R.id.post_time_last)).perform(pressKey(KeyEvent.KEYCODE_9), closeSoftKeyboard());

        onView(withId(R.id.post_post)).perform(scrollTo(), ViewActions.click());
        intended(hasComponent(MainUI.class.getName()));
    }



}

