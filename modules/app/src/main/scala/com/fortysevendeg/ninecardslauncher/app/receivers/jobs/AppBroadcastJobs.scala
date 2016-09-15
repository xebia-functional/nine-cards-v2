package com.fortysevendeg.ninecardslauncher.app.receivers.jobs

import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, Conversions}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Jobs
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters.{AppInstalledActionFilter, AppUninstalledActionFilter, AppUpdatedActionFilter}
import macroid.ContextWrapper
import cats.implicits._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import monix.eval.Task

class AppBroadcastJobs(implicit contextWrapper: ContextWrapper)
  extends Jobs
  with Conversions {

  def addApp(packageName: String) = {

    def insertAppInCollectionIfExist(maybeCollection: Option[Collection], app: App) = maybeCollection match {
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
    di.deviceProcess.deleteApp(packageName) *>
      di.collectionProcess.deleteAllCardsByPackageName(packageName) *>
      sendBroadCastTask(BroadAction(AppUninstalledActionFilter.action))

  def updateApp(packageName: String) =
    di.deviceProcess.updateApp(packageName) *>
      sendBroadCastTask(BroadAction(AppUpdatedActionFilter.action))

}
