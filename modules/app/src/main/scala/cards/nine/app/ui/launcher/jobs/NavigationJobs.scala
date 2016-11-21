package cards.nine.app.ui.launcher.jobs

import cats.implicits._
import android.graphics.Point
import android.os.Bundle
import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.jobs.uiactions.{AppDrawerUiActions, MenuDrawersUiActions, NavigationUiActions, WidgetUiActions}
import cards.nine.app.ui.launcher.{EditWidgetsMode, NormalMode}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models._
import cards.nine.models.types._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class NavigationJobs(
  val navigationUiActions: NavigationUiActions,
  val appDrawerUiActions: AppDrawerUiActions,
  val menuDrawersUiActions: MenuDrawersUiActions,
  val widgetUiActions: WidgetUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardsIntentConversions
  with AppNineCardsIntentConversions {

  def openMenu(): TaskService[Unit] = menuDrawersUiActions.openMenu()

  def launchCreateOrCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchCreateOrCollection(bundle)

  def launchPrivateCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPrivateCollection(bundle)

  def launchPublicCollection(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchPublicCollection(bundle)

  def launchAddMoment(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchAddMoment(bundle)

  def launchEditMoment(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchEditMoment(bundle)

  def launchWidgets(bundle: Bundle): TaskService[Unit] = navigationUiActions.launchWidgets(bundle)

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

  def goToCollection(maybeCollection: Option[Collection], point: Point): TaskService[Unit] = {

    def openCollection(collection: Collection): TaskService[Unit] =
      for {
        _ <- di.trackEventProcess.openCollectionTitle(collection.name)
        _ <- di.trackEventProcess.openCollectionOrder(collection.position)
        _ <- navigationUiActions.goToCollection(collection, point)
      } yield ()

    for {
      _ <- di.trackEventProcess.useNavigationBar()
      _ <- maybeCollection match {
        case Some(collection) => openCollection(collection)
        case _ => navigationUiActions.showContactUsError()
      }
    } yield()
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

  def openMomentIntent(card: Card, moment: Option[NineCardsMoment]): TaskService[Unit] ={

    def trackAppMoment(packageName: String, moment: NineCardsMoment) =
      for {
        _ <- di.trackEventProcess.openAppFromCollection(packageName, MomentCategory(moment))
        _ <- di.trackEventProcess.openApplicationByMoment(moment.name)
      } yield ()

    for {
      _ <- (card.packageName, moment) match {
        case (Some(packageName), Some(m)) => trackAppMoment(packageName, m)
        case _ => TaskService.empty
      }
      _ <- menuDrawersUiActions.closeAppsMoment()
      _ <- di.launcherExecutorProcess.execute(card.intent)
    } yield ()
  }


  def openMomentIntentException(maybePhone: Option[String]): TaskService[Unit] = {
    statuses = statuses.copy(lastPhone = maybePhone)
    di.userAccountsProcess.requestPermission(RequestCodes.phoneCallPermission, CallPhone)
  }

  def openDockApp(app: DockAppData): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.openDockAppTitle(app.name)
      _ <- di.trackEventProcess.openDockAppOrder(app.position)
      _ <- di.launcherExecutorProcess.execute(app.intent)
    } yield ()

  def launchSearch(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.usingSearchByKeyboard()
      _ <- di.launcherExecutorProcess.launchSearch
    } yield ()

  def launchVoiceSearch(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.usingSearchByVoice()
      _ <- di.launcherExecutorProcess.launchVoiceSearch
    } yield ()

  def launchGooglePlay(packageName: String): TaskService[Unit] = di.launcherExecutorProcess.launchGooglePlay(packageName)

  def launchGoogleWeather(): TaskService[Unit] =
    for {
      _ <- di.trackEventProcess.goToWeather()
      result <- di.userAccountsProcess.havePermission(types.FineLocation)
      _ <- if (result.hasPermission(types.FineLocation)) {
        di.launcherExecutorProcess.launchGoogleWeather
      } else {
        di.userAccountsProcess.requestPermission(RequestCodes.locationPermission, FineLocation)
      }
    } yield ()

  def launchPlayStore(): TaskService[Unit] = di.launcherExecutorProcess.launchPlayStore

  def launchDial(): TaskService[Unit] = di.launcherExecutorProcess.launchDial(phoneNumber = None)

  def goToChangeMoment(): TaskService[Unit] =
    for {
      moments <- di.momentProcess.getMoments
      _ <- navigationUiActions.showSelectMomentDialog(moments)
    } yield ()

  def goToMenuOption(itemId: Int): TaskService[Unit] = {
    itemId match {
      case R.id.menu_collections => di.trackEventProcess.goToCollectionsByMenu() *> navigationUiActions.goToCollectionWorkspace()
      case R.id.menu_moments => di.trackEventProcess.goToMomentsByMenu() *> navigationUiActions.goToMomentWorkspace()
      case R.id.menu_profile => di.trackEventProcess.goToProfileByMenu() *> navigationUiActions.goToProfile()
      case R.id.menu_send_feedback => di.trackEventProcess.goToSendUsFeedback() *> navigationUiActions.showNoImplementedYetMessage()
      case R.id.menu_help => di.trackEventProcess.goToHelpByMenu() *>  navigationUiActions.showNoImplementedYetMessage()
      case _ => TaskService.empty
    }
  }

}
