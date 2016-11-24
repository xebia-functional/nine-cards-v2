package cards.nine.app.ui.commons.dialogs.widgets

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ActivityContextWrapper

class WidgetsDialogJobs(actions: WidgetsDialogUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadWidgets()
    } yield ()

  def loadWidgets(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      widgets <- di.deviceProcess.getWidgets
      _ <- actions.loadWidgets(widgets)
    } yield ()

  def close(): TaskService[Unit] = actions.close()

}
