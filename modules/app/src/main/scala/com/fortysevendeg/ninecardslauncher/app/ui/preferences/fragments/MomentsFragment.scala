package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.os.Bundle
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.{Preference, PreferenceFragment}
import com.fortysevendeg.ninecardslauncher.app.commons.{NineCardsPreferencesStatus, NumberOfAppsInHorizontalMoment, NumberOfRowsMoment, ShowBackgroundMoment}
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

class MomentsFragment
  extends PreferenceFragment
  with Contexts[PreferenceFragment] {

  lazy val status = new NineCardsPreferencesStatus

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    Option(getActivity.getActionBar) foreach(_.setTitle(getString(R.string.momentsPrefTitle)))

    addPreferencesFromResource(R.xml.preferences_moments)

    findPreference(NumberOfAppsInHorizontalMoment.name).
      setOnPreferenceChangeListener(onPreferenceChangeListener(status.setMoments(true)))

    findPreference(NumberOfRowsMoment.name).
      setOnPreferenceChangeListener(onPreferenceChangeListener(status.setMoments(true)))

    findPreference(ShowBackgroundMoment.name).
      setOnPreferenceChangeListener(onPreferenceChangeListener(status.setMoments(true)))

  }

  private[this] def onPreferenceChangeListener(f: => Unit) = {
    new OnPreferenceChangeListener {
      override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
        f
        true
      }
    }
  }

}
