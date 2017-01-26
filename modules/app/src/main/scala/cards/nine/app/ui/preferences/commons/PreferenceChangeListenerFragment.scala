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

package cards.nine.app.ui.preferences.commons

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceFragment
import cards.nine.app.ui.preferences.NineCardsPreferencesActivity

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
    withActivity(_.preferenceChanged(key))

  protected def withActivity[T](f: (NineCardsPreferencesActivity) => T): Option[T] =
    Option(getActivity) match {
      case Some(a: NineCardsPreferencesActivity) => Some(f(a))
      case _                                     => None
    }

}
