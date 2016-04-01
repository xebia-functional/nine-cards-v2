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

class LauncherPresenter(actions: LauncherActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter {

  def registerUser(): Ui[Any] = Ui {
    Task.fork(di.userProcess.register.run).resolveAsync()
  }

  def addCollection(collection: Collection): Ui[Any] = actions.addCollection(collection)

  def removeCollection(maybeCollection: Option[Collection]): Ui[Any] =
    maybeCollection map { collection =>
      if (actions.canRemoveCollections().get) {
        actions.showDialogForRemoveCollection(collection)
      } else {
        actions.showMinimumOneCollectionMessage()
      }
    } getOrElse actions.showContactUsError()

  def removeCollection(collection: Collection): Ui[Any] = Ui {
    Task.fork(deleteCollection(collection.id).run).resolveAsyncUi(
      onResult = (_) => actions.removeCollection(collection),
      onException = (_) => actions.showContactUsError()
    )
  }

  def loadCollectionsAndDockApps(): Ui[Any] = Ui {
    Task.fork(getLauncherApps.run).resolveAsyncUi(
      onResult = {
        // Check if there are collections in DB, if there aren't we go to wizard
        case (Nil, _) => actions.goToWizard()
        case (collections, apps) =>
          Task.fork(getUser().run).resolveAsyncUi(
            onResult = user => actions.loadUserProfile(user))
          actions.loadCollections(collections, apps)
      },
      onException = (ex: Throwable) => actions.goToWizard(),
      onPreTask = () => actions.showLoading()
    )
  }

  def loadApps(appsMenuOption: AppsMenuOption): Ui[Any] = Ui {
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

  def loadContacts(contactsMenuOption: ContactsMenuOption): Ui[Any] = Ui {
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

  def loadAppsByKeyword(keyword: String): Ui[Any] = Ui {
    Task.fork(di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName).run).resolveAsyncUi(
      onResult = {
        case (apps: IterableApps) => actions.reloadAppsInDrawer(apps = apps)
      })
  }

  def loadContactsByKeyword(keyword: String): Ui[Any] = Ui {
    Task.fork(di.deviceProcess.getIterableContactsByKeyWord(keyword).run).resolveAsyncUi(
      onResult = {
        case (contacts: IterableContacts) => actions.reloadContactsInDrawer(contacts = contacts)
      })
  }

  def goToCollection(maybeView: Option[View], maybeCollection: Option[Collection]): Ui[Any] =
    (for {
      view <- maybeView
      collection <- maybeCollection
    } yield actions.goToCollection(view, collection)) getOrElse actions.showContactUsError()

  protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
    di.collectionProcess.deleteCollection(id)

  protected def getUser(): ServiceDef2[User, UserException] = di.userProcess.getUser

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
