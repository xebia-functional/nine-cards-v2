package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models._
import cards.nine.models.types._
import macroid.ActivityContextWrapper

class NewConfigurationJobs(visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  val defaultDockAppsSize = 4

  def loadBetterCollections(): TaskService[Seq[PackagesByCategory]] = {

    // For now, we are looking the better experience and we are filtering the collections
    // This should be implemented by the backend
    def filterApps(collections: Seq[PackagesByCategory]) = {
      val gamePackages = collections filter (_.category.isGameCategory) flatMap (_.packages)
      val list = (collections filterNot (collection => collection.category.isGameCategory || collection.category == Misc)) :+ PackagesByCategory(Game, gamePackages)
      list.filter(_.packages.length >= 4)
    }

    for {
      _ <- visibilityUiActions.hideFistStepAndShowLoadingBetterCollections()
      _ <- di.deviceProcess.resetSavedItems()
      _ <- di.deviceProcess.synchronizeInstalledApps
      collections <- di.collectionProcess.rankApps()
      finalCollections = filterApps(collections)
    } yield finalCollections
  }

  def saveCollections(collections: Seq[PackagesByCategory], best9Apps: Boolean): TaskService[Unit] = {

    def toFormedCollection(apps: Seq[ApplicationData]) = {
      collections map { collection =>
        val packageNames = if (best9Apps) collection.packages.take(9) else collection.packages
        val category = collection.category
        val collectionApps = apps.filter(app => packageNames.contains(app.packageName))
        val formedItems = collectionApps map { app =>
          FormedItem(
            itemType = AppCardType.name,
            title = app.name,
            intent = nineCardIntentToJson(toNineCardIntent(app)),
            uriImage = None)
        }
        FormedCollection(
          name = category.getName,
          originalSharedCollectionId = None,
          sharedCollectionId = None,
          sharedCollectionSubscribed = None,
          items = formedItems,
          collectionType = AppsCollectionType,
          icon = collection.category.name.toLowerCase,
          category = Option(category),
          moment = None)
      }
    }

    for {
      _ <- visibilityUiActions.hideSecondStepAndShowLoadingSavingCollection()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- di.collectionProcess.createCollectionsFromFormedCollections(toFormedCollection(apps))
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
    val momentsToAdd: Seq[(NineCardsMoment, Option[String])] = (infoMoment :+ (WalkMoment, None)) ++ Seq(homeNightMoment).flatten

    val momentsWithWifi = momentsToAdd map {
      case (moment, wifi) =>
        MomentData(
          collectionId = None,
          timeslot = toMomentTimeSlotSeq(moment),
          wifi = wifi.toSeq,
          headphone = false,
          momentType = Option(moment))
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
        timeslot = toMomentTimeSlotSeq(moment),
        wifi = Seq.empty,
        headphone = false,
        momentType = Option(moment))
    }

    for {
      _ <- visibilityUiActions.showLoadingSavingMoments()
      _ <- di.momentProcess.saveMoments(momentsWithoutWifi)
    } yield ()
  }

  private[this] def toMomentTimeSlotSeq(moment: NineCardsMoment): Seq[MomentTimeSlot] =
    moment match {
      case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
      case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
      case MusicMoment => Seq.empty
      case CarMoment => Seq.empty
      case RunningMoment => Seq.empty
      case BikeMoment => Seq.empty
      case WalkMoment => Seq(MomentTimeSlot(from = "00:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)))
    }

}
