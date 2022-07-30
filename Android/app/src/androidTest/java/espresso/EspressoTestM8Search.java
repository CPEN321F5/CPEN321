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

import android.util.Log;
import android.widget.EditText;

import androidx.test.espresso.intent.rule.IntentsTestRule;

//import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

//import com.cpen321.f5.MainActivity;
import com.cpen321.f5.ItemActivity;
import com.cpen321.f5.ItemListActivity;
import com.cpen321.f5.MainUI;
import com.cpen321.f5.PostActivity;
import com.cpen321.f5.ProfileActivity;
import com.cpen321.f5.R;
import com.cpen321.f5.SearchActivity;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTestM8Search
{
    @Rule
    public IntentsTestRule<MainUI> intentsRuleProfile = new IntentsTestRule<>(MainUI.class);

    @Test
    public void m8TestSearch1()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(SearchActivity.class.getName()));
    }

    @Test
    public void m8TestSearch2()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(SearchActivity.class.getName()));
        onView(withText("Fail, please type in something")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void m8TestSearch3()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(ItemListActivity.class.getName()));
    }

    @Test
    public void m8TestSearch4()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("SOMEITEMTHATDOESNOTEXIST"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withText("No result found")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void m8TestSearch5()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8TESTSEARCH"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(ItemListActivity.class.getName()));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).check(matches(withText(startsWith("1659145691145"))));
    }

    @Test
    public void m8TestSearch6()
    {
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8TESTSEARCH"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        intended(hasComponent(ItemListActivity.class.getName()));
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(ViewActions.click());
        intended(hasComponent(ItemActivity.class.getName()));
    }

    @Test
    public void m8TestSearchLoadingTimeLessThanThree(){
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onView(withId(R.id.search_bar)).perform(typeText("M8TESTSEARCH"));
        long now = System.currentTimeMillis();
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.item_list)).atPosition(0).perform(ViewActions.click());
        long end = System.currentTimeMillis();

        Log.d("searchingTime", Long.toString(end - now) + "ms");
        Assert.assertTrue(lessThanThree(now, end));
    }

    boolean lessThanThree(long num1, long num2){
        if (num2 - num1 < 3000){
            return true;
        }
        return false;
    }
}
