package espresso;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.cpen321.f5.MainActivity;
import com.cpen321.f5.MainUI;
import com.cpen321.f5.ProfileActivity;
import com.cpen321.f5.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest
{

    @Rule
    public IntentsTestRule<MainUI> intentsRule = new IntentsTestRule<>(MainUI.class);

    @Test
    public void m6ButtonTest()
    {
        onView(withId(R.id.profile_button)).perform(ViewActions.click());
        intended(hasComponent(ProfileActivity.class.getName()));
    }
}
