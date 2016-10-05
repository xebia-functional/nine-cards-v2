package cards.nine.app.receivers.jobs

import cards.nine.app.commons.{BroadAction, Conversions}
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.action_filters.{AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.ApplicationData
import cards.nine.process.commons.models.Collection
import cats.implicits._
import macroid.ContextWrapper
import monix.eval.Task

class AppBroadcastJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions {

  def addApp(packageName: String) = {

    def insertAppInCollectionIfExist(maybeCollection: Option[Collection], app: ApplicationData) = maybeCollection match {
      case Some(collection) => di.collectionProcess.addCards(collection.id, Seq(toAddCardRequest(app)))
      case _ => TaskService(Task(Either.right((): Unit)))
    }

    for {
      app <- di.deviceProcess.saveApp(packageName)
      collection <- di.collectionProcess.getCollectionByCategory(app.category)
      _ <- insertAppInCollectionIfExist(collection, app)
      _ <- di.collectionProcess.updateNoInstalledCardsInCollections(packageName)
      _ <- sendBroadCastTask(BroadAction(AppInstalledActionFilter.action))
    } yield (): Unit
  }

  def deleteApp(packageName: String) =
    for {
      _ <- di.deviceProcess.deleteApp(packageName)
      _ <- di.collectionProcess.deleteAllCardsByPackageName(packageName)
      _ <- sendBroadCastTask(BroadAction(AppUninstalledActionFilter.action))
    } yield (): Unit

  def updateApp(packageName: String) =
    for {
      _ <- di.deviceProcess.updateApp(packageName)
      _ <- sendBroadCastTask(BroadAction(AppUpdatedActionFilter.action))
    } yield (): Unit

}
