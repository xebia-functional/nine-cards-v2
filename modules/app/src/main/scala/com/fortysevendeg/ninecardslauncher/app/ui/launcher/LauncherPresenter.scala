package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.content.{Context, Intent}
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.commons.NineCardIntentConversions
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, Presenter}
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class LauncherPresenter(actions: LauncherUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with NineCardIntentConversions
  with LauncherExecutor
  with AnalyticDispatcher { self =>

  val tagDialog = "dialog"

  override def getApplicationContext: Context = contextWrapper.application

  def initialize(): Unit = {
    Task.fork(di.userProcess.register.run).resolveAsync()
    actions.initialize.run
  }

  def resume(): Unit = {
    di.observerRegister.registerObserver()
    if (actions.isEmptyCollectionsInWorkspace) {
      loadCollectionsAndDockApps()
    }
  }

  def pause(): Unit = di.observerRegister.unregisterObserver()

  def back(): Unit = actions.back.run

  def resetAction(): Unit = actions.resetAction.run

  def connectUserProfile(name: String, email: String, avatarUrl: Option[String]): Unit =
    actions.showUserProfile(name, email, avatarUrl).run

  def connectPlusProfile(coverPhotoUrl: String): Unit =
    actions.showPlusProfile(coverPhotoUrl).run

  def logout(): Unit = actions.logout.run

  def openApp(app: App): Unit = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    self !>>
      TrackEvent(
        screen = CollectionDetailScreen,
        category = AppCategory(app.category),
        action = OpenAction,
        label = Some(ProvideLabel(app.packageName)),
        value = Some(OpenAppFromAppDrawerValue))
    execute(toNineCardIntent(app))
  }

  def openSettings(app: App) = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    launchSettings(app.packageName)
  }

  def openContact(contact: Contact) = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    executeContact(contact.lookupKey)
  }

  def openLastCall(contact: LastCallsContact) = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    execute(phoneToNineCardIntent(contact.number))
  }

  def addCollection(collection: Collection): Unit = actions.addCollection(collection).run

  def removeCollection(maybeCollection: Option[Collection]): Unit =
    (maybeCollection map { collection =>
      if (actions.canRemoveCollections) {
        Ui(showDialogForRemoveCollection(collection))
      } else {
        actions.showMinimumOneCollectionMessage()
      }
    } getOrElse actions.showContactUsError()).run

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
        case (Nil, _) => Ui(goToWizard())
        case (collections, apps) =>
          Task.fork(getUser.run).resolveAsyncUi(
            onResult = user => actions.loadUserProfile(user))
          actions.loadCollections(collections, apps)
      },
      onException = (ex: Throwable) => Ui(goToWizard()),
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
      activity <- contextWrapper.original.get
    } yield {
      val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
        activity,
        new Pair[View, String](view, getContentTransitionName(collection.position)))
      val intent = new Intent(activity, classOf[CollectionsDetailsActivity])
      intent.putExtra(startPosition, collection.position)
      intent.putExtra(indexColorToolbar, collection.themedColorIndex)
      intent.putExtra(iconToolbar, collection.icon)
      activity.startActivity(intent, options.toBundle)
    }) getOrElse actions.showContactUsError().run

  def goToWizard(): Unit = {
    contextWrapper.original.get foreach { activity =>
      val wizardIntent = new Intent(activity, classOf[WizardActivity])
      activity.startActivity(wizardIntent)
    }
  }

  protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
    di.collectionProcess.deleteCollection(id)

  protected def getUser: ServiceDef2[User, UserException] = di.userProcess.getUser

  protected def getLauncherApps: ServiceDef2[(Seq[Collection], Seq[DockApp]), CollectionException with DockAppException] =
    for {
      collections <- di.collectionProcess.getCollections
      dockApps <- di.deviceProcess.getDockApps
    } yield (collections, dockApps)

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

  private[this] def showDialogForRemoveCollection(collection: Collection): Unit = {
    contextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        val dialog = new AlertDialogFragment(
          message = R.string.removeCollectionMessage,
          positiveAction = () => removeCollection(collection)
        )
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

}

trait LauncherUiActions {

  def initialize: Ui[Any]

  def back: Ui[Any]

  def resetAction: Ui[Any]

  def logout: Ui[Any]

  def closeTabs: Ui[Any]

  def showUserProfile(name: String, email: String, avatarUrl: Option[String]): Ui[Any]

  def showPlusProfile(coverPhotoUrl: String): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def removeCollection(collection: Collection): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showLoading(): Ui[Any]

  def loadCollections(collections: Seq[Collection], apps: Seq[DockApp]): Ui[Any]

  def loadUserProfile(user: User): Ui[Any]

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[_]

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any]

  def isEmptyCollectionsInWorkspace: Boolean

  def canRemoveCollections: Boolean

  def isTabsOpened: Boolean

}
