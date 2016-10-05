package cards.nine.app.ui.wizard.jobs

import cards.nine.app.commons.AppNineCardIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.NineCardsCategoryOps._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.ApplicationData
import cards.nine.models.types._
import cards.nine.process.collection.models.{FormedCollection, FormedItem, PackagesByCategory}
import cards.nine.process.commons.models.NineCardIntentImplicits._
import cards.nine.process.device.GetByName
import macroid.ActivityContextWrapper
import play.api.libs.json.Json

class NewConfigurationJobs(
  actions: NewConfigurationUiActions,
  visibilityUiActions: VisibilityUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with AppNineCardIntentConversions {

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

    def toFormedCollection(apps: Seq[ApplicationData]) = {
      collections map { collection =>
        val packageNames = if (best9Apps) collection.packages.take(9) else collection.packages
        val category = collection.category
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
          icon = collection.category.name.toLowerCase,
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
      _ <- actions.loadFourthStep(wifis, Seq(HomeMorningMoment, WorkMoment))
    } yield ()

}
