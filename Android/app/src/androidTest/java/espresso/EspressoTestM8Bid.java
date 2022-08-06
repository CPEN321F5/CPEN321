package espresso;

//import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.*;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

//import static org.hamcrest.Matchers.anything;
//import static org.hamcrest.Matchers.startsWith;

//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;

//import androidx.test.espresso.UiController;
//import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

//import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

//import com.cpen321.f5.MainActivity;
//import com.cpen321.f5.BidActivity;
import com.cpen321.f5.ItemActivity;
import com.cpen321.f5.ItemListActivity;
import com.cpen321.f5.MainUI;
//import com.cpen321.f5.PostActivity;
//import com.cpen321.f5.ProfileActivity;
import com.cpen321.f5.R;

//import org.hamcrest.Matcher;
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
    public void m8TestBid1() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("Boeing 787 airliner jet"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(100);
        intended(hasComponent(ItemActivity.class.getName()));
        Thread.sleep(5000);
        onView(withId(R.id.bid_button)).perform(ViewActions.click());
        onView(withText("bid price should be higher")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void m8TestBid2() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("Boeing 787 airliner jet"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(100);
        intended(hasComponent(ItemActivity.class.getName()));
        Thread.sleep(5000);
        onView(withId(R.id.item_price_down_button)).perform(ViewActions.click());
        onView(withId(R.id.bid_button)).perform(ViewActions.click());
        onView(withText("bid price should be higher")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void m8TestBid3() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("Boeing 787 airliner jet"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(100);
        intended(hasComponent(ItemActivity.class.getName()));
        Thread.sleep(5000);
        onView(withId(R.id.item_price_up_button)).perform(ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.upcoming_price)).check(matches(withText("5210000")));
    }

    @Test
    public void m8TestBid4() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("Boeing 787 airliner jet"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(100);
        intended(hasComponent(ItemActivity.class.getName()));
        Thread.sleep(5000);
        onView(withId(R.id.item_price_up_button)).perform(ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.item_price_up_button)).perform(ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.item_price_down_button)).perform(ViewActions.click());
        Thread.sleep(100);
        onView(withId(R.id.upcoming_price)).check(matches(withText("5210000")));
    }

    @Test
    public void m8TestBid5() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("Boeing 787 airliner jet"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(100);
        intended(hasComponent(ItemActivity.class.getName()));
        Thread.sleep(5000);
        onView(withId(R.id.item_price_up_button)).perform(ViewActions.click());
        onView(withId(R.id.bid_button)).perform(ViewActions.click());
        onView(withText("load your balance first")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }
}
