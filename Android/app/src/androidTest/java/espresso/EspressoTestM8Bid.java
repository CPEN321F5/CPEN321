package espresso;

import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.*;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.startsWith;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.rule.IntentsTestRule;

//import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

//import com.cpen321.f5.MainActivity;
import com.cpen321.f5.BidActivity;
import com.cpen321.f5.ItemActivity;
import com.cpen321.f5.ItemListActivity;
import com.cpen321.f5.MainUI;
import com.cpen321.f5.PostActivity;
import com.cpen321.f5.ProfileActivity;
import com.cpen321.f5.R;
import com.cpen321.f5.SearchActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTestM8Bid
{
    @Rule
    public IntentsTestRule<MainUI> intentsRuleProfile = new IntentsTestRule<>(MainUI.class);

    @Test
    public void m8TestBid1()
    {
        goToBidActivity();

        intended(hasComponent(BidActivity.class.getName()));
    }

    @Test
    public void m8TestBid2_AND_3_AND_4()
    {
        goToBidActivity();

        onView(withId(R.id.add_button)).perform(ViewActions.click());
        onView(withId(R.id.to_add_price)).check(matches(withText("1")));

        onView(withId(R.id.sub_button)).perform(ViewActions.click());
        onView(withId(R.id.to_add_price)).check(matches(withText("0")));

        onView(withId(R.id.sub_button)).perform(ViewActions.click());
        onView(withId(R.id.sub_button)).perform(ViewActions.click());
        onView(withId(R.id.to_add_price)).check(matches(withText("0")));
    }


    @Test
    public void m8TestBid5()
    {
        goToBidActivity();

        onView(withId(R.id.add_button)).perform(ViewActions.click());
        onView(withId(R.id.to_add_price)).check(matches(withText("1")));

        intended(hasComponent(ItemActivity.class.getName()));
    }


    @Test
    public void m8TestBid6()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8TESTSEARCH"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(ViewActions.click());

        onView(withId(R.id.item_description_caption)).check(matches(withText("name" + "price")));
    }

    private void goToBidActivity(){
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8TESTSEARCH"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(ViewActions.click());
        onView(withId(R.id.bid_button)).perform(ViewActions.click());
    }


}
