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

package cards.nine.app.ui.preferences

import android.app.{ActionBar, Activity}
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{Preference, PreferenceActivity, PreferenceFragment}
import android.view.MenuItem
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.preferences.about.AboutFragment
import cards.nine.app.ui.preferences.analytics.AnalyticsFragment
import cards.nine.app.ui.preferences.animations.AnimationsFragment
import cards.nine.app.ui.preferences.appdrawer.AppDrawerFragment
import cards.nine.app.ui.preferences.commons._
import cards.nine.app.ui.preferences.developers.DeveloperFragment
import cards.nine.app.ui.preferences.lookandfeel.LookFeelFragment
import cards.nine.app.ui.preferences.moments.MomentsFragment
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

class NineCardsPreferencesActivity
    extends PreferenceActivity
    with PreferencesDOM
    with Contexts[Activity] {

  override lazy val actionBar: Option[ActionBar] = Option(getActionBar)

  lazy val ui = new PreferencesUiActions(this)

  lazy val jobs = new PreferencesJobs(ui)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    jobs.initialize().resolveAsync()
    getFragmentManager
      .beginTransaction()
      .replace(android.R.id.content, new NineCardsPreferenceFragment())
      .commit()
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean =
    item.getItemId match {
      case android.R.id.home =>
        super.onBackPressed()
        jobs.initializeActionBarTitle().resolveAsync()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }

  override def onBackPressed() = {
    super.onBackPressed()
    jobs.initializeActionBarTitle().resolveAsync()
  }

  def preferenceChanged(prefName: String): Unit =
    jobs.preferenceChanged(prefName).resolveAsync()

  class NineCardsPreferenceFragment extends PreferenceFragment {

    override def onCreate(savedInstanceState: Bundle) = {
      super.onCreate(savedInstanceState)

      if (IsDeveloper.readValue) {
        addPreferencesFromResource(R.xml.preferences_devs_headers)
        findPreference(DeveloperPreferences.name).setOnPreferenceClickListener(
          preferenceClick(DeveloperPreferences.name, new DeveloperFragment()))
      } else {
        addPreferencesFromResource(R.xml.preferences_headers)
      }

      findPreference(LookFeelPreferences.name).setOnPreferenceClickListener(
        preferenceClick(LookFeelPreferences.name, new LookFeelFragment()))

      findPreference(MomentsPreferences.name).setOnPreferenceClickListener(
        preferenceClick(MomentsPreferences.name, new MomentsFragment()))

      findPreference(AppDrawerPreferences.name).setOnPreferenceClickListener(
        preferenceClick(AppDrawerPreferences.name, new AppDrawerFragment()))

      findPreference(AnimationsPreferences.name).setOnPreferenceClickListener(
        preferenceClick(AnimationsPreferences.name, new AnimationsFragment()))

      findPreference(AnalyticsPreferences.name).setOnPreferenceClickListener(
        preferenceClick(AnalyticsPreferences.name, new AnalyticsFragment()))

      findPreference(WizardInlinePreferences.name).setOnPreferenceClickListener(
        preferenceActionClick(() => jobs.cleanWizardInlinePreferences().resolveAsync()))

      findPreference(AboutPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(AboutPreferences.name, new AboutFragment()))

      findPreference(FeedbackPreferences.name).setOnPreferenceClickListener(
        preferenceActionClick(() => ui.goToFeedback().resolveAsync()))

      findPreference(HelpPreferences.name).setOnPreferenceClickListener(preferenceActionClick(() =>
        ui.goToHelp().resolveAsync()))
    }

    private[this] def preferenceClick(key: String, fragment: PreferenceFragment) =
      new OnPreferenceClickListener {
        override def onPreferenceClick(preference: Preference): Boolean = {
          getFragmentManager
            .beginTransaction()
            .addToBackStack(key)
            .replace(android.R.id.content, fragment)
            .commit()
          true
        }
      }

    private[this] def preferenceActionClick(action: () => Unit) =
      new OnPreferenceClickListener {
        override def onPreferenceClick(preference: Preference): Boolean = {
          action()
          true
        }
      }

  }

}

trait PreferencesDOM {

  def actionBar: Option[ActionBar]

}
