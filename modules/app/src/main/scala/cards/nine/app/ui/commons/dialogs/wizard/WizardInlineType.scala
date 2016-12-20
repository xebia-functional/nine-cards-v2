package cards.nine.app.ui.commons.dialogs.wizard

sealed trait WizardInlineType

case object LauncherWizardInline extends WizardInlineType

case object AppDrawerWizardInline extends WizardInlineType

case object ProfileWizardInline extends WizardInlineType

case object CollectionsWizardInline extends WizardInlineType

object WizardInlineType {

  def apply(name: String): WizardInlineType = name match {
    case n if n == LauncherWizardInline.toString    => LauncherWizardInline
    case n if n == AppDrawerWizardInline.toString   => AppDrawerWizardInline
    case n if n == CollectionsWizardInline.toString => CollectionsWizardInline
    case _                                          => ProfileWizardInline
  }

}
