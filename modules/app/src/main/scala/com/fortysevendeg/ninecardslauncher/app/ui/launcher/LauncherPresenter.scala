package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.view.View
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class LauncherPresenter(actions: LauncherUiActions, statuses: LauncherViewStatuses)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def registerUser(): Unit = Task.fork(di.userProcess.register.run).resolveAsync()

  def addCollection(collection: Collection): Unit = actions.addCollection(collection).run

  def removeCollection(maybeCollection: Option[Collection]): Unit =
    maybeCollection map { collection =>
      if (statuses.canRemoveCollections) {
        actions.showDialogForRemoveCollection(collection).run
      } else {
        actions.showMinimumOneCollectionMessage().run
      }
    } getOrElse actions.showContactUsError().run

  def removeCollection(collection: Collection): Unit = {
    Task.fork(deleteCollection(collection.id).run).resolveAsyncUi(
      onResult = (_) => actions.removeCollection(collection),
      onException = (_) => actions.showContactUsError()
    )
  }

  def loadCollectionsAndDockApps(): Unit = {
    Task.fork(getLauncherApps.run).resolveAsyncUi(
      onResult = {
        // Check if there are collections in DB, if there aren't we go to wizard
        case (Nil, _) => actions.goToWizard()
        case (collections, apps) =>
          Task.fork(getUser.run).resolveAsyncUi(
            onResult = user => actions.loadUserProfile(user))
          actions.loadCollections(collections, apps)
      },
      onException = (ex: Throwable) => actions.goToWizard(),
      onPreTask = () => actions.showLoading()
    )
  }

  def loadApps(appsMenuOption: AppsMenuOption): Unit = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    Task.fork(getLoadApps(getAppOrder).run).resolveAsyncUi(
      onResult = {
        case (apps: IterableApps, counters: Seq[TermCounter]) =>
          actions.reloadAppsInDrawer(
            apps = apps,
            getAppOrder = getAppOrder,
            counters = counters)
      }
    )
  }

  def loadContacts(contactsMenuOption: ContactsMenuOption): Unit = {
    contactsMenuOption match {
      case ContactsByLastCall =>
        Task.fork(di.deviceProcess.getLastCalls.run).resolveAsyncUi(
          onResult = (contacts: Seq[LastCallsContact]) => actions.reloadLastCallContactsInDrawer(contacts))
      case _ =>
        val getContactFilter = toGetContactFilter(contactsMenuOption)
        Task.fork(getLoadContacts(getContactFilter).run).resolveAsyncUi(
          onResult = {
            case (contacts: IterableContacts, counters: Seq[TermCounter]) =>
              actions.reloadContactsInDrawer(contacts = contacts, counters = counters)
          })
    }
  }

  def loadAppsByKeyword(keyword: String): Unit = {
    Task.fork(di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName).run).resolveAsyncUi(
      onResult = {
        case (apps: IterableApps) => actions.reloadAppsInDrawer(apps = apps)
      })
  }

  def loadContactsByKeyword(keyword: String): Unit = {
    Task.fork(di.deviceProcess.getIterableContactsByKeyWord(keyword).run).resolveAsyncUi(
      onResult = {
        case (contacts: IterableContacts) => actions.reloadContactsInDrawer(contacts = contacts)
      })
  }

  def goToCollection(maybeView: Option[View], maybeCollection: Option[Collection]): Unit =
    (for {
      view <- maybeView
      collection <- maybeCollection
    } yield actions.goToCollection(view, collection).run) getOrElse actions.showContactUsError().run

  protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
    di.collectionProcess.deleteCollection(id)

  protected def getUser: ServiceDef2[User, UserException] = di.userProcess.getUser

  protected def getLauncherApps: ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
    for {
      collections <- di.collectionProcess.getCollections
      dockApps <- di.deviceProcess.getDockApps
    } yield {
      android.util.Log.d("9cards", s"${collections.length} -- ${dockApps.length}")
      (collections, dockApps)
    }

  protected def getLoadApps(order: GetAppOrder): ServiceDef2[(IterableApps, Seq[TermCounter]), AppException] =
    for {
      iterableApps <- di.deviceProcess.getIterableApps(order)
      counters <- di.deviceProcess.getTermCountersForApps(order)
    } yield (iterableApps, counters)

  protected def getLoadContacts(order: ContactsFilter): ServiceDef2[(IterableContacts, Seq[TermCounter]), ContactException] =
    for {
      iterableContacts <- di.deviceProcess.getIterableContacts(order)
      counters <- di.deviceProcess.getTermCountersForContacts(order)
    } yield (iterableContacts, counters)

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

trait LauncherUiActions {

  def addCollection(collection: Collection): Ui[Any]

  def showDialogForRemoveCollection(collection: Collection): Ui[Any]

  def removeCollection(collection: Collection): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showLoading(): Ui[Any]

  def loadCollections(collections: Seq[Collection], apps: Seq[DockApp]): Ui[Any]

  def loadUserProfile(user: User): Ui[Any]

  def goToWizard(): Ui[Any]

  def goToCollection(view: View, collection: Collection): Ui[Any]

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[_]

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any]

}

trait LauncherViewStatuses {

  def canRemoveCollections: Boolean

}
