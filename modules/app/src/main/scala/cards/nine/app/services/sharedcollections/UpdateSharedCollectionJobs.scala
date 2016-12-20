package cards.nine.app.services.sharedcollections

import android.content.Intent
import cards.nine.app.ui.commons.action_filters.AppInstalledActionFilter
import cards.nine.app.ui.commons.{BroadAction, ImplicitsJobExceptions, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class UpdateSharedCollectionJobs(actions: UpdateSharedCollectionUiActions)(
    implicit contextWrapper: ContextWrapper)
    extends Jobs
    with ImplicitsJobExceptions {

  import UpdateSharedCollectionService._

  def handleIntent(intent: Intent): TaskService[Option[String]] = {

    val (collectionId, sharedCollectionId, action, packages) = Option(intent) match {
      case Some(i) =>
        (readIntValue(i, intentExtraCollectionId),
         readStringValue(i, intentExtraSharedCollectionId),
         Option(i.getAction),
         readArrayValue(i, intentExtraPackages).getOrElse(Array.empty))
      case _ => (None, None, None, Array.empty[String])
    }

    (collectionId, sharedCollectionId, action) match {
      case (_, Some(shareCollectionId), Some(`actionUnsubscribe`)) =>
        for {
          _ <- actions.cancelNotification()
          _ <- di.sharedCollectionsProcess.unsubscribe(shareCollectionId)
          _ <- actions.showUnsubscribedMessage
        } yield Some(actionUnsubscribe)
      case (Some(id), _, Some(`actionSync`)) =>
        for {
          _ <- actions.cancelNotification()
          _ <- di.collectionProcess.addPackages(id, packages.toSeq)
          _ <- sendBroadCastTask(BroadAction(AppInstalledActionFilter.action))
          _ <- actions.showCollectionUpdatedMessage
        } yield Some(actionSync)
      case _ => TaskService.right(None)
    }

  }

}
