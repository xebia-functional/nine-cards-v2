package cards.nine.app.ui.dialogs.wizard

import android.view.ViewGroup
import com.fortysevendeg.ninecardslauncher.TR

class WizardInlineDOM(viewGroup: ViewGroup) {

  import cards.nine.app.ui.commons.ViewGroupFindViews._

  lazy val wizardInlineRoot = findView(TR.wizard_inline_root).run(viewGroup)

  lazy val wizardInlineWorkspace = findView(TR.wizard_inline_workspace).run(viewGroup)

}
