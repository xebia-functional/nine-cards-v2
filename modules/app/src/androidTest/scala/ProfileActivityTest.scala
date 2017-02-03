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

package nine.cards
package test

import android.support.test.runner.AndroidJUnit4
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert
import cards.nine.app.ui.profile.ProfileActivity
import scala.annotation.meta._

@RunWith(classOf[AndroidJUnit4])
class ProfileActivityTest {

  @Rule @beanGetter
  val activityRule: ActivityTestRule[ProfileActivity] = new ActivityTestRule(classOf[ProfileActivity])

  @Test
  def getActivity(): Unit = {
    Assert.assertNotNull(activityRule.getActivity())
    Assert.assertTrue(activityRule.getActivity().isInstanceOf[ProfileActivity])
  }
}