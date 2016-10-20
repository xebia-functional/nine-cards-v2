package cards.nine.app.ui.launcher.jobs

import cards.nine.app.commons.AppNineCardsIntentConversions
import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.action_filters.MomentReloadedActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs, RequestCodes}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.app.ui.launcher.LauncherActivity._
import cards.nine.app.ui.launcher.exceptions.{ChangeMomentException, LoadDataException}
import cards.nine.app.ui.preferences.commons.PreferencesValuesKeys
import cards.nine.commons.NineCardExtensions._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.types.UnknownCondition
import cards.nine.models.{Collection, DockApp, Moment}
import cards.nine.process.accounts._
import cards.nine.process.theme.models.NineCardsTheme
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper
import monix.eval.Task

class LauncherJobs(
  val mainLauncherUiActions: MainLauncherUiActions,
  val workspaceUiActions: WorkspaceUiActions,
  val menuDrawersUiActions: MenuDrawersUiActions,
  val appDrawerUiActions: MainAppDrawerUiActions,
  val navigationUiActions: NavigationUiActions,
  val dockAppsUiActions: DockAppsUiActions,
  val topBarUiActions: TopBarUiActions,
  val widgetUiActions: WidgetUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardsIntentConversions { self =>

  lazy val momentPreferences = new MomentPreferences

  def initialize(): TaskService[Unit] = {
    def initServices: TaskService[Unit] =
      di.externalServicesProcess.initializeStrictMode *>
        di.externalServicesProcess.initializeCrashlytics *>
        di.externalServicesProcess.initializeFirebase *>
        di.externalServicesProcess.initializeStetho

    def setTheme(theme: NineCardsTheme): TaskService[Unit] =
      workspaceUiActions.initialize(theme) *>
        menuDrawersUiActions.initialize(theme) *>
        appDrawerUiActions.initialize(theme) *>
        topBarUiActions.initialize(theme) *>
        dockAppsUiActions.initialize(theme)

    for {
      _ <- mainLauncherUiActions.initialize()
      _ <- widgetUiActions.initialize()
      _ <- initServices
      _ <- di.userProcess.register
      theme <- getThemeTask
      _ <- setTheme(theme)
    } yield ()
  }

  def resume(): TaskService[Unit] =
    for {
      _ <- di.observerRegister.registerObserverTask()
      _ <- if (momentPreferences.loadWeather) updateWeather() else TaskService.empty
      _ <- if (mainLauncherUiActions.dom.isEmptyCollections) {
        loadLauncherInfo().resolveLeft(exception =>
          Left(LoadDataException("Data not loaded", Option(exception))))
      } else if (momentPreferences.nonPersist) {
        changeMomentIfIsAvailable().resolveLeft(exception =>
          Left(ChangeMomentException("Exception changing moment", Option(exception))))
      } else {
        TaskService.empty
      }
    } yield ()

  def pause(): TaskService[Unit] = di.observerRegister.unregisterObserverTask()

  def destroy(): TaskService[Unit] = widgetUiActions.destroy()

  def reloadAppsMomentBar(): TaskService[Unit] = {

    def selectMoment(moments: Seq[Moment]): Option[Moment] = for {
      currentMomentType <- mainLauncherUiActions.dom.getCurrentMomentType
      moment <- moments find (_.momentType.contains(currentMomentType))
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
      launcherMoment = LauncherMoment(moment flatMap (_.momentType), collection)
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
      case _ => di.momentProcess.getBestAvailableMoment
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
      launcherMoment = LauncherMoment(moment flatMap (_.momentType), collectionMoment)
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

  // Check if there is a new best available moment, if not reload the apps moment bar
  def changeMomentIfIsAvailable(): TaskService[Unit] = {

    def getCollection(moment: Option[Moment]): TaskService[Option[Collection]] = {
      val collectionId = moment flatMap (_.collectionId)
      collectionId map di.collectionProcess.getCollectionById getOrElse TaskService.right(None)
    }

    for {
      moment <- di.momentProcess.getBestAvailableMoment
      collection <- getCollection(moment)
      currentMomentType = mainLauncherUiActions.dom.getCurrentMomentType
      momentType = moment flatMap (_.momentType)
      _ <- currentMomentType match {
        case `momentType` => TaskService.empty
        case _ =>
          val launcherMoment = LauncherMoment(moment flatMap (_.momentType), collection)
          val data = LauncherData(MomentWorkSpace, Option(launcherMoment))
          workspaceUiActions.reloadMoment(data)
      }
    } yield ()
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

  def preferencesChanged(changedPreferences: Array[String]): TaskService[Unit] = {

    def needToRecreate(array: Array[String]): Boolean =
      array.intersect(
        Seq(PreferencesValuesKeys.theme,
          PreferencesValuesKeys.iconsSize,
          PreferencesValuesKeys.fontsSize,
          PreferencesValuesKeys.appDrawerSelectItemsInScroller)).nonEmpty

    def uiAction(prefKey: String): TaskService[Unit] = prefKey match {
      case PreferencesValuesKeys.showClockMoment => topBarUiActions.reloadMomentTopBar()
      case PreferencesValuesKeys.googleLogo => topBarUiActions.reloadTopBar()
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
          _ <- updateWeather()
          _ <- di.launcherExecutorProcess.launchGoogleWeather
        } yield ()
      case _ =>
        TaskService.right((): Unit)
    }


    for {
      result <- di.userAccountsProcess.parsePermissionsRequestResult(permissions, grantResults)
      _ <- serviceAction(result)
    } yield ()

  }

  private[this] def updateWeather(): TaskService[Unit] =
    for {
      maybeCondition <- di.recognitionProcess.getWeather.map(_.conditions.headOption).resolveLeftTo(None)
      _ = momentPreferences.weatherLoaded(maybeCondition.isEmpty || maybeCondition.contains(UnknownCondition))
      _ <- workspaceUiActions.showWeather(maybeCondition)
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
