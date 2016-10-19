package cards.nine.app.ui.launcher.jobs

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.{AppUtils, UiContext}
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.TopBarLayoutTweaks._
import cards.nine.app.ui.components.models.LauncherData
import cards.nine.commons.services.TaskService
import cards.nine.process.theme.models.NineCardsTheme
import macroid.{ActivityContextWrapper, FragmentManagerContext, Tweak}

case class TopBarUiActions(dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  case class State(theme: NineCardsTheme = AppUtils.getDefaultTheme)

  private[this] var actionsState = State()

  implicit def theme: NineCardsTheme = actionsState.theme

  def initialize(nineCardsTheme: NineCardsTheme): TaskService[Unit] =
    TaskService.right {
      actionsState = actionsState.copy(theme = nineCardsTheme)
    }

  def loadBar(data: Seq[LauncherData]): TaskService[Unit] = {
    val momentType = data.headOption.flatMap(_.moment).flatMap(_.momentType)
    (dom.topBarPanel <~ (momentType map tblReloadMoment getOrElse Tweak.blank)).toService
  }

}
