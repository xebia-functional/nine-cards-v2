package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Constants._
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.app.ui.wizard.WizardNoCollectionsSelectedException
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.Moment.MomentTimeSlotOps
import cards.nine.models._
import cards.nine.models.types._
import macroid.ActivityContextWrapper

class NewConfigurationJobs(visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  val defaultDockAppsSize = 4

  def loadBetterCollections(hidePrevious: Boolean = true): TaskService[Seq[PackagesByCategory]] = {

    for {
      _ <- visibilityUiActions.hideFistStepAndShowLoadingBetterCollections(hidePrevious)
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.synchronizeInstalledApps
      collections <- di.collectionProcess.rankApps()
      finalCollections = collections filter (collection => collection.category != Misc)
    } yield finalCollections
  }

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
      } yield ()
    }
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
      _ <- visibilityUiActions.showLoadingSavingMoments()
      _ <- di.momentProcess.saveMoments(momentsWithoutWifi)
    } yield ()
  }

}
