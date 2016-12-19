package cards.nine.app.ui.commons.dialogs.wizard

import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.TR

class WizardInlineDOM(viewGroup: ViewGroup) {

  import cards.nine.app.ui.commons.ViewGroupFindViews._

  lazy val wizardInlineWorkspace =
    findView(TR.wizard_inline_workspace).run(viewGroup)

  lazy val wizardInlinePagination =
    findView(TR.wizard_inline_pagination_panel).run(viewGroup)

  lazy val wizardInlineSkip = findView(TR.wizard_inline_skip).run(viewGroup)

  lazy val wizardInlineGotIt = findView(TR.wizard_inline_got_it).run(viewGroup)

}

trait WizardListener {

  def dismissWizard(): Unit

}
