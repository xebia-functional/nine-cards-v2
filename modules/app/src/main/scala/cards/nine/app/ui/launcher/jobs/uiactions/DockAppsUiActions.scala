package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.{AppUtils, UiContext}
import cards.nine.app.ui.components.layouts.tweaks.DockAppsPanelLayoutTweaks._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.DockAppData
import cards.nine.process.theme.models.NineCardsTheme
import macroid.{ActivityContextWrapper, FragmentManagerContext}

class DockAppsUiActions(val dom: LauncherDOM)
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

  def loadDockApps(apps: Seq[DockAppData]): TaskService[Unit] =
    (dom.dockAppsPanel <~ daplInit(apps)).toService

}
