package cards.nine.app.ui.preferences.developers

import cards.nine.app.ui.commons.{ImplicitsUiExceptions, Jobs}
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.GetByName
import macroid.ContextWrapper

class AppsListJobs(ui: AppsListUiActions)(implicit contextWrapper: ContextWrapper)
    extends Jobs
    with ImplicitsUiExceptions {

  def initialize() =
    for {
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _    <- ui.loadApps(apps)
    } yield ()

}
