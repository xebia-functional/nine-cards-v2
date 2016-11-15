package cards.nine.app.ui.dialogs.wizard

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.WizardInlineData
import cards.nine.app.ui.components.layouts.tweaks.WizardInlineWorkspacesTweaks._
import cards.nine.commons.services.TaskService.TaskService
import com.fortysevendeg.ninecardslauncher.R
import macroid.extras.ResourcesExtras._
import macroid.extras.ViewTweaks._
import macroid._

class WizardInlineUiActions(dom: WizardInlineDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  lazy val steps = Seq(
    WizardInlineData(
      R.drawable.wizard_01,
      resGetString(R.string.wizard_step_title_1),
      resGetString(R.string.wizard_step_1)),
    WizardInlineData(
      R.drawable.wizard_02,
      resGetString(R.string.wizard_step_title_2),
      resGetString(R.string.wizard_step_2)),
    WizardInlineData(
      R.drawable.wizard_03,
      resGetString(R.string.wizard_step_title_3),
      resGetString(R.string.wizard_step_3)))

  def initialize(): TaskService[Unit] =
    ((dom.wizardInlineWorkspace <~ wiwData(steps)) ~
      (dom.wizardInlineRoot <~ vBackgroundColorResource(R.color.wizard_inline_background))).toService

}
