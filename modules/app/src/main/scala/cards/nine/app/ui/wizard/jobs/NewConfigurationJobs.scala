package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.NineCardIntentConversions
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types._
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.process.collection.models.{FormedCollection, FormedItem, PackagesByCategory}
import cards.nine.process.commons.CommonConversions
import cards.nine.process.commons.models.MomentTimeSlot
import cards.nine.process.device.GetByName
import cards.nine.process.device.models.App
import cards.nine.process.moment.SaveMomentRequest
import macroid.ActivityContextWrapper
import play.api.libs.json.Json

class NewConfigurationJobs(
  actions: NewConfigurationUiActions,
  visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions
  with CommonConversions {

  val defaultDockAppsSize = 4

  def loadBetterCollections(): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.showLoadingBetterCollections()
      collections <- di.collectionProcess.rankApps()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- actions.loadSecondStep(apps.length, collections)
    } yield ()

  def saveCollections(collections: Seq[PackagesByCategory], best9Apps: Boolean): TaskService[Unit] = {

    def toFormedCollection(apps: Seq[App]) = {
      collections map { collection =>
        val packageNames = if (best9Apps) collection.packages.take(9) else collection.packages
        val category = NineCardCategory(collection.category)
        val collectionApps = apps.filter(app => packageNames.contains(app.packageName))
        val formedItems = collectionApps map { app =>
          FormedItem(
            itemType = AppCardType.name,
            title = app.name,
            intent = Json.toJson(toNineCardIntent(app)).toString(),
            uriImage = None)
        }
        FormedCollection(
          name = category.getName,
          originalSharedCollectionId = None,
          sharedCollectionId = None,
          sharedCollectionSubscribed = None,
          items = formedItems,
          collectionType = AppsCollectionType,
          icon = collection.category.toLowerCase,
          category = Option(category),
          moment = None)
      }
    }

    for {
      _ <- visibilityUiActions.showLoadingSavingCollection()
      apps <- di.deviceProcess.getSavedApps(GetByName)
      _ <- di.collectionProcess.createCollectionsFromFormedCollections(toFormedCollection(apps))
      _ <- di.deviceProcess.generateDockApps(defaultDockAppsSize)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- actions.loadThirdStep()
    } yield ()
  }

  def loadMomentWithWifi(): TaskService[Unit] =
    for {
      _ <- visibilityUiActions.cleanStep()
      wifis <- di.deviceProcess.getConfiguredNetworks
      _ <- actions.loadFourthStep(wifis, Seq(
        (HomeMorningMoment, true),
        (WorkMoment, false),
        (StudyMoment, false)))
    } yield ()

  def saveMoments(infoMoment: Seq[(NineCardsMoment, Option[String])]): TaskService[Unit] = {

    def toMomentTimeSlotSeq(moment: NineCardsMoment): Seq[MomentTimeSlot] =
      moment match {
        case HomeMorningMoment => Seq(MomentTimeSlot(from = "08:00", to = "19:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case WorkMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case HomeNightMoment => Seq(MomentTimeSlot(from = "19:00", to = "23:59", days = Seq(1, 1, 1, 1, 1, 1, 1)), MomentTimeSlot(from = "00:00", to = "08:00", days = Seq(1, 1, 1, 1, 1, 1, 1)))
        case StudyMoment => Seq(MomentTimeSlot(from = "08:00", to = "17:00", days = Seq(0, 1, 1, 1, 1, 1, 0)))
        case MusicMoment => Seq.empty
        case CarMoment => Seq.empty
        case RunningMoment => Seq.empty
        case BikeMoment => Seq.empty
        case WalkMoment => Seq.empty
      }

    val homeNightMoment = infoMoment find (_._1 == HomeMorningMoment) map (info => (HomeNightMoment, info._2))

    val momentsToAdd: Seq[(NineCardsMoment, Option[String])] = (infoMoment :+ (WalkMoment, None)) ++ Seq(homeNightMoment).flatten

    val request = momentsToAdd map {
      case (moment, wifi) =>
        SaveMomentRequest(
          collectionId = None,
          timeslot = toMomentTimeSlotSeq(moment),
          wifi = Seq(wifi).flatten,
          headphone = false,
          momentType = Option(moment),
          widgets = None)
    }
    for {
      _ <- visibilityUiActions.showLoadingSavingMoments()
      _ <- di.momentProcess.saveMoments(request)
      _ <- visibilityUiActions.showNewConfiguration()
      _ <- actions.loadFifthStep()
    } yield ()
  }

}
