package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.{TaskService, _}
import macroid.ActivityContextWrapper

class LauncherJobs(
  mainLauncherUiActions: MainLauncherUiActions,
  workspaceUiActions: WorkspaceUiActions,
  menuDrawersUiActions: MenuDrawersUiActions,
  appDrawerUiActions: MainAppDrawerUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs { self =>

  def initialize(): TaskService[Unit] =
    for {
      _ <- di.userProcess.register
      theme <- getThemeTask
      _ <- mainLauncherUiActions.initialize(theme)
      _ <- workspaceUiActions.initialize(theme)
      _ <- menuDrawersUiActions.initialize(theme)
      _ <- appDrawerUiActions.initialize(theme)
    } yield ()

}
