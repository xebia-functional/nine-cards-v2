package cards.nine.app.ui.launcher.jobs

import android.os.Bundle
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.{EditWidgetsMode, NormalMode}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.AppCategory
import cards.nine.models.{ApplicationData, Contact, NineCardsIntentConversions}
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class NavigationJobs(
  val navigationUiActions: NavigationUiActions,
  val appDrawerUiActions: MainAppDrawerUiActions,
  val menuDrawersUiActions: MenuDrawersUiActions,
  val widgetUiActions: WidgetUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardsIntentConversions
  with AppNineCardsIntentConversions {

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

  def openApp(app: ApplicationData): TaskService[Unit] = if (navigationUiActions.dom.isDrawerTabsOpened) {
    appDrawerUiActions.closeTabs()
  } else {
    for {
      _ <- di.trackEventProcess.openAppFromAppDrawer(app.packageName, AppCategory(app.category))
      _ <- di.launcherExecutorProcess.execute(toNineCardIntent(app))
    } yield ()
  }

  def openContact(contact: Contact): TaskService[Unit] = if (navigationUiActions.dom.isDrawerTabsOpened) {
    appDrawerUiActions.closeTabs()
  } else {
    di.launcherExecutorProcess.executeContact(contact.lookupKey)
  }

  def openLastCall(number: String): TaskService[Unit] = if (navigationUiActions.dom.isDrawerTabsOpened) {
    appDrawerUiActions.closeTabs()
  } else {
    di.launcherExecutorProcess.execute(phoneToNineCardIntent(None, number))
  }

  def launchPlayStore(): TaskService[Unit] = di.launcherExecutorProcess.launchPlayStore

  def launchDial(): TaskService[Unit] = di.launcherExecutorProcess.launchDial(phoneNumber = None)

  def goToMenuOption(itemId: Int): TaskService[Unit] = {
    itemId match {
      case R.id.menu_collections => navigationUiActions.goToCollectionWorkspace()
      case R.id.menu_moments => navigationUiActions.goToMomentWorkspace()
      case R.id.menu_profile => navigationUiActions.goToProfile()
      case R.id.menu_send_feedback => navigationUiActions.showNoImplementedYetMessage()
      case R.id.menu_help => navigationUiActions.showNoImplementedYetMessage()
      case _ => TaskService.empty
    }
  }

}
