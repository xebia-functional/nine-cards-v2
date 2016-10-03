package cards.nine.app.ui.wizard.jobs

import android.graphics.Color
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiContext}
import cards.nine.commons.services.TaskService._
import cards.nine.app.ui.commons.ops.UiOps._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import macroid._

class NewConfigurationUiActions(dom: WizardDOM with WizardUiListener)(implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends WizardStyles
  with ImplicitsUiExceptions {

  def loadFirstStep(): TaskService[Unit] =
    (dom.newConfigurationContent <~ vBackgroundColor(Color.RED)).toService

}
