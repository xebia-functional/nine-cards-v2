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

package cards.nine.app.ui.preferences.developers

import android.app.Fragment
import android.os.Bundle
import android.preference.{PreferenceCategory, PreferenceFragment}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class AppsListFragment extends PreferenceFragment with Contexts[Fragment] with FindPreferences {

  lazy val dom = AppsListDOM(this)

  lazy val appsListJobs = new AppsListJobs(new AppsListUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach (_.setTitle(getString(R.string.devAppsListTitle)))
    addPreferencesFromResource(R.xml.preferences_apps_list)

    appsListJobs.initialize().resolveAsync()
  }

}

case class AppsListDOM(dom: FindPreferences) {

  def appsListPreferenceCategory =
    dom.findByName[PreferenceCategory]("appsList")
}
