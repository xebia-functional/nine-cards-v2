package cards.nine.app.ui.commons.dialogs.wizard

import android.content.Context
import macroid.ContextWrapper

class WizardInlinePreferences(implicit contextWrapper: ContextWrapper) {

  private[this] val name = "wizard-inline-preferences"

  private[this] lazy val wizardInlinePreferences =
    contextWrapper.bestAvailable.getSharedPreferences(name, Context.MODE_PRIVATE)

  def wasShowed(wizardInlineType: WizardInlineType): Unit =
    wizardInlinePreferences.edit.putBoolean(wizardInlineType.toString, true).apply()

  def shouldBeShowed(wizardInlineType: WizardInlineType): Boolean =
    !wizardInlinePreferences.getBoolean(wizardInlineType.toString, false)

  def clean(): Unit = wizardInlinePreferences.edit().clear().apply()
}
