package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceFragment
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.NineCardsPreferencesActivity

trait PreferenceChangeListenerFragment
  extends PreferenceFragment
  with OnSharedPreferenceChangeListener {

  override def onResume(): Unit = {
    super.onResume()
    getPreferenceManager.getSharedPreferences.registerOnSharedPreferenceChangeListener(this)
  }

  override def onPause(): Unit = {
    getPreferenceManager.getSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    super.onPause()
  }

  override def onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String): Unit =
    withActivity(_.preferenceChanged())

  protected def withActivity[T](f: (NineCardsPreferencesActivity) => T): Option[T] =
    Option(getActivity) match {
      case Some(a: NineCardsPreferencesActivity) => Some(f(a))
      case _ => None
    }

}
