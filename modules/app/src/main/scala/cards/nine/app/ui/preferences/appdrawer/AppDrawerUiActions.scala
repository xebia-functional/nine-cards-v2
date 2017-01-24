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

package cards.nine.app.ui.preferences.appdrawer

import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import cards.nine.app.ui.commons.ops.UiOps._
import macroid.extras.ResourcesExtras._
import cards.nine.app.ui.preferences.commons._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Ui}

class AppDrawerUiActions(dom: AppDrawerDOM)(implicit contextWrapper: ContextWrapper) {

  def initialize(): TaskService[Unit] =
    Ui {
      reloadLongPressActionText(AppDrawerLongPressAction.readValue.value)
      reloadAnimationText(AppDrawerAnimation.readValue.value)
      dom.longPressPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
          reloadLongPressActionText(newValue.toString)
          true
        }
      })

      dom.animationPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener {
        override def onPreferenceChange(preference: Preference, newValue: scala.Any): Boolean = {
          reloadAnimationText(newValue.toString)
          true
        }
      })
    }.toService()

  private[this] def reloadLongPressActionText(value: String) = {
    val textValue = AppDrawerLongPressActionValue(value) match {
      case AppDrawerLongPressActionOpenKeyboard =>
        resGetString(R.string.appDrawerOpenKeyboard)
      case AppDrawerLongPressActionOpenContacts =>
        resGetString(R.string.appDrawerOpenContacts)
    }
    dom.longPressPreference.setSummary(resGetString(R.string.appDrawerLongPressSummary, textValue))
  }

  private[this] def reloadAnimationText(value: String) = {
    val textValue = AppDrawerAnimationValue(value) match {
      case AppDrawerAnimationCircle =>
        resGetString(R.string.appDrawerOpenAnimationReveal)
      case AppDrawerAnimationFade =>
        resGetString(R.string.appDrawerOpenAnimationFade)
    }
    dom.animationPreference.setSummary(
      resGetString(R.string.appDrawerOpenAnimationSummary, textValue))
  }

}
