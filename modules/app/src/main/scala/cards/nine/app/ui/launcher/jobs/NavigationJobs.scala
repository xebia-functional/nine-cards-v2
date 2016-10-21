package cards.nine.app.ui.launcher.jobs

import android.os.Bundle
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.{EditWidgetsMode, NormalMode}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import macroid.ActivityContextWrapper

class NavigationJobs(
  val navigationUiActions: NavigationUiActions,
  val menuDrawersUiActions: MenuDrawersUiActions,
  val widgetUiActions: WidgetUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs {

  def goToWizard(): TaskService[Unit] = navigationUiActions.goToWizard()

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchCreateOrCollection(bundle)

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPrivateCollection(bundle)

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPublicCollection(bundle)

  def launchEditMoment(bundle: Bundle): TaskService[Unit] =
    navigationUiActions.launchEditMoment(bundle)

  def launchWidgets(bundle: Bundle): TaskService[Unit] =
    navigationUiActions.launchWidgets(bundle)

  def clickWorkspaceBackground(): TaskService[Unit] = {
    (statuses.mode, statuses.transformation) match {
      case (NormalMode, _) => menuDrawersUiActions.openAppsMoment()
      case (EditWidgetsMode, Some(_)) =>
        statuses = statuses.copy(transformation = None)
        widgetUiActions.reloadViewEditWidgets()
      case (EditWidgetsMode, None) =>
        statuses = statuses.copy(mode = NormalMode, idWidget = None)
        widgetUiActions.closeModeEditWidgets()
      case _ => TaskService.empty
    }
  }

}
