package com.fortysevendeg.ninecardslauncher.app.ui.preferences.appdrawer

import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.preferences.commons._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ContextWrapper, Ui}

class AppDrawerUiActions(dom: AppDrawerDOM)(implicit contextWrapper: ContextWrapper) {

  lazy val preferenceValues = new NineCardsPreferencesValue

  def initialize(): TaskService[Unit] = Ui {
    reloadLongPressActionText(AppDrawerLongPressAction.readValue(preferenceValues).value)
    reloadAnimationText(AppDrawerAnimation.readValue(preferenceValues).value)
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
  }.toService

  private[this] def reloadLongPressActionText(value: String) = {
    val textValue = AppDrawerLongPressActionValue(value) match {
      case AppDrawerLongPressActionOpenKeyboard => resGetString(R.string.appDrawerOpenKeyboard)
      case AppDrawerLongPressActionOpenContacts => resGetString(R.string.appDrawerOpenContacts)
    }
    dom.longPressPreference.setSummary(resGetString(R.string.appDrawerLongPressSummary, textValue))
  }

  private[this] def reloadAnimationText(value: String) = {
    val textValue = AppDrawerAnimationValue(value) match {
      case AppDrawerAnimationCircle => resGetString(R.string.appDrawerOpenAnimationReveal)
      case AppDrawerAnimationFade => resGetString(R.string.appDrawerOpenAnimationFade)
    }
    dom.animationPreference.setSummary(resGetString(R.string.appDrawerOpenAnimationSummary, textValue))
  }

}
