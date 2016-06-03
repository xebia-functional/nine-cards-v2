package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.{CheckBoxPreference, ListPreference, Preference, PreferenceFragment}
import com.fortysevendeg.macroid.extras.PreferencesBuildingExtra._
import com.fortysevendeg.macroid.extras.RootPreferencesFragment
import com.fortysevendeg.ninecardslauncher.app.commons.{NineCardsPreferencesStatus, NumberOfAppsInHorizontalMoment, NumberOfRowsMoment, ShowBackgroundMoment}
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

class MomentsFragment
  extends PreferenceFragment
  with Contexts[PreferenceFragment] {

  lazy val status = new NineCardsPreferencesStatus

  implicit lazy val rootPreferencesFragment = new RootPreferencesFragment(this, R.xml.preferences_moments)

  lazy val numberOfAppsPreference = connect[ListPreference](NumberOfAppsInHorizontalMoment.name)

  lazy val numberOfRowsMomentPreference = connect[ListPreference](NumberOfRowsMoment.name)

  lazy val showBackgroundMomentPreference = connect[CheckBoxPreference](ShowBackgroundMoment.name)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.momentsPrefTitle)))

    numberOfAppsPreference foreach (onPreferenceChangeListener(_, status.setMoments(true)))

    numberOfRowsMomentPreference foreach (onPreferenceChangeListener(_, status.setMoments(true)))

    showBackgroundMomentPreference foreach (onPreferenceChangeListener(_, status.setMoments(true)))

  }

  private[this] def onPreferenceChangeListener(pref: Preference, f: => Unit) = {
    pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
        f
        true
      }
    })
  }

}
