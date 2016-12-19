package cards.nine.app.ui.preferences.appdrawer

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import macroid.ContextWrapper

class AppDrawerJobs(ui: AppDrawerUiActions)(implicit contextWrapper: ContextWrapper) extends Jobs {

  def initialize(): TaskService[Unit] = ui.initialize()

}
