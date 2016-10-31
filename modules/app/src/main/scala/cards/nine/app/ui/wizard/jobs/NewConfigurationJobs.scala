package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Moment.MomentTimeSlotOps
import cards.nine.models._
import cards.nine.models.types._
import cats.data.EitherT
import macroid.ActivityContextWrapper
import monix.eval.Task

class NewConfigurationJobs(visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  val defaultDockAppsSize = 4

  def loadBetterCollections(): TaskService[Seq[PackagesByCategory]] = {

    for {
      _ <- visibilityUiActions.hideFistStepAndShowLoadingBetterCollections()
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.synchronizeInstalledApps
      collections <- di.collectionProcess.rankApps()
      finalCollections = collections filter (collection => collection.category != Misc && collection.packages.length >= 3)
    } yield finalCollections
  }

  def saveCollections(collections: Seq[PackagesByCategory], best9Apps: Boolean): TaskService[Unit] = {

    def toCollectionData(apps: Seq[ApplicationData]) = {
      collections.zipWithIndex.map { zipped =>
        val (collection, index) = zipped
        val packageNames = if (best9Apps) collection.packages.take(9) else collection.packages
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

    for {
      - <- di.trackEventProcess.chooseAppNumber(best9Apps)
      _ <- visibilityUiActions.hideSecondStepAndShowLoadingSavingCollection()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- di.collectionProcess.createCollectionsFromCollectionData(toCollectionData(apps))
      _ <- di.deviceProcess.generateDockApps(defaultDockAppsSize)
    } yield ()
  }

  def loadMomentWithWifi(): TaskService[Seq[String]] =
    for {
      _ <- visibilityUiActions.hideThirdStep()
      wifis <- di.deviceProcess.getConfiguredNetworks
    } yield wifis

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
      - <- trackMomentTasks(momentsWithWifi)
      _ <- visibilityUiActions.fadeOutInAllChildInStep
      _ <- di.momentProcess.saveMoments(momentsWithWifi)
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
      - <- trackMomentTasks(momentsWithoutWifi)
      _ <- visibilityUiActions.showLoadingSavingMoments()
      _ <- di.momentProcess.saveMoments(momentsWithoutWifi)
    } yield ()
  }

  private[this] def trackMomentTasks(moments: Seq[MomentData]): TaskService[Unit] = {
    val tasks = moments map { moment =>
      (for {
          _ <- di.trackEventProcess.chooseMoment(moment.momentType)
          _ <- if(moment.wifi.nonEmpty) di.trackEventProcess.chooseMomentWifi(moment.momentType) else TaskService.right(Unit)
        } yield ()).value
    }
    TaskService {
      Task.gatherUnordered(tasks) map (_ => Right((): Unit))
    }
  }

}
