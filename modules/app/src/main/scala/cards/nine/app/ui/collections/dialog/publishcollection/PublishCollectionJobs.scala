package cards.nine.app.ui.collections.dialog.publishcollection

import cats.implicits._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import cards.nine.app.ui.commons.{JobException, Jobs}
import cards.nine.app.ui.preferences.commons.Theme
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.commons.models.Collection
import cards.nine.process.commons.types.NineCardCategory
import cards.nine.process.sharedcollections.SharedCollectionsException
import cards.nine.process.sharedcollections.models.CreateSharedCollection
import com.fortysevendeg.ninecardslauncher.R
import macroid.ActivityContextWrapper

class PublishCollectionJobs(actions: PublishCollectionActions)(implicit val contextWrapper: ActivityContextWrapper)
  extends Jobs {

  var statuses = PublishCollectionStatuses()

  def initialize(collection: Collection): TaskService[Unit] = {
    statuses = statuses.copy(collection = Some(collection))
    for {
      theme <- di.themeProcess.getTheme(Theme.getThemeFile(preferenceValues))
      _ <- actions.loadTheme(theme)
      _ <- actions.initialize()
    } yield ()
  }

  def showCollectionInformation(): TaskService[Unit] = {
    statuses.collection match {
      case Some(collection) => actions.goToPublishCollectionInformation(collection)
      case None => TaskService.left(JobException("Collection not found"))
    }
  }

  def publishCollection(maybeName: Option[String], maybeCategory: Option[NineCardCategory]): TaskService[Unit] = {

    def getCollection: TaskService[Collection] =
      statuses.collection map TaskService.right getOrElse TaskService.left(SharedCollectionsException("", None))

    def createPublishedCollection(name: String, category: NineCardCategory): TaskService[String] =
      for {
        user <- di.userProcess.getUser
        collection <- getCollection
        sharedCollection = CreateSharedCollection(
          author = user.userProfile.name getOrElse (user.email getOrElse resGetString(R.string.defaultUser)),
          name = name,
          packages = collection.cards flatMap (_.packageName),
          category = category,
          icon = collection.icon,
          community = false)
        sharedCollectionId <- di.sharedCollectionsProcess.createSharedCollection(sharedCollection)
        _ <- di.collectionProcess.updateSharedCollection(collection.id, sharedCollectionId)
      } yield sharedCollectionId

    (for {
      name <- maybeName
      category <- maybeCategory
    } yield {
      for {
        _ <- actions.goToPublishCollectionPublishing()
        sharedCollectionId <- createPublishedCollection(name, category)
        _ <- actions.goToPublishCollectionEnd(sharedCollectionId)
      } yield ()
    }) getOrElse actions.showMessageFormFieldError
  }

  def launchShareCollection(sharedCollectionId: String): TaskService[Unit] =
    di.launcherExecutorProcess
      .launchShare(resGetString(R.string.shared_collection_url, sharedCollectionId))

  def showPublishingError(maybeName: Option[String], maybeCategory: Option[NineCardCategory]): TaskService[Unit] =
    (for {
      name <- maybeName
      category <- maybeCategory
    } yield {
      actions.showMessagePublishingError *> actions.goBackToPublishCollectionInformation(name, category)
    }) getOrElse actions.showContactUsError

  def showCollectionError(): TaskService[Unit] = actions.showMessageCollectionError

  def showGenericError(): TaskService[Unit] = actions.showContactUsError

}

case class PublishCollectionStatuses(
  collection: Option[Collection] = None)
