package cards.nine.app.ui.wizard.jobs

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.device.GetByName
import macroid.ActivityContextWrapper

class NewConfigurationJobs(
  actions: NewConfigurationUiActions,
  wizardUiActions: WizardUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs {

  def loadBetterCollections(): TaskService[Unit] =
    for {
      _ <- wizardUiActions.showLoading()
      collections <- di.collectionProcess.rankApps()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- wizardUiActions.showNewConfiguration()
      _ <- actions.loadSecondStep(apps.length, collections)
    } yield ()

}
