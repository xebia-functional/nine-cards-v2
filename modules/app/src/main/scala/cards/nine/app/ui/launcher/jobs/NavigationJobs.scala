package cards.nine.app.ui.launcher.jobs

import android.os.Bundle
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import macroid.ActivityContextWrapper

class NavigationJobs(navigationUiActions: NavigationUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def goToWizard(): TaskService[Unit] = navigationUiActions.goToWizard()

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchCreateOrCollection(bundle)

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPrivateCollection(bundle)

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPublicCollection(bundle)

  def launchEditMoment(bundle: Bundle, momentMap: Map[String, String]): TaskService[Unit] =
    navigationUiActions.launchEditMoment(bundle, momentMap)

}
