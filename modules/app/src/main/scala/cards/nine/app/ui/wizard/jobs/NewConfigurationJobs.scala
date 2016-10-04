package cards.nine.app.ui.wizard.jobs

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.device.GetByName
import macroid.ActivityContextWrapper

class NewConfigurationJobs(
  actions: NewConfigurationUiActions,
  visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def loadBetterCollections(): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.showLoadingBetterCollections()
      collections <- di.collectionProcess.rankApps()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- actions.loadSecondStep(apps.length, collections)
    } yield ()

}
