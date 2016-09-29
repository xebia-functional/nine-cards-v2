package com.fortysevendeg.ninecardslauncher.app.ui.collections.jobs

import com.fortysevendeg.ninecardslauncher.app.commons.{Conversions, NineCardIntentConversions}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.CollectionOps._
import cards.nine.commons.services.TaskService._
import cards.nine.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppCardType
import macroid.ActivityContextWrapper

class SharedCollectionJobs(actions: SharedCollectionUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
    with Conversions
    with NineCardIntentConversions { self =>

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
