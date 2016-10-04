package cards.nine.app.ui.collections.jobs

import cards.nine.app.commons.{Conversions, AppNineCardIntentConversions}
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.CollectionOps._
import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import cards.nine.models.types.AppCardType
import macroid.ActivityContextWrapper

class SharedCollectionJobs(actions: SharedCollectionUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with AppNineCardIntentConversions { self =>

  def reloadSharedCollectionId(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id).resolveOption()
      areDifferentCollections = databaseCollection.sharedCollectionId != currentCollection.sharedCollectionId
      _ <- actions.reloadSharedCollectionId(databaseCollection.sharedCollectionId).resolveIf(areDifferentCollections, (): Unit)
    } yield (): Unit

  def showPublishCollectionWizard(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      _ <- if (currentCollection.cards.exists(_.cardType == AppCardType)) {
        actions.showPublishCollectionWizardDialog(currentCollection)
      } else {
        actions.showMessagePublishContactsCollectionError
      }
    } yield (): Unit

  def shareCollection(): TaskService[Unit] =
    for {
      currentCollection <- actions.getCurrentCollection.resolveOption()
      databaseCollection <- di.collectionProcess.getCollectionById(currentCollection.id).resolveOption()
      _ <- (databaseCollection.sharedCollectionId, databaseCollection.getUrlSharedCollection) match {
        case (Some(_), Some(url)) => di.launcherExecutorProcess.launchShare(url)
        case _ => actions.showMessageNotPublishedCollectionError
      }
    } yield (): Unit

}
