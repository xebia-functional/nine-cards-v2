package cards.nine.app.ui.launcher

import android.content.{ComponentName, Intent}
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.action_filters.{MomentForceBestAvailableActionFilter, MomentReloadedActionFilter}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.commons.ops.WidgetsOps
import cards.nine.app.ui.commons.ops.WidgetsOps.Cell
import cards.nine.app.ui.commons.{BroadAction, Jobs, RequestCodes}
import cards.nine.app.ui.components.dialogs.AlertDialogFragment
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.drawer._
import cards.nine.app.ui.launcher.exceptions.SpaceException
import cards.nine.app.ui.wizard.WizardActivity
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons._
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types._
import cards.nine.models.{Card, Collection, Moment, _}
import cards.nine.process.accounts._
import cards.nine.process.device._
import cards.nine.process.device.models._
import cards.nine.process.intents.LauncherExecutorProcessPermissionException
import cards.nine.process.moment.MomentException
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.R
import macroid.{ActivityContextWrapper, Ui}
import monix.eval.Task

import scala.language.postfixOps

class LauncherPresenter(actions: LauncherUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with AppNineCardsIntentConversions {

  val tagDialog = "dialog"

  val defaultPage = 1

  lazy val momentPreferences = new MomentPreferences

  private[this] def updateWeather(): TaskService[Unit] =
    for {
      maybeCondition <- di.recognitionProcess.getWeather.map(_.conditions.headOption).resolveLeftTo(None)
      _ = momentPreferences.weatherLoaded(maybeCondition.isEmpty || maybeCondition.contains(UnknownCondition))
      _ <- actions.showWeather(maybeCondition).toService
    } yield ()

  def startAddItemToCollection(app: ApplicationData): Unit = startAddItemToCollection(toCardData(app))

  def startAddItemToCollection(contact: Contact): Unit = startAddItemToCollection(toCardData(contact))

  def launchMenu(): Unit = actions.openMenu().run

  private[this] def startAddItemToCollection(card: CardData): Unit = {
    statuses = statuses.startAddItem(card)
    actions.startAddItem(card.cardType).run
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
      case (Some(collection: Collection), Some(card: CardData)) =>
        di.collectionProcess.addCards(collection.id, Seq(card)).resolveAsyncUi2(
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
      case Some(card: CardData) =>
        card.cardType match {
          case AppCardType =>
            createOrUpdateDockApp(card, AppDockType, position).resolveAsyncUi2(
              onResult = (_) => actions.reloadDockApps(DockAppData(card.term, AppDockType, card.intent, card.imagePath getOrElse "", position)),
              onException = (_) => actions.showContactUsError())
          case ContactCardType =>
            createOrUpdateDockApp(card, ContactDockType, position).resolveAsyncUi2(
              onResult = (_) => actions.reloadDockApps(DockAppData(card.term, ContactDockType, card.intent, card.imagePath getOrElse "", position)),
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
      case Some(card: CardData) if card.cardType == AppCardType =>
        card.packageName foreach { packageName =>
          launcherService(di.launcherExecutorProcess.launchUninstall(packageName))
        }
      case _ =>
    }
    statuses = statuses.reset()
    actions.endAddItem.run
  }

  def settingsInAddItem(): Unit = {
    statuses.cardAddItemMode match {
      case Some(card: CardData) if card.cardType == AppCardType =>
        card.packageName foreach { packageName =>
          launcherService(di.launcherExecutorProcess.launchSettings(packageName))
        }
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
      di.collectionProcess.reorderCollection(from, to).resolveAsyncUi2(
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

  def goToMomentWorkspace(): Unit = (actions.goToMomentWorkspace() ~ actions.closeAppsMoment()).run

  def openMomentIntent(card: Card, moment: Option[NineCardsMoment]): Unit = {
    card.packageName foreach { packageName =>
      val category = moment map MomentCategory getOrElse FreeCategory
      di.trackEventProcess.openAppFromAppDrawer(packageName, category).resolveAsync2()
    }
    actions.closeAppsMoment().run
    launcherCallService(di.launcherExecutorProcess.execute(card.intent), card.intent.extractPhone())
  }

  def openApp(app: ApplicationData): Unit = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    di.trackEventProcess.openAppFromAppDrawer(app.packageName, AppCategory(app.category)).resolveAsync2()
    launcherService(di.launcherExecutorProcess.execute(toNineCardIntent(app)))
  }

  def openContact(contact: Contact) = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    launcherService(di.launcherExecutorProcess.executeContact(contact.lookupKey))
  }

  def openLastCall(number: String) = if (actions.isTabsOpened) {
    actions.closeTabs.run
  } else {
    launcherCallService(di.launcherExecutorProcess.execute(phoneToNineCardIntent(None, number)), Some(number))
  }

  def execute(intent: NineCardsIntent): Unit =
    launcherCallService(di.launcherExecutorProcess.execute(intent), intent.extractPhone())

  def launchSearch(): Unit = launcherService(di.launcherExecutorProcess.launchSearch)

  def launchVoiceSearch(): Unit = launcherService(di.launcherExecutorProcess.launchVoiceSearch)

  def launchGoogleWeather(): Unit = launcherService {
    for {
      result <- di.userAccountsProcess.havePermission(FineLocation)
      _ <- if (result.hasPermission(FineLocation)) {
        updateWeather() *> di.launcherExecutorProcess.launchGoogleWeather
      } else {
        di.userAccountsProcess.requestPermission(RequestCodes.locationPermission, FineLocation)
      }
    } yield ()
  }

  def launchPlayStore(): Unit = launcherService(di.launcherExecutorProcess.launchPlayStore)

  def launchDial(): Unit = launcherService(di.launcherExecutorProcess.launchDial(phoneNumber = None))

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
    di.collectionProcess.deleteCollection(collection.id).resolveAsyncUi2(
      onResult = (_) => {
        val (page, data) = removeCollectionToCurrentData(collection.id)
        actions.reloadWorkspaces(data, Some(page)) ~
          Ui(momentReloadBroadCastIfNecessary())
      },
      onException = (_) => actions.showContactUsError()
    )
  }

  def closeModeEditWidgets(): Unit = {
    statuses = statuses.copy(mode = NormalMode, idWidget = None)
    actions.closeModeEditWidgets().run
  }

  def goToEditMoment(): Unit = {
    val momentType = actions.getData.headOption flatMap (_.moment) flatMap (_.momentType)
    (momentType match {
      case Some(moment) => actions.editMoment(moment.name)
      case _ => actions.showContactUsError()
    }).run
  }

  def loadApps(appsMenuOption: AppsMenuOption): Unit = {
    val getAppOrder = toGetAppOrder(appsMenuOption)
    getLoadApps(getAppOrder).resolveAsyncUi2(
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

    def getLoadContacts(order: ContactsFilter): TaskService[(IterableContacts, Seq[TermCounter])] =
      for {
        iterableContacts <- di.deviceProcess.getIterableContacts(order)
        counters <- di.deviceProcess.getTermCountersForContacts(order)
      } yield (iterableContacts, counters)

    contactsMenuOption match {
      case ContactsByLastCall =>
        di.deviceProcess.getLastCalls.resolveAsyncUi2(
          onResult = (contacts: Seq[LastCallsContact]) => actions.reloadLastCallContactsInDrawer(contacts),
          onException = (throwable: Throwable) => throwable match {
            case e: CallPermissionException => Ui(requestReadCallLog())
            case _ => Ui.nop
          })
      case _ =>
        val getContactFilter = toGetContactFilter(contactsMenuOption)
        getLoadContacts(getContactFilter).resolveAsyncUi2(
          onResult = {
            case (contacts: IterableContacts, counters: Seq[TermCounter]) =>
              actions.reloadContactsInDrawer(contacts = contacts, counters = counters)
          },
          onException = (throwable: Throwable) => throwable match {
            case e: ContactPermissionException => Ui(requestReadContacts())
            case _ => Ui.nop
          })
    }
  }

  def loadAppsByKeyword(keyword: String): Unit = {
    di.deviceProcess.getIterableAppsByKeyWord(keyword, GetByName).resolveAsyncUi2(
      onResult = {
        case (apps: IterableApps) => actions.reloadAppsInDrawer(apps = apps)
      })
  }

  def loadContactsByKeyword(keyword: String): Unit = {
    di.deviceProcess.getIterableContactsByKeyWord(keyword).resolveAsyncUi2(
      onResult = {
        case (contacts: IterableContacts) => actions.reloadContactsInDrawer(contacts = contacts)
      },
      onException = (throwable: Throwable) => throwable match {
        case e: ContactPermissionException => Ui(requestReadContacts())
        case _ => Ui.nop
      })
  }

  def goToCollection(maybeCollection: Option[Collection], point: Point): Unit = (maybeCollection match {
    case Some(collection) => actions.goToCollection(collection, point)
    case _ => actions.showContactUsError()
  }).run

  def goToWizard(): Unit = {
    contextWrapper.original.get foreach { activity =>
      val wizardIntent = new Intent(activity, classOf[WizardActivity])
      activity.startActivity(wizardIntent)
    }
  }

  def addWidget(maybeAppWidgetId: Option[Int]): Unit = {

    def getWidgetInfoById(appWidgetId: Int): TaskService[(ComponentName, Cell)] =
      actions.getWidgetInfoById(appWidgetId) match {
        case Some(info) => TaskService.right(info)
        case _ => TaskService.left(MomentException("Info widget not found"))
      }

    def createWidget(appWidgetId: Int, nineCardsMoment: NineCardsMoment) = for {
      moment <- di.momentProcess.getMomentByType(nineCardsMoment)
      widgetInfo <- getWidgetInfoById(appWidgetId)
      (provider, cell) = widgetInfo
      widgetsByMoment <- di.widgetsProcess.getWidgetsByMoment(moment.id)
      space <- getSpaceInTheScreen(widgetsByMoment, cell.spanX, cell.spanY)
      appWidgetRequest = WidgetData(
        momentId = moment.id,
        packageName = provider.getPackageName,
        className = provider.getClassName,
        appWidgetId = Option(appWidgetId),
        area = WidgetArea(
          startX = space.startX,
          startY = space.startY,
          spanX = space.spanX,
          spanY = space.spanY),
        widgetType = AppWidgetType,
        label = None,
        imagePath = None,
        intent = None)
      widget <- di.widgetsProcess.addWidget(appWidgetRequest)
    } yield widget

    def replaceWidget(id: Int, appWidgetId: Int) = for {
      widget <- di.widgetsProcess.updateAppWidgetId(id, appWidgetId)
    } yield widget

    (for {
      appWidgetId <- maybeAppWidgetId
      data <- actions.getData.headOption
      moment <- data.moment
      nineCardMoment <- moment.momentType
    } yield {
      val hostingWidgetId = statuses.hostingNoConfiguredWidget map (_.id)
      val task = hostingWidgetId match {
        case Some(id) => replaceWidget(id, appWidgetId)
        case _ => createWidget(appWidgetId, nineCardMoment)
      }
      task.resolveAsyncUi2(
        onResult = (widget: Widget) => {
          hostingWidgetId match {
            case Some(_) =>
              statuses = statuses.copy(hostingNoConfiguredWidget = None)
              actions.replaceWidget(widget)
            case _ =>
              actions.addWidgets(Seq(widget))
          }
        },
        onException = (ex: Throwable) => ex match {
          case ex: SpaceException => actions.showWidgetNoHaveSpaceMessage()
          case _ => actions.showContactUsError()
        })
    }) getOrElse actions.showContactUsError().run
  }

  def configureOrAddWidget(maybeAppWidgetId: Option[Int]): Unit =
    (maybeAppWidgetId map actions.configureWidget getOrElse actions.showContactUsError()).run

  def cleanPersistedMoment() = {
    momentPreferences.clean()
    momentForceBestAvailable()
  }

  private[this] def momentReloadBroadCastIfNecessary() = sendBroadCast(BroadAction(MomentReloadedActionFilter.action))

  private[this] def momentForceBestAvailable() = sendBroadCast(BroadAction(MomentForceBestAvailableActionFilter.action))

  private[this] def createOrUpdateDockApp(card: CardData, dockType: DockType, position: Int) =
    di.deviceProcess.createOrUpdateDockApp(card.term, dockType, card.intent, card.imagePath getOrElse "", position)

  private[this] def launcherService(service: TaskService[Unit]) =
    service.resolveAsyncUi2(onException = _ => actions.showContactUsError())

  private[this] def launcherCallService(service: TaskService[Unit], maybePhone: Option[String]) =
    service.resolveAsyncServiceOr[Throwable] {
      case e: LauncherExecutorProcessPermissionException =>
        statuses = statuses.copy(lastPhone = maybePhone)
        di.userAccountsProcess.requestPermission(RequestCodes.phoneCallPermission, CallPhone)
      case _ => actions.showContactUsError().toService
    }

  private[this] def requestReadContacts() = launcherService {
    di.userAccountsProcess.requestPermission(RequestCodes.contactsPermission, ReadContacts)
  }

  private[this] def requestReadCallLog() = launcherService {
    di.userAccountsProcess.requestPermission(RequestCodes.callLogPermission, ReadCallLog)
  }

  protected def getLoadApps(order: GetAppOrder): TaskService[(IterableApps, Seq[TermCounter])] =
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

  private[this] def getSpaceInTheScreen(widgetsByMoment: Seq[Widget], spanX: Int, spanY: Int): TaskService[WidgetArea] = {

    def searchSpace(widgets: Seq[Widget]): TaskService[WidgetArea] = {
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
        case Some(space) => TaskService.right(space)
        case _ => TaskService.left(SpaceException("Widget don't have space"))
      }
    }

    for {
      space <- searchSpace(widgetsByMoment)
    } yield space
  }

}

trait LauncherUiActions {

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

  def reloadDockApps(dockApp: DockAppData): Ui[Any]

  def closeModeEditWidgets(): Ui[Any]

  def showAddItemMessage(nameCollection: String): Ui[Any]

  def showWidgetCantResizeMessage(): Ui[Any]

  def showWidgetCantMoveMessage(): Ui[Any]

  def showWidgetNoHaveSpaceMessage(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showMinimumOneCollectionMessage(): Ui[Any]

  def showNoImplementedYetMessage(): Ui[Any]

  def showNoPhoneCallPermissionError(): Ui[Any]

  def showLoading(): Ui[Any]

  def goToPreviousScreen(): Ui[Any]

  def goToNextScreen(): Ui[Any]

  def goToCollection(collection: Collection, point: Point): Ui[Any]

  def reloadCurrentMoment(): Ui[Any]

  def reloadAllViews(): Ui[Any]

  def reloadBarMoment(data: LauncherMoment): Ui[Any]

  def reloadAppsInDrawer(
    apps: IterableApps,
    getAppOrder: GetAppOrder = GetByName,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadContactsInDrawer(
    contacts: IterableContacts,
    counters: Seq[TermCounter] = Seq.empty): Ui[Any]

  def reloadLastCallContactsInDrawer(contacts: Seq[LastCallsContact]): Ui[Any]

  def editMoment(momentType: String): Ui[Any]

  def addWidgets(widgets: Seq[Widget]): Ui[Any]

  def replaceWidget(widget: Widget): Ui[Any]

  def unhostWidget(id: Int): Ui[Any]

  def hostWidget(packageName: String, className: String): Ui[Any]

  def configureWidget(appWidgetId: Int): Ui[Any]

  def getWidgetInfoById(appWidgetId: Int): Option[(ComponentName, Cell)]

  def clearWidgets(): Ui[Any]

  def openMenu(): Ui[Any]

  def openAppsMoment(): Ui[Any]

  def closeAppsMoment(): Ui[Any]

  def reloadDrawerApps(): Ui[Any]

  def reloadDrawerContacts(): Ui[Any]

  def showBottomError(message: Int, action: () => Unit): Ui[Any]

  def isEmptyCollectionsInWorkspace: Boolean

  def canRemoveCollections: Boolean

  def isWorkspaceScrolling: Boolean

  def getCollectionsWithMoment(moments: Seq[Moment]): Seq[(NineCardsMoment, Option[Collection])]

  def getCollection(position: Int): Option[Collection]

  def isTabsOpened: Boolean

  def getData: Seq[LauncherData]

  def getCurrentPage: Option[Int]

  def showWeather(condition: Option[ConditionWeather]): Ui[Any]

}
