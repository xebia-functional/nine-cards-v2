package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.{ComponentName, Context, Intent}
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions, PreferencesValuesKeys}
import com.fortysevendeg.ninecardslauncher.app.ui.CachePreferences
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, Presenter, RequestCodes, WidgetsOps}
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.exceptions.{SpaceException, SpaceExceptionImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection, Moment, MomentWithCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentException, MomentExceptionImpl}
import com.fortysevendeg.ninecardslauncher.process.user.UserException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import com.fortysevendeg.ninecardslauncher.process.widget.{AddWidgetRequest, MoveWidgetRequest, ResizeWidgetRequest, AppWidgetException}
import com.fortysevendeg.ninecardslauncher.process.widget.models.{AppWidget, WidgetArea}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.firebase.analytics.FirebaseAnalytics
import macroid.{ActivityContextWrapper, Ui}
import rapture.core.{Answer, Errata, Result}

import scala.concurrent.Future
import scala.util.Try
import scalaz.concurrent.Task

class LauncherPresenter(actions: LauncherUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
    with Conversions
    with NineCardIntentConversions
    with LauncherExecutor
    with AnalyticDispatcher {
  self =>

  val tagDialog = "dialog"

  val defaultPage = 1

  lazy val cache = new CachePreferences

  var statuses = LauncherPresenterStatuses()

  override def getApplicationContext: Context = contextWrapper.application

  def initialize(): Unit = {
    Try(FirebaseAnalytics.getInstance(contextWrapper.bestAvailable))
    Task.fork(di.userProcess.register.run).resolveAsync()
    actions.initialize.run
  }

  def resume(): Unit = {
    di.observerRegister.registerObserver()
    if (actions.isEmptyCollectionsInWorkspace) {
      loadLauncherInfo()
    } else if (cache.canReloadMomentInResume) {
      checkMoment()
    }
  }

  def pause(): Unit = di.observerRegister.unregisterObserver()

  def destroy(): Unit = actions.destroy.run

  def back(): Unit = actions.back.run

  def resetAction(): Unit = actions.resetAction.run

  def destroyAction(): Unit = actions.destroyAction.run

  def logout(): Unit = actions.logout.run

  def startAddItemToCollection(app: App): Unit = startAddItemToCollection(toAddCardRequest(app))

  def startAddItemToCollection(contact: Contact): Unit = startAddItemToCollection(toAddCardRequest(contact))

  def launchMenu(): Unit = actions.openMenu().run

  private[this] def startAddItemToCollection(addCardRequest: AddCardRequest): Unit = {
    statuses = statuses.startAddItem(addCardRequest)
    actions.startAddItem(addCardRequest.cardType).run
  }

  def draggingAddItemTo(position: Int): Unit = statuses = statuses.updateCurrentPosition(position)

  def draggingAddItemToPreviousScreen(position: Int): Unit = {
    actions.goToPreviousScreenAddingItem().run
    statuses.updateCurrentPosition(position)
  }

  def draggingAddItemToNextScreen(position: Int): Unit = {
    actions.goToNextScreenAddingItem().run
    statuses.updateCurrentPosition(position)
  }

  def endAddItemToCollection(): Unit = {
    (actions.getCollection(statuses.currentDraggingPosition), statuses.cardAddItemMode) match {
      case (Some(collection: Collection), Some(request: AddCardRequest)) =>
        Task.fork(di.collectionProcess.addCards(collection.id, Seq(request)).run).resolveAsyncUi(
          onResult = (_) => actions.showAddItemMessage(collection.name),
          onException = (_) => actions.showContactUsError())
      case _ =>
    }
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def endAddItemToDockApp(position: Int): Unit = {
    statuses.cardAddItemMode match {
      case Some(card: AddCardRequest) =>
        card.cardType match {
          case AppCardType =>
            Task.fork(createOrUpdateDockApp(card, AppDockType, position).run).resolveAsyncUi(
              onResult = (_) => actions.reloadDockApps(DockApp(card.term, AppDockType, card.intent, card.imagePath, position)),
              onException = (_) => actions.showContactUsError())
          case ContactCardType =>
            Task.fork(createOrUpdateDockApp(card, ContactDockType, position).run).resolveAsyncUi(
              onResult = (_) => actions.reloadDockApps(DockApp(card.term, ContactDockType, card.intent, card.imagePath, position)),
              onException = (_) => actions.showContactUsError())
          case _ =>
            actions.showContactUsError()
        }
      case _ =>
        actions.showContactUsError().run
    }
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def endAddItem(): Unit = if (statuses.mode == AddItemMode) {
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def uninstallInAddItem(): Unit = {
    statuses.cardAddItemMode match {
      case Some(card: AddCardRequest) if card.cardType == AppCardType =>
        card.packageName foreach launchUninstall
      case _ =>
    }
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def settingsInAddItem(): Unit = {
    statuses.cardAddItemMode match {
      case Some(card: AddCardRequest) if card.cardType == AppCardType =>
        card.packageName foreach launchSettings
      case _ =>
    }
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def startReorder(maybeCollection: Option[Collection], position: Int): Unit = {
    maybeCollection map { collection =>
      statuses = statuses.startReorder(collection, position)
      actions.startReorder.run
    } getOrElse {
      actions.showContactUsError().run
    }
  }

  def draggingReorderTo(position: Int): Unit = statuses = statuses.updateCurrentPosition(position)

  def draggingReorderToNextScreen(position: Int): Unit = {
    actions.goToNextScreenReordering().run
    statuses = statuses.updateCurrentPosition(position)
  }

  def draggingReorderToPreviousScreen(position: Int): Unit = {
    actions.goToPreviousScreenReordering().run
    statuses = statuses.updateCurrentPosition(position)
  }

  def dropReorder(): Unit = if (statuses.mode == ReorderMode) {
    actions.endReorder.run
    val from = statuses.startPositionReorderMode
    val to = statuses.currentDraggingPosition
    if (from != to) {
      Task.fork(di.collectionProcess.reorderCollection(from, to).run).resolveAsyncUi(
        onResult = (_) => {
          val data = reorderCollectionsInCurrentData(from, to)
          actions.reloadWorkspaces(data)
        },
        onException = (_) => {
          val data = reloadCollectionsInCurrentData
          actions.reloadWorkspaces(data) ~ actions.showContactUsError()
        })
    } else {
      val data = reloadCollectionsInCurrentData
      actions.reloadWorkspaces(data).run
    }
    statuses = statuses.reset()
  }

  def removeCollectionInReorderMode(): Unit =
    (statuses.collectionReorderMode map { collection =>
      if (actions.canRemoveCollections) {
        Ui(showDialogForRemoveCollection(collection))
      } else {
        actions.showMinimumOneCollectionMessage()
      }
    } getOrElse actions.showContactUsError()).run

  def editCollectionInReorderMode(): Unit =
    (statuses.collectionReorderMode match {
      case Some(collection) => actions.editCollection(collection)
      case None => actions.showContactUsError()
    }).run

  def goToMomentWorkspace(): Unit = (actions.goToMomentWorkspace() ~ actions.closeAppsMoment()).run

  def clickWorkspaceBackground(): Unit = {
    statuses.mode match {
      case NormalMode => actions.openAppsMoment().run
      case EditWidgetsMode => closeModeEditWidgets()
      case _ =>
    }
  }

  def clickMomentTopBar(): Unit = actions.openAppsMoment().run

  def openMomentIntent(card: Card, moment: Option[NineCardsMoment]): Unit = {
    self !>>
      TrackEvent(
        screen = LauncherScreen,
        category = moment map MomentCategory getOrElse FreeCategory,
        action = OpenAction,
        label = card.packageName map ProvideLabel,
        value = Some(OpenMomentFromWorkspaceValue))
    actions.closeAppsMoment().run
    execute(card.intent)
  }

  def openApp(app: App): Unit = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    self !>>
      TrackEvent(
        screen = LauncherScreen,
        category = AppCategory(app.category),
        action = OpenAction,
        label = Some(ProvideLabel(app.packageName)),
        value = Some(OpenAppFromAppDrawerValue))
    execute(toNineCardIntent(app))
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

  def addCollection(collection: Collection): Unit = {
    addCollectionToCurrentData(collection) match {
      case Some((page: Int, data: Seq[LauncherData])) =>
        actions.reloadWorkspaces(data, Some(page)).run
      case _ =>
    }
  }

  def updateCollection(collection: Collection): Unit = {
    val data = updateCollectionInCurrentData(collection)
    actions.reloadWorkspaces(data).run
  }

  def removeCollection(collection: Collection): Unit = {
    Task.fork(deleteCollection(collection.id).run).resolveAsyncUi(
      onResult = (_) => {
        val (page, data) = removeCollectionToCurrentData(collection.id)
        actions.reloadWorkspaces(data, Some(page))
      },
      onException = (_) => actions.showContactUsError()
    )
  }

  def openModeEditWidgets(id: Int): Unit = {
    statuses = statuses.copy(mode = EditWidgetsMode, idWidget = Some(id))
    actions.openModeEditWidgets().run
  }

  def resizeWidget(): Unit = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = ResizeTransformation)
    actions.resizeWidget().run
  }

  def moveWidget(): Unit = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = MoveTransformation)
    actions.moveWidget().run
  }

  def arrowWidget(arrow: Arrow): Unit = if (statuses.mode == EditWidgetsMode) {

    def getIntersect(idWidget: Int): ServiceDef2[Boolean, AppWidgetException] =
      for {
        widget <- di.widgetsProcess.getWidgetById(idWidget).resolveOption()
        widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(widget.momentId)
        newSpace = convertSpace(widget.area)
      } yield widgetsByMoment.filterNot(_.id == widget.id).exists(_.area.intersect(newSpace))

    def convertSpace(widgetArea: WidgetArea) = statuses.transformation match {
      case ResizeTransformation =>
        val r = ResizeWidgetRequest.tupled(operationArgs)
        widgetArea.copy(
          spanX = widgetArea.spanX + r.increaseX,
          spanY = widgetArea.spanY + r.increaseY)
      case MoveTransformation =>
        val r = MoveWidgetRequest.tupled(operationArgs)
        widgetArea.copy(
          startX = widgetArea.startX + r.displaceX,
          startY = widgetArea.startY + r.displaceY)
    }

    def widgetOperation(id: Int) = statuses.transformation match {
      case ResizeTransformation => di.widgetsProcess.resizeWidget(id, ResizeWidgetRequest.tupled(operationArgs))
      case MoveTransformation => di.widgetsProcess.moveWidget(id, MoveWidgetRequest.tupled(operationArgs))
    }

    def operationArgs = arrow match {
      case ArrowUp => (0, -1)
      case ArrowDown => (0, 1)
      case ArrowRight => (1, 0)
      case ArrowLeft => (-1, 0)
    }

    statuses.idWidget match {
      case Some(id) =>
        Task.fork(getIntersect(id).run).resolveAsync(
          onResult = (intersect: Boolean) => (intersect, statuses.transformation) match {
            case (true, ResizeTransformation) => actions.showWidgetCantResizeMessage().run
            case (true, MoveTransformation) => actions.showWidgetCantMoveMessage().run
            case (false, _) =>
              Task.fork(widgetOperation(id).run).resolveAsyncUi(
                onResult = (_) => actions.arrowWidget(arrow),
                onException = (_) => actions.showContactUsError()
              )
          },
          onException = (_) => actions.showContactUsError().run
        )
      case _ => actions.showContactUsError().run
    }

  }

  def cancelWidget(maybeAppWidgetId: Option[Int]): Unit = if (statuses.mode == EditWidgetsMode) {
    (maybeAppWidgetId match {
      case Some(id) => actions.cancelWidget(id)
      case _ => Ui.nop
    }).run
  }

  def editWidgetsShowActions(): Unit = actions.editWidgetsShowActions().run

  def closeModeEditWidgets(): Unit = {
    statuses = statuses.copy(mode = NormalMode, idWidget = None)
    actions.closeModeEditWidgets().run
  }

  def goToChangeMoment(): Unit = {
    Task.fork(di.momentProcess.getAvailableMoments.run).resolveAsyncUi(
      onResult = (moments: Seq[MomentWithCollection]) => {
        if (moments.isEmpty) {
          actions.showEmptyMoments()
        } else {
          actions.showSelectMomentDialog(moments)
        }
      },
      onException = (_) => actions.showContactUsError())
  }

  def changeMoment(moment: MomentWithCollection): Unit = {
    cache.updateTimeMomentChangedManually()
    val data = LauncherData(MomentWorkSpace, Some(LauncherMoment(moment.momentType, Some(moment.collection))))
    actions.reloadMoment(data).run
  }

  def loadLauncherInfo(): Unit = {
    Task.fork(getLauncherInfo.run).resolveAsyncUi(
      onResult = {
        // Check if there are collections in DB, if there aren't we go to wizard
        case (Nil, _, _) => Ui(goToWizard())
        case (collections, apps, moment) =>
          Task.fork(getUser.run).resolveAsyncUi(
            onResult = user => actions.showUserProfile(
              email = user.email,
              name = user.userProfile.name,
              avatarUrl = user.userProfile.avatar,
              coverPhotoUrl = user.userProfile.cover))
          val collectionMoment = for {
            m <- moment
            collectionId <- m.collectionId
            collection <- collections.find(_.id == collectionId)
          } yield collection
          val launcherMoment = LauncherMoment(moment flatMap (_.momentType), collectionMoment)
          val data = LauncherData(MomentWorkSpace, Some(launcherMoment)) +: createLauncherDataCollections(collections)
          actions.loadLauncherInfo(data, apps)
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

  def goToCollection(maybeCollection: Option[Collection], point: Point): Unit = {
    def launchIntent(activity: Activity, collection: Collection) = {
      val intent = new Intent(activity, classOf[CollectionsDetailsActivity])
      intent.putExtra(startPosition, collection.position)
      intent.putExtra(indexColorToolbar, collection.themedColorIndex)
      intent.putExtra(iconToolbar, collection.icon)
      Lollipop.ifSupportedThen {
        val color = resGetColor(getIndexColor(collection.themedColorIndex))
        actions.rippleToCollection(color, point) ~~
          Ui {
            activity.startActivityForResult(intent, RequestCodes.goToCollectionDetails)
          }
      } getOrElse {
        Ui(activity.startActivity(intent))
      }
    }

    ((for {
      collection <- maybeCollection
      activity <- contextWrapper.original.get
    } yield launchIntent(activity, collection)) getOrElse actions.showContactUsError()).run
  }

  def resetFromCollectionDetail(): Unit = actions.resetFromCollection().run

  def goToWizard(): Unit = {
    contextWrapper.original.get foreach { activity =>
      val wizardIntent = new Intent(activity, classOf[WizardActivity])
      activity.startActivity(wizardIntent)
    }
  }

  def goToWidgets(): Unit = actions.showWidgetsDialog().run

  def deleteWidget(): Unit =
    (statuses.idWidget match {
      case Some(id) => actions.deleteSelectedWidget()
      case _ => actions.showContactUsError()
    }).run

  def deleteDBWidget(): Unit =
    statuses.idWidget match {
      case Some(id) =>
        Task.fork(di.widgetsProcess.deleteWidget(id).run).resolveAsyncUi(
          onResult = (_) => {
            closeModeEditWidgets()
            actions.unhostWidget(id)
          },
          onException = (_) => actions.showContactUsError()
        )
      case _ => actions.showContactUsError().run
    }

  def loadWidgetsForMoment(nineCardsMoment: NineCardsMoment): Unit = {

    def getWidgets = for {
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      widgets <- di.widgetsProcess.getWidgetsByMoment(moment.id)
    } yield widgets

    Task.fork(getWidgets.run).resolveAsyncUi(
      onPreTask = actions.clearWidgets,
      onResult = {
        case Nil => Ui.nop
        case widgets => actions.addWidgets(widgets)
      },
      onException = (_) => actions.showContactUsError()
    )

  }

  def addWidget(maybeAppWidgetId: Option[Int]): Unit = {

    def getWidgetInfoById(appWidgetId: Int): ServiceDef2[(ComponentName, Cell), MomentException] =
      actions.getWidgetInfoById(appWidgetId) match {
        case Some(info) => Service(Task(Answer[(ComponentName, Cell), MomentException](info)))
        case _ => Service(Task(Errata(MomentExceptionImpl("Info widget not found"))))
      }

    def createWidget(appWidgetId: Int, nineCardsMoment: NineCardsMoment) = for {
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      (provider, cell) <- getWidgetInfoById(appWidgetId)
      widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(moment.id)
      space <- getSpaceInTheScreen(widgetsByMoment, cell.spanX, cell.spanY)
      appWidgetRequest = AddWidgetRequest(
        momentId = moment.id,
        packageName = provider.getPackageName,
        className = provider.getClassName,
        appWidgetId = appWidgetId,
        startX = space.startX,
        startY = space.startY,
        spanX = space.spanX,
        spanY = space.spanY,
        widgetType = AppWidgetType)
      widget <- di.widgetsProcess.addWidget(appWidgetRequest)
    } yield widget

    (for {
      appWidgetId <- maybeAppWidgetId
      data <- actions.getData.headOption
      moment <- data.moment
      nineCardMoment <- moment.momentType
    } yield {
      Task.fork(createWidget(appWidgetId, nineCardMoment).run).resolveAsyncUi(
        onResult = (widget: AppWidget) => actions.addWidgets(Seq(widget)),
        onException = (ex) => ex match {
          case ex: SpaceException => actions.showWidgetNoHaveSpaceMessage()
          case _ => actions.showContactUsError()
        }
      )
    }) getOrElse actions.showContactUsError().run
  }

  def hostWidget(widget: Widget): Unit = actions.hostWidget(widget).run

  def configureOrAddWidget(maybeAppWidgetId: Option[Int]): Unit =
    (maybeAppWidgetId map actions.configureWidget getOrElse actions.showContactUsError()).run

  def preferencesChanged(changedPreferences: Array[String]): Unit = {

    def needToRecreate(array: Array[String]): Boolean = array.contains(PreferencesValuesKeys.themeFile)

    def uiAction(prefKey: String): Ui[_] = prefKey match {
      case PreferencesValuesKeys.showClockMoment => actions.reloadMomentTopBar()
      case _ => Ui.nop
    }

    (contextWrapper.original.get, Option(changedPreferences)) match {
        case (Some(activity), Some(array)) if array.nonEmpty =>
          if (needToRecreate(array)) {
            activity.recreate()
          } else {
            (array map uiAction reduce (_ ~ _)).run
          }
        case _ =>
      }
    }

  private[this] def createOrUpdateDockApp(card: AddCardRequest, dockType: DockType, position: Int) =
    di.deviceProcess.createOrUpdateDockApp(card.term, dockType, card.intent, card.imagePath, position)

  protected def deleteCollection(id: Int): ServiceDef2[Unit, CollectionException] =
    di.collectionProcess.deleteCollection(id)

  protected def getUser: ServiceDef2[User, UserException] = di.userProcess.getUser

  protected def getLauncherInfo: ServiceDef2[(Seq[Collection], Seq[DockApp], Option[Moment]), CollectionException with DockAppException with MomentException] =
    for {
      collections <- di.collectionProcess.getCollections
      dockApps <- di.deviceProcess.getDockApps
      moment <- di.momentProcess.getBestAvailableMoment
    } yield (collections, dockApps, moment)

  // Check if the best available moment is different to the current moment, if it's different return Some(moment)
  // in the other case None
  protected def getCheckMoment: ServiceDef2[Option[LauncherMoment], CollectionException with MomentException] = {

    def getCollection(moment: Option[Moment]): ServiceDef2[Option[Collection], CollectionException] = {
      val emptyService = Service(Task(Result.answer[Option[Collection], CollectionException](None)))
      val momentType = moment flatMap (_.momentType)
      val currentMomentType = actions.getData.headOption flatMap (_.moment) flatMap (_.momentType)
      val collectionId = moment flatMap (_.collectionId)
      if (momentType == currentMomentType) {
        emptyService
      } else {
        collectionId map di.collectionProcess.getCollectionById getOrElse emptyService
      }
    }

    for {
      moment <- di.momentProcess.getBestAvailableMoment
      collection <- getCollection(moment)
    } yield collection map (_ => LauncherMoment(moment flatMap (_.momentType), collection))
  }

  // Check if there is a new best available moment. If not, we check if the current moment was changed
  private[this] def checkMoment(): Unit =
    Task.fork(getCheckMoment.run).resolveAsyncUi(
      onResult = (launcherMoment) => {
        launcherMoment map { _ =>
          val data = LauncherData(MomentWorkSpace, launcherMoment)
          actions.reloadMoment(data)
        } getOrElse Ui.nop
      })

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

  private[this] def removeCollectionToCurrentData(collectionId: Int): (Int, Seq[LauncherData]) = {
    val currentData = actions.getData.filter(_.workSpaceType == CollectionsWorkSpace)

    // We remove a collection in sequence and fix positions
    val collections = (currentData flatMap (_.collections.filterNot(_.id == collectionId))).zipWithIndex map {
      case (col, index) => col.copy(position = index)
    }

    val maybeWorkspaceCollection = currentData find (_.collections.exists(_.id == collectionId))
    val maybePage = maybeWorkspaceCollection map currentData.indexOf

    val newData = createLauncherDataCollections(collections)

    val page = maybePage map { page =>
      if (newData.isDefinedAt(page)) page else newData.length - 1
    } getOrElse defaultPage

    (page, newData)
  }

  private[this] def reorderCollectionsInCurrentData(from: Int, to: Int): Seq[LauncherData] = {
    val cols = actions.getData flatMap (_.collections)
    val collections = cols.reorder(from, to).zipWithIndex map {
      case (collection, index) => collection.copy(position = index)
    }
    createLauncherDataCollections(collections)
  }

  private[this] def reloadCollectionsInCurrentData: Seq[LauncherData] = {
    val collections = actions.getData flatMap (_.collections)
    createLauncherDataCollections(collections)
  }

  private[this] def addCollectionToCurrentData(collection: Collection): Option[(Int, Seq[LauncherData])] = {
    val currentData = actions.getData.filter(_.workSpaceType == CollectionsWorkSpace)
    currentData.lastOption map { data =>
      val lastWorkspaceHasSpace = data.collections.size < numSpaces
      val newData = if (lastWorkspaceHasSpace) {
        currentData.dropRight(1) :+ data.copy(collections = data.collections :+ collection)
      } else {
        val newPosition = currentData.count(_.workSpaceType == CollectionsWorkSpace)
        currentData :+ LauncherData(CollectionsWorkSpace, collections = Seq(collection), positionByType = newPosition)
      }
      val page = newData.size - 1
      (page, newData)
    }
  }

  private[this] def updateCollectionInCurrentData(collection: Collection): Seq[LauncherData] = {
    val cols = actions.getData flatMap (_.collections)
    val collections = cols.updated(collection.position, collection)
    createLauncherDataCollections(collections)
  }

  private[this] def createLauncherDataCollections(collections: Seq[Collection]): Seq[LauncherData] = {
    collections.grouped(numSpaces).toList.zipWithIndex map {
      case (data, index) => LauncherData(CollectionsWorkSpace, collections = data, positionByType = index)
    }
  }

  private[this] def getSpaceInTheScreen(widgetsByMoment: Seq[AppWidget], spanX: Int, spanY: Int): ServiceDef2[WidgetArea, SpaceException] = {

    def searchSpace(widgets: Seq[AppWidget]): ServiceDef2[WidgetArea, SpaceException] = {
      val emptySpaces = (for {
        column <- 0 to (WidgetsOps.columns - spanX)
        row <- 0 to (WidgetsOps.rows - spanY)
      } yield {
        val area = WidgetArea(
          startX = column,
          startY = row,
          spanX = spanX,
          spanY = spanY)
        val hasConflict = widgets find (widget => widget.area.intersect(area))
        if (hasConflict.isEmpty) Some(area) else None
      }).flatten
      emptySpaces.headOption match {
        case Some(space) => Service(Task(Answer[WidgetArea, SpaceException](space)))
        case _ => Service(Task(Errata(SpaceExceptionImpl("Widget don't have space"))))
      }
    }

    for {
      space <- searchSpace(widgetsByMoment)
    } yield space
  }

}

trait LauncherUiActions {

  def initialize: Ui[Any]

  def destroy: Ui[Any]

  def back: Ui[Any]

  def resetAction: Ui[Any]

  def destroyAction: Ui[Any]

  def logout: Ui[Any]

  def closeTabs: Ui[Any]

  def startReorder: Ui[Any]

  def endReorder: Ui[Any]

  def goToMomentWorkspace(): Ui[Any]

  def goToPreviousScreenReordering(): Ui[Any]

  def goToNextScreenReordering(): Ui[Any]

  def startAddItem(cardType: CardType): Ui[Any]

  def endAddItem: Ui[Any]

  def goToPreviousScreenAddingItem(): Ui[Any]

  def goToNextScreenAddingItem(): Ui[Any]

  def showUserProfile(email: Option[String], name: Option[String], avatarUrl: Option[String], coverPhotoUrl: Option[String]): Ui[Any]

  def reloadWorkspaces(data: Seq[LauncherData], page: Option[Int] = None): Ui[Any]

  def reloadDockApps(dockApp: DockApp): Ui[Any]

  def openModeEditWidgets(): Ui[Any]

  def resizeWidget(): Ui[Any]

  def moveWidget(): Ui[Any]

  def arrowWidget(arrow: Arrow): Ui[Any]

  def cancelWidget(appWidgetId: Int): Ui[Any]

  def editWidgetsShowActions(): Ui[Any]

  def closeModeEditWidgets(): Ui[Any]

  def showAddItemMessage(nameCollection: String): Ui[Any]

  def showWidgetCantResizeMessage(): Ui[Any]

  def showWidgetCantMoveMessage(): Ui[Any]

  def showWidgetNoHaveSpaceMessage(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showEmptyMoments(): Ui[Any]

  def showNoImplementedYetMessage(): Ui[Any]

  def showLoading(): Ui[Any]

  def goToPreviousScreen(): Ui[Any]

  def goToNextScreen(): Ui[Any]

  def loadLauncherInfo(data: Seq[LauncherData], apps: Seq[DockApp]): Ui[Any]

  def reloadCurrentMoment(): Ui[Any]

  def reloadMomentTopBar(): Ui[Any]

  def reloadMoment(moment: LauncherData): Ui[Any]

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any]

  def rippleToCollection(color: Int, point: Point): Ui[Future[Any]]

  def resetFromCollection(): Ui[Any]

  def editCollection(collection: Collection): Ui[Any]

  def addWidgets(widgets: Seq[AppWidget]): Ui[Any]

  def deleteSelectedWidget(): Ui[Any]

  def unhostWidget(id: Int): Ui[Any]

  def hostWidget(widget: Widget): Ui[Any]

  def configureWidget(appWidgetId: Int): Ui[Any]

  def getWidgetInfoById(appWidgetId: Int): Option[(ComponentName, Cell)]

  def clearWidgets(): Ui[Any]

  def showWidgetsDialog(): Ui[Any]

  def showSelectMomentDialog(moments: Seq[MomentWithCollection]): Ui[Any]

  def openMenu(): Ui[Any]

  def openAppsMoment(): Ui[Any]

  def closeAppsMoment(): Ui[Any]

  def isEmptyCollectionsInWorkspace: Boolean

  def canRemoveCollections: Boolean

  def getCollectionsWithMoment(moments: Seq[Moment]): Seq[(NineCardsMoment, Option[Collection])]

  def getCollection(position: Int): Option[Collection]

  def isTabsOpened: Boolean

  def getData: Seq[LauncherData]

  def getCurrentPage: Option[Int]

}

object Statuses {

  case class LauncherPresenterStatuses(
    touchingWidget: Boolean = false, // This parameter is for controlling scrollable widgets
    mode: LauncherMode = NormalMode,
    transformation: EditWidgetTransformation = ResizeTransformation,
    idWidget: Option[Int] = None,
    cardAddItemMode: Option[AddCardRequest] = None,
    collectionReorderMode: Option[Collection] = None,
    startPositionReorderMode: Int = 0,
    currentDraggingPosition: Int = 0) {

    def startAddItem(card: AddCardRequest): LauncherPresenterStatuses =
      copy(mode = AddItemMode, cardAddItemMode = Some(card))

    def startReorder(collection: Collection, position: Int): LauncherPresenterStatuses =
      copy(
        startPositionReorderMode = position,
        collectionReorderMode = Some(collection),
        currentDraggingPosition = position,
        mode = ReorderMode)

    def updateCurrentPosition(position: Int): LauncherPresenterStatuses =
      copy(currentDraggingPosition = position)

    def reset(): LauncherPresenterStatuses =
      copy(
        startPositionReorderMode = 0,
        cardAddItemMode = None,
        collectionReorderMode = None,
        currentDraggingPosition = 0,
        mode = NormalMode)

    def isReordering(): Boolean = mode == ReorderMode

  }

  sealed trait LauncherMode

  case object NormalMode extends LauncherMode

  case object AddItemMode extends LauncherMode

  case object ReorderMode extends LauncherMode

  case object EditWidgetsMode extends LauncherMode

  sealed trait EditWidgetTransformation

  case object ResizeTransformation extends EditWidgetTransformation

  case object MoveTransformation extends EditWidgetTransformation

}