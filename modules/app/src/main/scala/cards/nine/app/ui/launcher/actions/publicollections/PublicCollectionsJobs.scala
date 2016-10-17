package cards.nine.app.ui.launcher.actions.publicollections

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.collections.tasks.CollectionJobs
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.launcher.actions.publicollections.PublicCollectionsFragment._
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardCategory
import cards.nine.process.sharedcollections.TypeSharedCollection
import cards.nine.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.macroid.extras.ResourcesExtras._
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
      category: NineCardCategory,
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

  def loadPublicCollectionsByCategory(category: NineCardCategory): TaskService[Unit] = {
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

  def saveSharedCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    for {
      collections <- addSharedCollection(sharedCollection)
      _ <- actions.addCollection(collections)
      _ <- actions.close()
    } yield ()

  def shareCollection(sharedCollection: SharedCollection): TaskService[Unit] =
    di.launcherExecutorProcess.launchShare(resGetString(R.string.shared_collection_url, sharedCollection.id))

}
