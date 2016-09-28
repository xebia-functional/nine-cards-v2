package com.fortysevendeg.ninecardslauncher.app.ui.preferences

import android.app.{ActionBar, Activity}
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{Preference, PreferenceActivity, PreferenceFragment}
import android.view.MenuItem
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.about.AboutFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.animations.AnimationsFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.appdrawer.AppDrawerFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.developers.DeveloperFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.help.HelpFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.lookandfeel.LookFeelFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.moments.MomentsFragment
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts

class NineCardsPreferencesActivity
  extends PreferenceActivity
  with PreferencesDOM
  with Contexts[Activity] {

  override lazy val actionBar: Option[ActionBar] = Option(getActionBar)

  lazy val jobs = new PreferencesJobs(new PreferencesUiActions(this))

  lazy val nineCardsPreferences = new NineCardsPreferencesValue

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    jobs.initialize().resolveAsync()
    getFragmentManager.beginTransaction().replace(android.R.id.content, new NineCardsPreferenceFragment()).commit()
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
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

  class NineCardsPreferenceFragment
    extends PreferenceFragment {

    override def onCreate(savedInstanceState: Bundle) = {
      super.onCreate(savedInstanceState)

      if (IsDeveloper.readValue(nineCardsPreferences)) {
        addPreferencesFromResource(R.xml.preferences_devs_headers)
        findPreference(DeveloperPreferences.name).setOnPreferenceClickListener(preferenceClick(DeveloperPreferences.name, new DeveloperFragment()))
      } else {
        addPreferencesFromResource(R.xml.preferences_headers)
      }

      findPreference(LookFeelPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(LookFeelPreferences.name, new LookFeelFragment()))

      findPreference(MomentsPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(MomentsPreferences.name, new MomentsFragment()))

      findPreference(AppDrawerPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(AppDrawerPreferences.name, new AppDrawerFragment()))

      findPreference(AnimationsPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(AnimationsPreferences.name, new AnimationsFragment()))

      findPreference(AppInfoPreferences.name)
        .setOnPreferenceClickListener(preferenceActionClick(AboutPreferences.name, () => jobs.launchSettings().resolveAsync()))

      findPreference(AboutPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(AboutPreferences.name, new AboutFragment()))

      findPreference(HelpPreferences.name)
        .setOnPreferenceClickListener(preferenceClick(HelpPreferences.name, new HelpFragment()))
    }

    private[this] def preferenceClick(key: String, fragment: PreferenceFragment) = new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        getFragmentManager.beginTransaction().addToBackStack(key).replace(android.R.id.content, fragment).commit()
        true
      }
    }

    private[this] def preferenceActionClick(key: String, action: () => Unit) = new OnPreferenceClickListener {
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
