package cards.nine.app.ui.launcher.jobs.uiactions

import android.support.v4.app.{Fragment, FragmentManager}
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.layouts.tweaks.DockAppsPanelLayoutTweaks._
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.DockAppData
import cards.nine.process.theme.models.NineCardsTheme
import macroid.{ActivityContextWrapper, FragmentManagerContext}

class DockAppsUiActions(val dom: LauncherDOM)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_]) {

  implicit def theme: NineCardsTheme = statuses.theme

  def loadDockApps(apps: Seq[DockAppData]): TaskService[Unit] =
    (dom.dockAppsPanel <~ daplInit(apps)).toService

  def reloadDockApps(dockApp: DockAppData): TaskService[Unit] = (dom.dockAppsPanel <~ daplReload(dockApp)).toService

  def reset(): TaskService[Unit] = (dom.dockAppsPanel <~ daplReset()).toService

}
