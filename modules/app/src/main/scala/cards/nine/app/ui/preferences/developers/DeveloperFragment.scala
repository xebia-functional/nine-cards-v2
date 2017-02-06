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
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{CheckBoxPreference, Preference, PreferenceFragment}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class DeveloperFragment extends PreferenceFragment with Contexts[Fragment] with FindPreferences {

  lazy val dom = DeveloperDOM(this)

  lazy val preferencesJobs = new DeveloperJobs(new DeveloperUiActions(dom))

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach (_.setTitle(getString(R.string.developerPrefTitle)))
    addPreferencesFromResource(R.xml.preferences_dev)

    preferencesJobs.initialize().resolveAsync()

    dom.appsCategorizedPreferences.setOnPreferenceClickListener(new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        getFragmentManager
          .beginTransaction()
          .addToBackStack(AppsCategorized.name)
          .replace(android.R.id.content, new AppsListFragment)
          .commit()
        true
      }
    })

  }

}

case class DeveloperDOM(dom: FindPreferences) {

  def appsCategorizedPreferences = dom.find[Preference](AppsCategorized)
  def overrideBackendV2UrlPreference =
    dom.find[CheckBoxPreference](OverrideBackendV2Url)
  def backendV2UrlPreference       = dom.find[Preference](BackendV2Url)
  def androidTokenPreferences      = dom.find[Preference](AndroidToken)
  def deviceCloudIdPreferences     = dom.find[Preference](DeviceCloudId)
  def currentDensityPreferences    = dom.find[Preference](CurrentDensity)
  def probablyActivityPreference   = dom.find[Preference](ProbablyActivity)
  def headphonesPreference         = dom.find[Preference](Headphones)
  def locationPreference           = dom.find[Preference](Location)
  def weatherPreference            = dom.find[Preference](Weather)
  def restartApplicationPreference = dom.find[Preference](RestartApplication)
  def clearCacheImagesPreference   = dom.find[Preference](ClearCacheImages)
  def isStethoActivePreference     = dom.find[CheckBoxPreference](IsStethoActive)
  def isFlowUpActivePreference     = dom.find[CheckBoxPreference](IsFlowUpActive)

}
