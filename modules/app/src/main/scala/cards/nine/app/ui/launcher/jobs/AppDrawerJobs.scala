package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.commons.{Jobs, RequestCodes}
import cards.nine.app.ui.launcher.jobs.uiactions.MainAppDrawerUiActions
import cards.nine.app.ui.launcher.types._
import cards.nine.commons.services.TaskService._
import cards.nine.models.types._
import cards.nine.models.{TermCounter, types}
import cards.nine.process.device.models.{IterableApps, IterableContacts}
import macroid.ActivityContextWrapper

class AppDrawerJobs(
  val mainAppDrawerUiActions: MainAppDrawerUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs { self =>

  def loadApps(appsMenuOption: AppsMenuOption): TaskService[Unit] = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    for {
      result <- getLoadApps(getAppOrder)
      (apps, counters) = result
      _ <- mainAppDrawerUiActions.reloadAppsInDrawer(
        apps = apps,
        getAppOrder = getAppOrder,
        counters = counters)
    } yield ()
  }

  def loadContacts(contactsMenuOption: ContactsMenuOption): TaskService[Unit] = {

    def getLoadContacts(order: ContactsFilter): TaskService[(IterableContacts, Seq[TermCounter])] =
      for {
        iterableContacts <- di.deviceProcess.getIterableContacts(order)
        counters <- di.deviceProcess.getTermCountersForContacts(order)
      } yield (iterableContacts, counters)

    contactsMenuOption match {
      case ContactsByLastCall =>
        for {
          contacts <- di.deviceProcess.getLastCalls
          _ <- mainAppDrawerUiActions.reloadLastCallContactsInDrawer(contacts)
        } yield ()
      case _ =>
        val getContactFilter = toGetContactFilter(contactsMenuOption)
        for {
          result <- getLoadContacts(getContactFilter)
          (contacts, counters) = result
          _ <- mainAppDrawerUiActions.reloadContactsInDrawer(contacts, counters)
        } yield ()
    }
  }

  def loadAppsByKeyword(keyword: String): TaskService[Unit] =
    for {
      apps <- di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)
      _ <- mainAppDrawerUiActions.reloadAppsInDrawer(apps)
    } yield ()

  def loadContactsByKeyword(keyword: String): TaskService[Unit] =
    for {
      contacts <- di.deviceProcess.getIterableContactsByKeyWord(keyword)
      _ <- mainAppDrawerUiActions.reloadContactsInDrawer(contacts)
    } yield ()

  def requestReadContacts(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.contactsPermission, ReadContacts)

  def requestReadCallLog(): TaskService[Unit] =
    di.userAccountsProcess.requestPermission(RequestCodes.callLogPermission, types.ReadCallLog)

  private[this] def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  private[this] def toGetAppOrder(appsMenuOption: AppsMenuOption): GetAppOrder = appsMenuOption match {
    case AppsAlphabetical => GetByName
    case AppsByCategories => GetByCategory
    case AppsByLastInstall => GetByInstallDate
  }

  private[this] def toGetContactFilter(contactMenuOption: ContactsMenuOption): ContactsFilter = contactMenuOption match {
    case ContactsFavorites => FavoriteContacts
    case _ => AllContacts
  }

}
