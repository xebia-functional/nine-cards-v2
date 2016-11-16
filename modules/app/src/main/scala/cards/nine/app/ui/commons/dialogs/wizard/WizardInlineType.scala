package cards.nine.app.ui.commons.dialogs.wizard

import com.fortysevendeg.ninecardslauncher.R
import macroid.ContextWrapper
import macroid.extras.ResourcesExtras._

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

object WizardInlineType {

  def apply(name: String): WizardInlineType = name match {
    case n if n == LauncherWizardInline.toString => LauncherWizardInline
    case n if n == AppDrawerWizardInline.toString => AppDrawerWizardInline
    case n if n == CollectionsWizardInline.toString => CollectionsWizardInline
    case _ => ProfileWizardInline
  }

}