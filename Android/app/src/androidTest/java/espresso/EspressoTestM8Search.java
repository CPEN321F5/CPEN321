package espresso;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.*;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;

//import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

//import com.cpen321.f5.MainActivity;
import com.cpen321.f5.CategoryActivity;
import com.cpen321.f5.ItemActivity;
import com.cpen321.f5.ItemListActivity;
import com.cpen321.f5.MainUI;
import com.cpen321.f5.R;

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
        onView(withText("Fail, please type in something")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void m8TestSearch2() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("TEST ITEM"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
    }

    @Test
    public void m8TestSearch3() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("ITEM THAT DOES NOT EXIST"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withText("No result found, maybe you will be interested in:")));
    }

    @Test
    public void m8TestSearch4() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("desk"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
    }

    @Test
    public void m8TestSearch5() throws InterruptedException {
        onView(withId(R.id.search_bar)).perform(typeText("desk"));
        onView(withId(R.id.search_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(1000);
        intended(hasComponent(ItemActivity.class.getName()));
    }

    @Test
    public void m8TestSearch6() throws InterruptedException {
        onView(withId(R.id.clothes_button)).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(ItemListActivity.class.getName()));
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(1000);
        intended(hasComponent(ItemActivity.class.getName()));
        onView(withId(R.id.item_category_caption)).check(matches(withText("Category: Clothing")));
    }

    @Test
    public void m8TestSearch7() throws InterruptedException {
        onView(withId(R.id.main_ui_more)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.category_list)).atPosition(2).perform(ViewActions.click());
        Thread.sleep(100);
        intended(hasComponent(CategoryActivity.class.getName()));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(1000);
        intended(hasComponent(ItemActivity.class.getName()));
        onView(withId(R.id.item_category_caption)).check(matches(withText("Category: Clothing")));
    }

    @Test
    public void m8TestSearch8() throws InterruptedException {
        onView(withId(R.id.clothes_button)).perform(ViewActions.click());
        onView(withId(R.id.search_itemList_bar)).perform(typeText("desk"));
        onView(withId(R.id.search_itemList_button)).perform(ViewActions.click());
        onView(withId(R.id.search_hint)).check(matches(withHint("Here are your results:")));
        onView(withId(R.id.item_recyclerview)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Thread.sleep(1000);
        intended(hasComponent(ItemActivity.class.getName()));
    }
}
