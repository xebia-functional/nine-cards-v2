package com.fortysevendeg.ninecardslauncher.app.ui.preferences

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.{Preference, PreferenceActivity, PreferenceFragment}
import android.view.MenuItem
import com.fortysevendeg.ninecardslauncher.app.commons._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, ResultCodes, ResultData}
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts

class NineCardsPreferencesActivity
  extends PreferenceActivity
  with Contexts[Activity]
  with LauncherExecutor {

  lazy val actionBar = Option(getActionBar)

  private[this] var changedPreferences: Set[String] = Set.empty

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    actionBar foreach{ab =>
      ab.setDisplayHomeAsUpEnabled(true)
      ab.setDisplayShowHomeEnabled(false)
      ab.setDisplayShowTitleEnabled(true)
      ab.setDisplayUseLogoEnabled(false)
    }
    getFragmentManager.beginTransaction().replace(android.R.id.content, new NineCardsPreferenceFragment()).commit()
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

  def preferenceChanged(prefName: String): Unit = {
    changedPreferences = changedPreferences + prefName
    if (changedPreferences.nonEmpty) {
      val data = new Intent()
      data.putExtra(ResultData.preferencesResultData, changedPreferences.toArray)
      setResult(ResultCodes.preferencesChanged, data)
    }
  }

  class NineCardsPreferenceFragment
    extends PreferenceFragment {

    override def onCreate(savedInstanceState: Bundle) = {
      super.onCreate(savedInstanceState)
      addPreferencesFromResource(R.xml.preferences_headers)

      findPreference(LookFeelPreferences.name).setOnPreferenceClickListener(preferenceClick(LookFeelPreferences.name, new LookFeelFragment()))

      findPreference(MomentsPreferences.name).setOnPreferenceClickListener(preferenceClick(MomentsPreferences.name, new MomentsFragment()))

      findPreference(AppDrawerPreferences.name).setOnPreferenceClickListener(preferenceClick(AppDrawerPreferences.name, new AppDrawerFragment()))

      findPreference(AnimationsPreferences.name).setOnPreferenceClickListener(preferenceClick(AnimationsPreferences.name, new AnimationsFragment()))

      findPreference(NewAppPreferences.name).setOnPreferenceClickListener(preferenceClick(NewAppPreferences.name, new NewAppFragment()))

      findPreference(AppInfoPreferences.name).setOnPreferenceClickListener(preferenceActionClick(AboutPreferences.name, () => {
        launchSettings(getPackageName)
      }))

      findPreference(AboutPreferences.name).setOnPreferenceClickListener(preferenceClick(AboutPreferences.name, new AboutFragment()))

      findPreference(HelpPreferences.name).setOnPreferenceClickListener(preferenceClick(HelpPreferences.name, new HelpFragment()))
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
