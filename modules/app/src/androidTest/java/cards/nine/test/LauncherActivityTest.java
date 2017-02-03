/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.test;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.ActivityTestRule;
import com.fortysevendeg.ninecardslauncher.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import cards.nine.app.ui.launcher.LauncherActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LauncherActivityTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(
            LauncherActivity.class);

    @Test
    public void getActivity() {
        Assert.assertNotNull(activityRule.getActivity());
        Assert.assertTrue(activityRule.getActivity() instanceof LauncherActivity);
    }

    @Test
    public void appDrawerShowed(){

        activityRule.getActivity();

        onView(withId(R.id.launcher_app_drawer)).perform(click());

        onView(withId(R.id.launcher_drawer_content)).check(matches(isCompletelyDisplayed()));
    }
}
