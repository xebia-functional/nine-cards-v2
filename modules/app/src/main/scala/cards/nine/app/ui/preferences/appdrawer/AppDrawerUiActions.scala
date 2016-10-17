package cards.nine.app.ui.preferences.appdrawer

import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import cards.nine.app.ui.commons.ops.UiOps._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.ui.preferences.commons._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ContextWrapper, Ui}

class AppDrawerUiActions(dom: AppDrawerDOM)(implicit contextWrapper: ContextWrapper) {

  def initialize(): TaskService[Unit] = Ui {
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
