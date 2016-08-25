package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.app.Activity
import android.content.{ComponentName, Context, Intent}
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.analytics._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions, NineCardIntentConversions, PreferencesValuesKeys}
import com.fortysevendeg.ninecardslauncher.app.ui.PersistMoment
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity
import com.fortysevendeg.ninecardslauncher.app.ui.collections.CollectionsDetailsActivity._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Constants._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WidgetsOps.Cell
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{MomentForceBestAvailableActionFilter, MomentReloadedActionFilter}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, Presenter, RequestCodes, WidgetsOps}
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.AlertDialogFragment
import com.fortysevendeg.ninecardslauncher.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.Statuses._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.drawer._
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.exceptions.{SpaceException, SpaceExceptionImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CollectionException}
import com.fortysevendeg.ninecardslauncher.process.commons.models.{Card, Collection, Moment}
import com.fortysevendeg.ninecardslauncher.process.commons.types._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models._
import com.fortysevendeg.ninecardslauncher.process.moment.MomentException
import com.fortysevendeg.ninecardslauncher.process.widget.models.{AppWidget, WidgetArea}
import com.fortysevendeg.ninecardslauncher.process.widget.{AddWidgetRequest, AppWidgetException, MoveWidgetRequest, ResizeWidgetRequest}
import com.fortysevendeg.ninecardslauncher2.R
import com.google.firebase.analytics.FirebaseAnalytics
import macroid.{ActivityContextWrapper, Ui}
import rapture.core.{Answer, Errata, Result}

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try
import scalaz.concurrent.Task

class LauncherPresenter(actions: LauncherUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with Conversions
  with NineCardIntentConversions
  with LauncherExecutor
  with AnalyticDispatcher { self =>

  val tagDialog = "dialog"

  val defaultPage = 1

  lazy val persistMoment = new PersistMoment

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
    } else if (persistMoment.nonPersist) {
      changeMomentIfIsAvailable()
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
          onResult = (_) => {
            actions.showAddItemMessage(collection.name) ~
              Ui(momentReloadBroadCastIfNecessary())
          },
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
    (statuses.mode, statuses.transformation) match {
      case (NormalMode, _) => actions.openAppsMoment().run
      case (EditWidgetsMode, Some(_)) => backToActionEditWidgets()
      case (EditWidgetsMode, None) => closeModeEditWidgets()
      case _ =>
    }
  }

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
        (actions.reloadWorkspaces(data, Some(page)) ~
          Ui(momentReloadBroadCastIfNecessary())).run
      case _ =>
    }
  }

  def updateCollection(collection: Collection): Unit = {
    val data = updateCollectionInCurrentData(collection)
    actions.reloadWorkspaces(data).run
  }

  def removeCollection(collection: Collection): Unit = {
    Task.fork(di.collectionProcess.deleteCollection(collection.id).run).resolveAsyncUi(
      onResult = (_) => {
        val (page, data) = removeCollectionToCurrentData(collection.id)
        actions.reloadWorkspaces(data, Some(page)) ~
          Ui(momentReloadBroadCastIfNecessary())
      },
      onException = (_) => actions.showContactUsError()
    )
  }

  def openModeEditWidgets(id: Int): Unit = if (!actions.isWorkspaceScrolling) {
    statuses = statuses.copy(mode = EditWidgetsMode, transformation = None, idWidget = Some(id))
    actions.openModeEditWidgets().run
  }

  def backToActionEditWidgets(): Unit = {
    statuses = statuses.copy(transformation = None)
    actions.reloadViewEditWidgets().run
  }

  def loadViewEditWidgets(id: Int): Unit = {
    statuses = statuses.copy(idWidget = Some(id), transformation = None)
    actions.reloadViewEditWidgets().run
  }

  def closeModeEditWidgets(): Unit = {
    statuses = statuses.copy(mode = NormalMode, idWidget = None)
    actions.closeModeEditWidgets().run
  }

  def resizeWidget(): Unit = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = Some(ResizeTransformation))
    actions.resizeWidget().run
  }

  def moveWidget(): Unit = if (statuses.mode == EditWidgetsMode) {
    statuses = statuses.copy(transformation = Some(MoveTransformation))
    actions.moveWidget().run
  }

  def arrowWidget(arrow: Arrow): Unit = if (statuses.mode == EditWidgetsMode) {

    type WidgetMovement = (Int, MoveWidgetRequest)

    val limits = Option((WidgetsOps.rows, WidgetsOps.columns))

    def outOfTheLimit(area: WidgetArea) =
      area.spanX <= 0 ||
        area.spanY <= 0 ||
        area.startX + area.spanX > WidgetsOps.columns ||
        area.startY + area.spanY > WidgetsOps.rows

    def resizeIntersect(idWidget: Int): ServiceDef2[Boolean, AppWidgetException] = {

      def convertSpace(widgetArea: WidgetArea) = {
        val r = ResizeWidgetRequest.tupled(operationArgs)
        widgetArea.copy(
          spanX = widgetArea.spanX + r.increaseX,
          spanY = widgetArea.spanY + r.increaseY)
      }

      for {
        widget <- di.widgetsProcess.getWidgetById(idWidget).resolveOption()
        widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(widget.momentId)
        newSpace = convertSpace(widget.area)
      } yield {
        outOfTheLimit(newSpace) ||
          widgetsByMoment.filterNot(_.id == widget.id).exists(w => newSpace.intersect(w.area, limits))
      }
    }

    @scala.annotation.tailrec
    def searchSpaceForMoveWidget(
      movements: List[MoveWidgetRequest],
      widget: AppWidget,
      otherWidgets: Seq[AppWidget]): Option[WidgetMovement] =
      movements match {
        case Nil => None
        case head :: tail =>
          val newPosition = widget.area.copy(
            startX = widget.area.startX + head.displaceX,
            startY = widget.area.startY + head.displaceY)
          if (outOfTheLimit(newPosition)) {
            None
          } else {
            val widgetsIntersected = otherWidgets.filter(w => newPosition.intersect(w.area, limits))
            widgetsIntersected match {
              case Nil => Option((widget.id, head))
              case intersected =>
                searchSpaceForMoveWidget(tail, widget, otherWidgets)
            }
          }
      }

    def moveIntersect(idWidget: Int): ServiceDef2[Option[WidgetMovement], AppWidgetException] =
      for {
        widget <- di.widgetsProcess.getWidgetById(idWidget).resolveOption()
        widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(widget.momentId)
      } yield {
        val otherWidgets = widgetsByMoment.filterNot(_.id == widget.id)
        searchSpaceForMoveWidget(steps(widget.area), widget, otherWidgets)
      }

    def operationArgs: (Int, Int) = arrow match {
      case ArrowUp => (0, -1)
      case ArrowDown => (0, 1)
      case ArrowRight => (1, 0)
      case ArrowLeft => (-1, 0)
    }

    def steps(area: WidgetArea): List[MoveWidgetRequest] = (arrow match {
      case ArrowUp => 1 to area.startY map (p => MoveWidgetRequest(0, -p))
      case ArrowDown => 1 until (WidgetsOps.columns - area.startY) map (p => MoveWidgetRequest(0, p))
      case ArrowRight => 1 until (WidgetsOps.rows - area.startX) map (p => MoveWidgetRequest(p, 0))
      case ArrowLeft => 1 to area.startX map (p => MoveWidgetRequest(-p, 0))
    }).toList

    (statuses.idWidget, statuses.transformation) match {
      case (Some(id), Some(ResizeTransformation)) =>
        Task.fork(resizeIntersect(id).run).resolveAsync(
          onResult = (intersect: Boolean) => {
            if (intersect) {
              actions.showWidgetCantResizeMessage().run
            } else {
              val resizeRequest= ResizeWidgetRequest.tupled(operationArgs)
              Task.fork(di.widgetsProcess.resizeWidget(id, resizeRequest).run).resolveAsyncUi(
                onResult = (_) => actions.resizeWidgetById(id, resizeRequest),
                onException = (_) => actions.showContactUsError())
            }
          },
          onException = (_) => actions.showContactUsError().run)
      case (Some(id), Some(MoveTransformation)) =>
        Task.fork(moveIntersect(id).run).resolveAsync(
          onResult = {
            case Some((idWidget, moveWidgetRequest)) =>
              Task.fork(di.widgetsProcess.moveWidget(id, moveWidgetRequest).run).resolveAsyncUi(
                onResult = (_) => actions.moveWidgetById(idWidget, moveWidgetRequest),
                onException = (_) => actions.showContactUsError())
            case _ => actions.showWidgetCantMoveMessage().run
          },
          onException = (_) => actions.showContactUsError())
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

  def goToEditMoment(): Unit = {
    val momentType = actions.getData.headOption flatMap (_.moment) flatMap (_.momentType)
    (momentType match {
      case Some(moment) => actions.editMoment(moment.name)
      case _ => actions.showContactUsError()
    }).run
  }

  def goToChangeMoment(): Unit = actions.showSelectMomentDialog().run

  def changeMoment(momentType: NineCardsMoment): Unit = {
    persistMoment.persist(momentType)

    def getMoment = for {
      maybeMoment <- di.momentProcess.fetchMomentByType(momentType)
      moment <- maybeMoment match {
        case Some(moment) => Service(Task(Answer[Moment, MomentException](moment)))
        case _ => di.momentProcess.createMomentWithoutCollection(momentType)
      }
      collection <- moment.collectionId match {
        case Some(collectionId: Int) => di.collectionProcess.getCollectionById(collectionId)
        case _ => Service(Task(Answer[Option[Collection], CollectionException](None)))
      }

    } yield (moment, collection)

    Task.fork(getMoment.run).resolveAsyncUi(
      onResult = {
        case (moment, collection) =>
          val data = LauncherData(MomentWorkSpace, Some(LauncherMoment(moment.momentType, collection)))
          actions.reloadMoment(data) ~
            Ui(momentReloadBroadCastIfNecessary())
        case _ => Ui.nop
      })
  }

  def reloadAppsMomentBar(): Unit = {

    def selectMoment(moments: Seq[Moment]) = for {
      data <- actions.getData.headOption
      currentMoment <- data.moment
      moment <- moments.find(_.momentType == currentMoment.momentType)
    } yield moment

    def getCollectionById(collectionId: Option[Int]): ServiceDef2[Option[Collection], CollectionException] =
      collectionId match {
        case Some(id) => di.collectionProcess.getCollectionById(id)
        case _ => Service(Task(Answer(None)))
      }

    def getCollection: ServiceDef2[LauncherMoment, MomentException with CollectionException] = for {
      moments <- di.momentProcess.getMoments
      moment = selectMoment(moments)
      collection <- getCollectionById(moment flatMap (_.collectionId))
    } yield LauncherMoment(moment flatMap (_.momentType), collection)

    Task.fork(getCollection.run).resolveAsyncUi(
      onResult = {
        case launcherMoment: LauncherMoment => actions.reloadBarMoment(launcherMoment)
        case _ => Ui.nop
      })
  }

  def loadLauncherInfo(): Unit = {

    def getMoment = persistMoment.getPersistMoment match {
      case Some(moment) => di.momentProcess.fetchMomentByType(moment)
      case _ => di.momentProcess.getBestAvailableMoment
    }

    def getLauncherInfo: ServiceDef2[(Seq[Collection], Seq[DockApp], Option[Moment]), CollectionException with DockAppException with MomentException] =
      for {
        collections <- di.collectionProcess.getCollections
        dockApps <- di.deviceProcess.getDockApps
        moment <- getMoment
      } yield (collections, dockApps, moment)

    Task.fork(getLauncherInfo.run).resolveAsyncUi(
      onResult = {
        // Check if there are collections in DB, if there aren't we go to wizard
        case (Nil, _, _) => Ui(goToWizard())
        case (collections, apps, moment) =>
          Task.fork(di.userProcess.getUser.run).resolveAsyncUi(
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
        case _ => Service(Task(Errata(MomentException("Info widget not found"))))
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
        })
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

  def cleanPersistedMoment() = {
    persistMoment.clean()
    momentForceBestAvailable()
  }

  private[this] def momentReloadBroadCastIfNecessary() = sendBroadCast(BroadAction(MomentReloadedActionFilter.action))

  private[this] def momentForceBestAvailable() = sendBroadCast(BroadAction(MomentForceBestAvailableActionFilter.action))

  private[this] def createOrUpdateDockApp(card: AddCardRequest, dockType: DockType, position: Int) =
    di.deviceProcess.createOrUpdateDockApp(card.term, dockType, card.intent, card.imagePath, position)

  // Check if there is a new best available moment, if not reload the apps moment bar
  def changeMomentIfIsAvailable(): Unit = {

    // Check if the best available moment is different to the current moment, if it's different return Some(moment)
    // in the other case None
    def getCheckMoment: ServiceDef2[LauncherMoment, CollectionException with MomentException] = {

      def getCollection(moment: Option[Moment]): ServiceDef2[Option[Collection], CollectionException] = {
        val emptyService = Service(Task(Result.answer[Option[Collection], CollectionException](None)))
        val momentType = moment flatMap (_.momentType)
        val currentMomentType = actions.getData.headOption flatMap (_.moment) flatMap (_.momentType)
        val collectionId = moment flatMap (_.collectionId)
        if (momentType == currentMomentType) {
          Service(Task(Result.errata(CollectionExceptionImpl("Best available moment is same of current moment"))))
        } else {
          collectionId map di.collectionProcess.getCollectionById getOrElse emptyService
        }
      }

      for {
        moment <- di.momentProcess.getBestAvailableMoment
        collection <- getCollection(moment)
      } yield LauncherMoment(moment flatMap (_.momentType), collection)
    }

    Task.fork(getCheckMoment.run).resolveAsyncUi(
      onResult = (launcherMoment) => {
        val data = LauncherData(MomentWorkSpace, Some(launcherMoment))
        actions.reloadMoment(data)
      },
      onException = (_) => Ui(reloadAppsMomentBar()))
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
        val hasConflict = widgets find (widget => widget.area.intersect(area, Option((WidgetsOps.rows, WidgetsOps.columns))))
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

  def resizeWidgetById(id: Int, resize: ResizeWidgetRequest): Ui[Any]

  def moveWidgetById(id: Int, move: MoveWidgetRequest): Ui[Any]

  def cancelWidget(appWidgetId: Int): Ui[Any]

  def editWidgetsShowActions(): Ui[Any]

  def reloadViewEditWidgets(): Ui[Any]

  def closeModeEditWidgets(): Ui[Any]

  def showAddItemMessage(nameCollection: String): Ui[Any]

  def showWidgetCantResizeMessage(): Ui[Any]

  def showWidgetCantMoveMessage(): Ui[Any]

  def showWidgetNoHaveSpaceMessage(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showNoImplementedYetMessage(): Ui[Any]

  def showLoading(): Ui[Any]

  def goToPreviousScreen(): Ui[Any]

  def goToNextScreen(): Ui[Any]

  def loadLauncherInfo(data: Seq[LauncherData], apps: Seq[DockApp]): Ui[Any]

  def reloadCurrentMoment(): Ui[Any]

  def reloadMomentTopBar(): Ui[Any]

  def reloadMoment(moment: LauncherData): Ui[Any]

  def reloadBarMoment(data: LauncherMoment): Ui[Any]

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

  def editMoment(momentType: String): Ui[Any]

  def addWidgets(widgets: Seq[AppWidget]): Ui[Any]

  def deleteSelectedWidget(): Ui[Any]

  def unhostWidget(id: Int): Ui[Any]

  def hostWidget(widget: Widget): Ui[Any]

  def configureWidget(appWidgetId: Int): Ui[Any]

  def getWidgetInfoById(appWidgetId: Int): Option[(ComponentName, Cell)]

  def clearWidgets(): Ui[Any]

  def showWidgetsDialog(): Ui[Any]

  def showSelectMomentDialog(): Ui[Any]

  def openMenu(): Ui[Any]

  def openAppsMoment(): Ui[Any]

  def closeAppsMoment(): Ui[Any]

  def isEmptyCollectionsInWorkspace: Boolean

  def canRemoveCollections: Boolean

  def isWorkspaceScrolling: Boolean

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
    transformation: Option[EditWidgetTransformation] = None,
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