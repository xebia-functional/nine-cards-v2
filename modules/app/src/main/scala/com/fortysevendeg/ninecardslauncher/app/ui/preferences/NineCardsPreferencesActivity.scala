package com.fortysevendeg.ninecardslauncher.app.ui.preferences

import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{Preference, PreferenceFragment, PreferenceActivity}
import android.view.MenuItem
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Remove
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments._
import com.fortysevendeg.ninecardslauncher2.R

class NineCardsPreferencesActivity
  extends PreferenceActivity {

  lazy val actionBar = Option(getActionBar)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    actionBar foreach{ab =>
      ab.setDisplayHomeAsUpEnabled(true)
      ab.setDisplayShowHomeEnabled(false)
      ab.setDisplayShowTitleEnabled(true)
      ab.setDisplayUseLogoEnabled(false)
    }
    getFragmentManager.beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit()
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = item.getItemId match {
    case android.R.id.home =>
      super.onBackPressed()
      actionBar foreach(_.setTitle(R.string.nineCardsSettingsTitle))
      true
    case _ =>
      super.onOptionsItemSelected(item)
  }

  override def onBackPressed() = {
    super.onBackPressed()
    actionBar foreach(_.setTitle(R.string.nineCardsSettingsTitle))
  }

  class MyPreferenceFragment
    extends PreferenceFragment {

    override def onCreate(savedInstanceState: Bundle) = {
      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preferences_headers)
      findPreference(DefaultLauncherPreferences.name).setOnPreferenceClickListener(preferenceClick(DefaultLauncherPreferences.name, new DefaultLauncherFragment()))
      findPreference(ThemesPreferences.name).setOnPreferenceClickListener(preferenceClick(ThemesPreferences.name, new ThemesFragment()))
      findPreference(AppDrawerPreferences.name).setOnPreferenceClickListener(preferenceClick(AppDrawerPreferences.name, new AppDrawerFragment()))
      findPreference(SizesPreferences.name).setOnPreferenceClickListener(preferenceClick(SizesPreferences.name, new SizesFragment()))
      findPreference(AnimationsPreferences.name).setOnPreferenceClickListener(preferenceClick(AnimationsPreferences.name, new AnimationsFragment()))
      findPreference(NewAppPreferences.name).setOnPreferenceClickListener(preferenceClick(NewAppPreferences.name, new NewAppFragment()))
      findPreference(AboutPreferences.name).setOnPreferenceClickListener(preferenceClick(AboutPreferences.name, new AboutFragment()))
      findPreference(HelpPreferences.name).setOnPreferenceClickListener(preferenceClick(HelpPreferences.name, new HelpFragment()))
    }

    private[this] def preferenceClick(key: String, fragment: PreferenceFragment) = new OnPreferenceClickListener {
      override def onPreferenceClick(preference: Preference): Boolean = {
        getFragmentManager.beginTransaction().addToBackStack(key).replace(android.R.id.content, fragment).commit()
        true
      }
    }

  }

}
