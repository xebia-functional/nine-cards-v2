package cards.nine.app.ui.launcher.actions.privatecollections

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Collection, CollectionData}
import cards.nine.models.types.GetByName
import macroid.ActivityContextWrapper

class PrivateCollectionsJobs(actions: PrivateCollectionsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadPrivateCollections()
    } yield ()

  def loadPrivateCollections(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      collections <- di.collectionProcess.getCollections
      moments <- di.momentProcess.getMoments
      apps <- di.deviceProcess.getSavedApps(GetByName)
      newCollections <- di.collectionProcess.generatePrivateCollections(apps)
      privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) => (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      _ <- if (privateCollections.isEmpty) {
        actions.showEmptyMessageInScreen()
      } else {
        actions.addPrivateCollections(privateCollections)
      }
    } yield ()

  def saveCollection(collection: CollectionData): TaskService[Collection] =
    for {
      collectionAdded <- di.collectionProcess.addCollection(collection)
      _ <- actions.close()
    } yield collectionAdded

}
