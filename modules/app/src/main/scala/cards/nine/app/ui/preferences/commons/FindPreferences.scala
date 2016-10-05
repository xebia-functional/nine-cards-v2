package cards.nine.app.ui.preferences.commons

import android.preference.PreferenceFragment

trait FindPreferences {

  self: PreferenceFragment =>

  def find[T](pref: NineCardsPreferenceValue[_]): T = findPreference(pref.name).asInstanceOf[T]

}
