package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.app.ui.wizard.WizardNoCollectionsSelectedException
import cards.nine.app.ui.wizard.jobs.uiactions.{NewConfigurationUiActions, VisibilityUiActions, WizardUiActions}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Moment.MomentTimeSlotOps
import cards.nine.models._
import cards.nine.models.types._
import macroid.ActivityContextWrapper
import monix.eval.Task

class NewConfigurationJobs(
  val wizardUiActions: WizardUiActions,
  val newConfigurationActions: NewConfigurationUiActions,
  val visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  val defaultDockAppsSize = 4

  val limitWidgets = 10

  def loadBetterCollections(packagesByCategory: Seq[PackagesByCategory] = Seq.empty): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.hideFistStepAndShowLoadingBetterCollections(hidePrevious = true)
      _ <- loadCollections(packagesByCategory)
    } yield ()

  def rollbackLoadBetterCollections(packagesByCategory: Seq[PackagesByCategory] = Seq.empty): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.hideFistStepAndShowLoadingBetterCollections(hidePrevious = false)
      _ <- di.collectionProcess.cleanCollections()
      _ <- loadCollections(packagesByCategory)
    } yield ()

  private[this] def loadCollections(packagesByCategory: Seq[PackagesByCategory] = Seq.empty): TaskService[Unit] =
    for {
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.synchronizeInstalledApps
      collections <- packagesByCategory match {
        case Nil => di.collectionProcess.rankApps()
        case packages => TaskService.right(packages)
      }
      finalCollections = collections filter (collection => collection.category != Misc)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadSecondStep(finalCollections)
    } yield ()

  def saveCollections(collections: Seq[PackagesByCategory]): TaskService[Unit] = {

    def toCollectionData(apps: Seq[ApplicationData]) = {
      collections.zipWithIndex.map { zipped =>
        val (collection, index) = zipped
        val packageNames = collection.packages
        val category = collection.category
        val collectionApps = packageNames flatMap (packageName => apps.find(_.packageName == packageName))
        val cards = collectionApps.zipWithIndex.map { zippedCard =>
          val (app, indexApp) = zippedCard
          CardData(
            position = indexApp,
            term = app.name,
            packageName = Option(app.packageName),
            cardType = AppCardType,
            intent = toNineCardIntent(app))
        }
        CollectionData(
          position = index,
          name = category.getName,
          collectionType = AppsCollectionType,
          icon = collection.category.name.toLowerCase,
          themedColorIndex = index % numSpaces,
          cards = cards,
          appsCategory = Option(category))
      }
    }

    if (collections.isEmpty) {
      TaskService.left(WizardNoCollectionsSelectedException("No collections selected"))
    } else {
      for {
        _ <- visibilityUiActions.hideSecondStepAndShowLoadingSavingCollection()
        apps <- di.deviceProcess.getSavedApps(GetByName)
        _ <- di.collectionProcess.createCollectionsFromCollectionData(toCollectionData(apps))
        _ <- di.deviceProcess.generateDockApps(defaultDockAppsSize)
        _ <- visibilityUiActions.showNewConfiguration()
        _ <- newConfigurationActions.loadThirdStep()
      } yield ()
    }
  }

  def loadMomentWithWifi(): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.hideThirdStep()
      _ <- configureWithWifi()
    } yield ()

  def rollbackMomentWithWifi(): TaskService[Unit] =
    for {
      _ <- wizardUiActions.showErrorGeneral()
      _ <- visibilityUiActions.cleanNewConfiguration()
      _ <- di.momentProcess.deleteAllMoments()
      _ <- di.widgetsProcess.deleteAllWidgets()
      _ <- configureWithWifi()
    } yield ()

  private[this] def configureWithWifi(): TaskService[Unit] =
    for {
      wifis <- di.deviceProcess.getConfiguredNetworks
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadFourthStep(wifis, Seq(
        (HomeMorningMoment, true),
        (WorkMoment, false),
        (StudyMoment, false)))
    } yield ()

  def saveMomentsWithWifi(infoMoment: Seq[(NineCardsMoment, Option[String])]): TaskService[Unit] = {
    val homeNightMoment = infoMoment find (_._1 == HomeMorningMoment) map (info => (HomeNightMoment, info._2))
    val momentsToAdd: Seq[(NineCardsMoment, Option[String])] = (infoMoment :+ (NineCardsMoment.defaultMoment, None)) ++ Seq(homeNightMoment).flatten

    val momentsWithWifi = momentsToAdd map {
      case (moment, wifi) =>
        MomentData(
          collectionId = None,
          timeslot = moment.toMomentTimeSlot,
          wifi = wifi.toSeq,
          headphone = false,
          momentType = moment)
    }

    for {
      _ <- trackMomentTasks(momentsWithWifi)
      _ <- visibilityUiActions.showLoadingSavingMoments()
      momentsSaved <- di.momentProcess.saveMoments(momentsWithWifi)
      nineCardsMoments = momentsSaved map (_.momentType)
      widgetsByMoment <- di.collectionProcess.rankWidgetsByMoment(limitWidgets, nineCardsMoments)
      _ <- di.widgetsProcess.addWidgets(toSeqWidgetData(momentsSaved, widgetsByMoment))
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- newConfigurationActions.loadFifthStep()
    } yield ()
  }

  def saveMoments(moments: Seq[NineCardsMoment]): TaskService[Unit] = {

    val momentsWithoutWifi = moments map { moment =>
      MomentData(
        collectionId = None,
        timeslot = moment.toMomentTimeSlot,
        wifi = Seq.empty,
        headphone = false,
        momentType = moment)
    }

    for {
      _ <- trackMomentTasks(momentsWithoutWifi)
      _ <- visibilityUiActions.showLoadingSavingMoments()
      momentsSaved <- di.momentProcess.saveMoments(momentsWithoutWifi)
      nineCardsMoments = momentsSaved map (_.momentType)
      widgetsByMoment <- di.collectionProcess.rankWidgetsByMoment(limitWidgets, nineCardsMoments)
      _ <- di.widgetsProcess.addWidgets(toSeqWidgetData(momentsSaved, widgetsByMoment))
    } yield ()
  }

  private[this] def trackMomentTasks(moments: Seq[MomentData]): TaskService[Unit] = {
    val tasks = moments map { moment =>
      (for {
          _ <- di.trackEventProcess.chooseMoment(moment.momentType)
          _ <- if(moment.wifi.nonEmpty) di.trackEventProcess.chooseMomentWifi(moment.momentType) else TaskService.empty
        } yield ()).value
    }
    TaskService {
      Task.gatherUnordered(tasks) map (_ => Right((): Unit))
    }
  }

}
