package cards.nine.app.ui.launcher.jobs

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.receivers.moments.MomentBroadcastReceiver
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.action_filters.{MomentForceBestAvailableActionFilter, MomentReloadedActionFilter}
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.{BroadAction, Jobs, MomentPreferences, RequestCodes}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.{ChangeMomentException, LoadDataException}
import cards.nine.app.ui.launcher.jobs.uiactions._
import cards.nine.app.ui.preferences.commons._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.{NineCardsMoment, UnknownCondition, _}
import cards.nine.models.{Collection, DockApp, Moment}
import cats.implicits._
import macroid.ActivityContextWrapper
import monix.eval.Task

class LauncherJobs(
  val mainLauncherUiActions: LauncherUiActions,
  val workspaceUiActions: WorkspaceUiActions,
  val menuDrawersUiActions: MenuDrawersUiActions,
  val appDrawerUiActions: AppDrawerUiActions,
  val navigationUiActions: NavigationUiActions,
  val dockAppsUiActions: DockAppsUiActions,
  val topBarUiActions: TopBarUiActions,
  val widgetUiActions: WidgetUiActions,
  val dragUiActions: DragUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardsIntentConversions { self =>

  lazy val momentPreferences = new MomentPreferences

  val defaultPage = 1

  def initialize(): TaskService[Unit] = {
    def initServices: TaskService[Unit] =
      di.externalServicesProcess.initializeStrictMode *>
        di.externalServicesProcess.initializeCrashlytics *>
        di.externalServicesProcess.initializeFirebase *>
        di.externalServicesProcess.initializeStetho *>
        di.externalServicesProcess.initializeFlowUp

    def initAllUiActions(): TaskService[Unit] =
      widgetUiActions.initialize() *>
        workspaceUiActions.initialize() *>
        menuDrawersUiActions.initialize() *>
        appDrawerUiActions.initialize() *>
        topBarUiActions.initialize() *>
        mainLauncherUiActions.initialize()

    for {
      _ <- mainLauncherUiActions.initialize()
      theme <- getThemeTask
      _ <- TaskService.right(statuses = statuses.copy(theme = theme))
      _ <- initAllUiActions()
      _ <- initServices
      _ <- di.userProcess.register
    } yield ()
  }

  def resume(): TaskService[Unit] =
    (if (mainLauncherUiActions.dom.isEmptyCollections) {
      loadLauncherInfo().resolveLeft(exception =>
        Left(LoadDataException("Data not loaded", Option(exception))))
    } else {
      changeMomentIfIsAvailable(force = false).resolveLeft(exception =>
        Left(ChangeMomentException("Exception changing moment", Option(exception))))
    }) *>
      di.observerRegister.registerObserverTask() *>
      updateWeather().resolveIf(ShowWeatherMoment.readValue, ())

  def registerFence(): TaskService[Unit] =
    di.recognitionProcess.registerFenceUpdates(
      action = MomentBroadcastReceiver.momentFenceAction,
      receiver = new MomentBroadcastReceiver)

  def unregisterFence(): TaskService[Unit] =
    di.recognitionProcess.unregisterFenceUpdates(MomentBroadcastReceiver.momentFenceAction)

  def reloadFence(): TaskService[Unit] =
    for {
      _ <- unregisterFence()
      _ <- registerFence()
    } yield ()

  def pause(): TaskService[Unit] = di.observerRegister.unregisterObserverTask()

  def destroy(): TaskService[Unit] = widgetUiActions.destroy()

  def reloadAppsMomentBar(): TaskService[Unit] = {

    def selectMoment(moments: Seq[Moment]): Option[Moment] = for {
      currentMomentType <- mainLauncherUiActions.dom.getCurrentMomentType
      moment <- moments find (_.momentType == currentMomentType)
    } yield moment

    def getCollectionById(collectionId: Option[Int]): TaskService[Option[Collection]] =
      collectionId match {
        case Some(id) => di.collectionProcess.getCollectionById(id)
        case _ => TaskService.right(None)
      }

    for {
      moments <- di.momentProcess.getMoments
      moment = selectMoment(moments)
      collection <- getCollectionById(moment flatMap (_.collectionId))
      launcherMoment = LauncherMoment(moment map (_.momentType), collection)
      _ <- menuDrawersUiActions.reloadBarMoment(launcherMoment)
    } yield ()
  }

  def loadLauncherInfo(): TaskService[Unit] = {

    def getCollectionMoment(moment: Option[Moment], collections: Seq[Collection]) =
      for {
        m <- moment
        collectionId <- m.collectionId
        collection <- collections find (_.id == collectionId)
      } yield collection

    def getMoment = momentPreferences.getPersistMoment match {
      case Some(moment) => di.momentProcess.fetchMomentByType(moment)
      case _ => di.momentProcess.getBestAvailableMoment()
    }

    def getLauncherInfo: TaskService[(Seq[Collection], Seq[DockApp], Option[Moment])] =
      (di.collectionProcess.getCollections |@| di.deviceProcess.getDockApps |@| getMoment).tupled

    def loadData(collections: Seq[Collection], apps: Seq[DockApp], moment: Option[Moment]) = for {
      user <- di.userProcess.getUser
      _ <- menuDrawersUiActions.loadUserProfileMenu(
        maybeEmail = user.email,
        maybeName = user.userProfile.name,
        maybeAvatarUrl = user.userProfile.avatar,
        maybeCoverUrl = user.userProfile.cover)
      collectionMoment = getCollectionMoment(moment, collections)
      launcherMoment = LauncherMoment(moment map (_.momentType), collectionMoment)
      data = LauncherData(MomentWorkSpace, Option(launcherMoment)) +: createLauncherDataCollections(collections)
      _ <- workspaceUiActions.loadLauncherInfo(data)
      _ <- dockAppsUiActions.loadDockApps(apps map (_.toData))
      _ <- topBarUiActions.loadBar(data)
      _ <- menuDrawersUiActions.reloadBarMoment(launcherMoment)
    } yield ()

    for {
      result <- getLauncherInfo
      _ <- result match {
        case (Nil, _, _) => navigationUiActions.goToWizard()
        case (collections, apps, moment) => loadData(collections, apps, moment)
      }
    } yield ()
  }

  def changeMomentIfIsAvailable(force: Boolean, fenceKey: Option[String] = None): TaskService[Unit] = {

    def getCollection(moment: Option[Moment]): TaskService[Option[Collection]] = {
      val collectionId = moment flatMap (_.collectionId)
      collectionId map di.collectionProcess.getCollectionById getOrElse TaskService.right(None)
    }

    def headphoneKey: Option[Boolean] = fenceKey match {
      case Some(HeadphonesFence.keyIn) => Some(true)
      case Some(HeadphonesFence.keyOut) => Some(false)
      case _ => None
    }

    def activityKey: Option[KindActivity] = fenceKey match {
      case Some(InVehicleFence.key) => Some(InVehicleActivity)
      case _ => None
    }

    val canChangeMoment = force || momentPreferences.nonPersist

    for {
      moment <- di.momentProcess.getBestAvailableMoment(maybeHeadphones = headphoneKey, maybeActivity = activityKey)
      collection <- getCollection(moment)
      currentMomentType = mainLauncherUiActions.dom.getCurrentMomentType
      momentType = moment map (_.momentType)
      sameMoment = currentMomentType == momentType
      _ <- (sameMoment, canChangeMoment) match {
        case (false, true) =>
          val launcherMoment = LauncherMoment(moment map (_.momentType), collection)
          val data = LauncherData(MomentWorkSpace, Option(launcherMoment))
          workspaceUiActions.reloadMoment(data)
        case _ => TaskService.empty
      }
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
    } yield ()
  }

  def changeMoment(momentId: Int): TaskService[Unit] = {
    for {
      moment <- di.momentProcess.findMoment(momentId).resolveOption(s"Moment id $momentId not found")
      _ <- TaskService.right(momentPreferences.persist(moment.momentType))
      collection <- moment.collectionId match {
        case Some(collectionId: Int) => di.collectionProcess.getCollectionById(collectionId)
        case _ => TaskService.right(None)
      }
      data = LauncherData(MomentWorkSpace, Option(LauncherMoment(Option(moment.momentType), collection)))
      _ <- workspaceUiActions.reloadMoment(data)
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
    } yield ()
  }

  def cleanPersistedMoment(): TaskService[Unit] = {
    momentPreferences.clean()
    sendBroadCastTask(BroadAction(MomentForceBestAvailableActionFilter.action))
  }

  def reloadCollection(collectionId: Int): TaskService[Unit] =
    for {
      collection <- di.collectionProcess.getCollectionById(collectionId).resolveOption("Collection Id not found in reload collection")
      _ <- addCollection(collection)
    } yield ()

  def addCollection(collection: Collection): TaskService[Unit] = {
    addCollectionToCurrentData(collection) match {
      case Some((page: Int, data: Seq[LauncherData])) =>
        for {
          _ <- workspaceUiActions.reloadWorkspaces(data, Some(page))
          _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
        } yield ()
      case _ => TaskService.empty
    }
  }

  def updateCollection(collection: Collection): TaskService[Unit] = {
    def updateCollectionInCurrentData(collection: Collection): Seq[LauncherData] = {
      val cols = mainLauncherUiActions.dom.getData flatMap (_.collections)
      val collections = cols.updated(collection.position, collection)
      createLauncherDataCollections(collections)
    }
    workspaceUiActions.reloadWorkspaces(updateCollectionInCurrentData(collection))
  }

  def removeCollection(collection: Collection): TaskService[Unit] =
    for {
      _ <- di.collectionProcess.deleteCollection(collection.id)
      (page, data) = removeCollectionToCurrentData(collection.id)
      _ <- workspaceUiActions.reloadWorkspaces(data, Option(page))
      _ <- sendBroadCastTask(BroadAction(MomentReloadedActionFilter.action))
    } yield ()

  def removeMomentDialog(moment: NineCardsMoment, momentId: Int): TaskService[Unit] =
    moment.isDefault match {
      case true => navigationUiActions.showCantRemoveOutAndAboutMessage()
      case _ => navigationUiActions.showDialogForRemoveMoment(momentId)
    }

  def removeMoment(momentId: Int): TaskService[Unit] =
    for {
      _ <- di.momentProcess.deleteMoment(momentId)
      _ <- cleanPersistedMoment()
      _ <- reloadFence()
    } yield ()

  def preferencesChanged(changedPreferences: Array[String]): TaskService[Unit] = {

    def needToRecreate(array: Array[String]): Boolean =
      array.intersect(
        Seq(Theme.name,
          IconsSize.name,
          FontSize.name,
          AppDrawerSelectItemsInScroller.name)).nonEmpty

    def uiAction(prefKey: String): TaskService[Unit] = prefKey match {
      case ShowClockMoment.name => topBarUiActions.reloadMomentTopBar()
      case ShowMicSearchMoment.name => topBarUiActions.reloadMomentTopBar()
      case ShowWeatherMoment.name => topBarUiActions.reloadMomentTopBar()
      case GoogleLogo.name => topBarUiActions.reloadTopBar()
      case _ => TaskService.empty
    }

    Option(changedPreferences) match {
      case Some(array) if array.nonEmpty =>
        if (needToRecreate(array)) {
          mainLauncherUiActions.reloadAllViews()
        } else {
          val tasks = array.map(ar => uiAction(ar).value).toSeq
          TaskService(Task.gatherUnordered(tasks) map (_ => Right((): Unit)))
        }
      case _ => TaskService.empty
    }
  }

  def requestPermissionsResult(
    requestCode: Int,
    permissions: Array[String],
    grantResults: Array[Int]): TaskService[Unit] = {

    def serviceAction(result: Seq[PermissionResult]): TaskService[Unit] = requestCode match {
      case RequestCodes.contactsPermission if result.exists(_.hasPermission(ReadContacts)) =>
        appDrawerUiActions.reloadContacts()
      case RequestCodes.callLogPermission if result.exists(_.hasPermission(ReadCallLog)) =>
        appDrawerUiActions.reloadContacts()
      case RequestCodes.phoneCallPermission if result.exists(_.hasPermission(CallPhone)) =>
        statuses.lastPhone match {
          case Some(phone) =>
            statuses = statuses.copy(lastPhone = None)
            di.launcherExecutorProcess.execute(phoneToNineCardIntent(None, phone))
          case _ => TaskService.right((): Unit)
        }
      case RequestCodes.contactsPermission =>
        for {
          _ <- appDrawerUiActions.reloadApps()
          _ <- navigationUiActions.showContactPermissionError(() =>
            di.userAccountsProcess.requestPermission(RequestCodes.contactsPermission, ReadContacts).resolveAsync())
        } yield ()
      case RequestCodes.callLogPermission =>
        for {
          _ <- appDrawerUiActions.reloadApps()
          _ <- navigationUiActions.showCallPermissionError(() =>
            di.userAccountsProcess.requestPermission(RequestCodes.callLogPermission, ReadCallLog).resolveAsync())
        } yield ()
      case RequestCodes.phoneCallPermission =>
        statuses.lastPhone match {
          case Some(phone) =>
            statuses = statuses.copy(lastPhone = None)
            for {
              _ <- di.launcherExecutorProcess.launchDial(Option(phone))
              _ <- navigationUiActions.showNoPhoneCallPermissionError()
            } yield ()
          case _ => TaskService.empty
        }
      case RequestCodes.locationPermission if result.exists(_.hasPermission(FineLocation)) =>
        for {
          _ <- updateWeather().resolveIf(ShowWeatherMoment.readValue, ())
          _ <- di.launcherExecutorProcess.launchGoogleWeather
        } yield ()
      case _ => TaskService.empty
    }

    for {
      result <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
      _ <- serviceAction(result)
    } yield ()

  }

  private[this] def removeCollectionToCurrentData(collectionId: Int): (Int, Seq[LauncherData]) = {
    val currentData = mainLauncherUiActions.dom.getData.filter(_.workSpaceType == CollectionsWorkSpace)

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

  private[this] def updateWeather(): TaskService[Unit] =
    for {
      weather <- di.recognitionProcess.getWeather
      _ <- workspaceUiActions.showWeather(weather.conditions.headOption getOrElse UnknownCondition)
    } yield ()

  private[this] def addCollectionToCurrentData(collection: Collection): Option[(Int, Seq[LauncherData])] = {
    val currentData = mainLauncherUiActions.dom.getData.filter(_.workSpaceType == CollectionsWorkSpace)
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

  private[this] def createLauncherDataCollections(collections: Seq[Collection]): Seq[LauncherData] = {
    collections.grouped(numSpaces).toList.zipWithIndex map {
      case (data, index) => LauncherData(CollectionsWorkSpace, collections = data, positionByType = index)
    }
  }

}
