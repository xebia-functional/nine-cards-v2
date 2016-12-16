package cards.nine.app.ui.commons.dialogs.shortcuts

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Shortcut
import macroid.ActivityContextWrapper

class ShortcutDialogJobs(actions: ShortcutDialogUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadShortcuts()
  } yield ()

  def loadShortcuts(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      shortcuts <- di.deviceProcess.getAvailableShortcuts
      _ <- actions.loadShortcuts(shortcuts)
    } yield ()

  def configureShortcut(shortcut: Shortcut): TaskService[Unit] =
    for {
      _ <- actions.close()
      _ <- actions.configureShortcut(shortcut)
    } yield ()

  def showErrorLoadingShortcuts(): TaskService[Unit] = actions.showErrorLoadingShortcutsInScreen()

}

