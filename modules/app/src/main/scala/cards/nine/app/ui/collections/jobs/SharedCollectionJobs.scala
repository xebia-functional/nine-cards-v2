package cards.nine.app.ui.collections.jobs

import cards.nine.app.commons.{AppNineCardsIntentConversions, Conversions}
import cards.nine.app.ui.collections.jobs.uiactions.SharedCollectionUiActions
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.models.Collection
import cards.nine.models.types.AppCardType
import macroid.ActivityContextWrapper

class SharedCollectionJobs(val actions: SharedCollectionUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with AppNineCardsIntentConversions { self =>

  def reloadSharedCollectionId(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id)
        .resolveOption(s"Can't find the collection with id ${currentCollection.id}")
      areDifferentCollections = databaseCollection.sharedCollectionId != currentCollection.sharedCollectionId
      _ <- actions.reloadSharedCollectionId(databaseCollection.sharedCollectionId).resolveIf(areDifferentCollections, (): Unit)
    } yield (): Unit

  def showPublishCollectionWizard(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      _ <- if (currentCollection.cards.exists(_.cardType == AppCardType)) {
        actions.showPublishCollectionWizardDialog(currentCollection)
      } else {
        actions.showMessagePublishContactsCollectionError
      }
    } yield (): Unit

  def shareCollection(): TaskService[Unit] =
    for {
      currentCollection <- fetchCurrentCollection
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id)
        .resolveOption(s"Can't find the collection with id ${currentCollection.id}")
      _ <- (databaseCollection.sharedCollectionId, databaseCollection.getUrlSharedCollection) match {
        case (Some(_), Some(url)) => di.launcherExecutorProcess.launchShare(url)
        case _ => actions.showMessageNotPublishedCollectionError
      }
    } yield (): Unit

  private[this] def fetchCurrentCollection: TaskService[Collection] =
    actions.getCurrentCollection.resolveOption("Can't find the current collection in the UI")

}
