package cards.nine.app.ui.commons

import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper

sealed trait WizardInlineType {
  def name(implicit contextWrapper: ContextWrapper): String
}

case object LauncherWizardInline extends WizardInlineType {
  override def name(implicit contextWrapper: ContextWrapper): String =
    resGetString(R.string.wizard_inline_launcher)
}

case object AppDrawerWizardInline extends WizardInlineType {
  override def name(implicit contextWrapper: ContextWrapper): String =
    resGetString(R.string.wizard_inline_app_drawer)
}

case object ProfileWizardInline extends WizardInlineType {
  override def name(implicit contextWrapper: ContextWrapper): String =
    resGetString(R.string.wizard_inline_profile)
}

case object CollectionsWizardInline extends WizardInlineType {
  override def name(implicit contextWrapper: ContextWrapper): String =
    resGetString(R.string.wizard_inline_collections)
}
