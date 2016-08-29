package com.fortysevendeg.ninecardslauncher.app.ui.preferences.fragments

import android.preference.PreferenceFragment
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardsPreferenceValue

trait FindPreferences {

  self: PreferenceFragment =>

  def find[T](pref: NineCardsPreferenceValue[_]): T = findPreference(pref.name).asInstanceOf[T]

}
