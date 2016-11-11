package cards.nine.app.ui.launcher.actions.publicollections

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment._
import cards.nine.commons.services.TaskService.{TaskService, _}
import cards.nine.models.{Collection, SharedCollection}
import cards.nine.models.types.{NineCardsCategory, TypeSharedCollection}
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class PublicCollectionsJobs(actions: PublicCollectionsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions
  with CollectionJobs {

  def initialize(): TaskService[Unit] =
    for {
      _ <- loadPublicCollections()
      _ <- actions.initialize()
    } yield ()

  def loadPublicCollections(): TaskService[Unit] = {

    def getSharedCollections(
      category: NineCardsCategory,
      typeSharedCollection: TypeSharedCollection): TaskService[Seq[SharedCollection]] =
      di.sharedCollectionsProcess.getSharedCollectionsByCategory(category, typeSharedCollection)

    for {
      _ <- actions.showLoading()
      sharedCollections <- getSharedCollections(statuses.category, statuses.typeSharedCollection)
      _ <- if (sharedCollections.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.loadPublicCollections(sharedCollections)
      }
    } yield ()
  }

  def loadPublicCollectionsByCategory(category: NineCardsCategory): TaskService[Unit] = {
    statuses = statuses.copy(category = category)
    for {
      _ <- actions.updateCategory(category)
      _ <- loadPublicCollections()
    } yield ()
  }

  def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): TaskService[Unit] = {
    statuses = statuses.copy(typeSharedCollection = typeSharedCollection)
    for {
      _ <- actions.updateTypeCollection(typeSharedCollection)
      _ <- loadPublicCollections()
    } yield ()
  }

  def saveSharedCollection(sharedCollection: SharedCollection): TaskService[Collection] =
    for {
      collection <- addSharedCollection(sharedCollection)
      _ <- actions.close()
    } yield collection

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))

}
