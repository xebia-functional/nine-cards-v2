package cards.nine.app.ui.launcher.jobs

import cards.nine.app.ui.MomentPreferences
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.{Jobs, actions}
import cards.nine.app.ui.components.models.{CollectionsWorkSpace, LauncherData, LauncherMoment, MomentWorkSpace}
import cards.nine.commons.services.TaskService
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{Collection, DockApp, Moment, UnknownCondition}
import cats.implicits._
import macroid.{ActivityContextWrapper, Ui}

class LauncherJobs(
  mainLauncherUiActions: MainLauncherUiActions,
  workspaceUiActions: WorkspaceUiActions,
  menuDrawersUiActions: MenuDrawersUiActions,
  appDrawerUiActions: MainAppDrawerUiActions,
  navigationUiActions: NavigationUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs { self =>

  lazy val momentPreferences = new MomentPreferences

  def initialize(): TaskService[Unit] =
    for {
      _ <- di.userProcess.register
      theme <- getThemeTask
      _ <- mainLauncherUiActions.initialize(theme)
      _ <- workspaceUiActions.initialize(theme)
      _ <- menuDrawersUiActions.initialize(theme)
      _ <- appDrawerUiActions.initialize(theme)
    } yield ()

  def resume(): TaskService[Unit] =
    for {
      _ <- di.observerRegister.registerObserverTask()
      _ <- if (momentPreferences.loadWeather) updateWeather() else TaskService.empty
      _ <- if (mainLauncherUiActions.dom.isEmptyCollections) {
        loadLauncherInfo()
      } else if (momentPreferences.nonPersist) {
        changeMomentIfIsAvailable()
      } else {
        TaskService.empty
      }
    } yield ()

  def reloadAppsMomentBar(): TaskService[Unit] = {

    def selectMoment(moments: Seq[Moment]): Option[Moment] = for {
      currentMomentType <- mainLauncherUiActions.dom.getCurrentMomentType
      moment <- moments.find(_.momentType == currentMomentType)
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
        collection <- collections.find(_.id == collectionId)
      } yield collection

    def getMoment = momentPreferences.getPersistMoment match {
      case Some(moment) => di.momentProcess.fetchMomentByType(moment)
      case _ => di.momentProcess.getBestAvailableMoment
    }

    def getLauncherInfo: TaskService[(Seq[Collection], Seq[DockApp], Option[Moment])] =
      (di.collectionProcess.getCollections |@| di.deviceProcess.getDockApps |@| getMoment).tupled

    for {
      result <- getLauncherInfo
      _ <- result match {
        case (Nil, _, _) => navigationUiActions.goToWizard()
        case (collections, apps, moment) =>
          for {
            user <- di.userProcess.getUser
            _ <- menuDrawersUiActions.loadUserProfileMenu(
              maybeEmail = user.email,
              maybeName = user.userProfile.name,
              maybeAvatarUrl = user.userProfile.avatar,
              maybeCoverUrl = user.userProfile.cover)
            collectionMoment = getCollectionMoment(moment, collections)
            launcherMoment = LauncherMoment(moment flatMap (_.momentType), collectionMoment)
            data = LauncherData(MomentWorkSpace, Some(launcherMoment)) +: createLauncherDataCollections(collections)
          } yield ()
      }
    } yield ()

    // goToWizard())
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

    //reloadAppsMomentBar()
  }

  private[this] def updateWeather(): TaskService[Unit] =
    for {
      maybeCondition <- di.recognitionProcess.getWeather.map(_.conditions.headOption).resolveLeftTo(None)
      _ = momentPreferences.weatherLoaded(maybeCondition.isEmpty || maybeCondition.contains(UnknownCondition))
      _ <- workspaceUiActions.showWeather(maybeCondition)
    } yield ()

  private[this] def createLauncherDataCollections(collections: Seq[Collection]): Seq[LauncherData] = {
    collections.grouped(numSpaces).toList.zipWithIndex map {
      case (data, index) => LauncherData(CollectionsWorkSpace, collections = data, positionByType = index)
    }
  }

}
