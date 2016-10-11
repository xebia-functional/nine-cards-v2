package cards.nine.app.ui.launcher.actions.privatecollections

import cards.nine.app.commons.Conversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.commons.services.TaskService._
import macroid.{ActivityContextWrapper, Ui}

class PrivateCollectionsPresenter(actions: PrivateCollectionsActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Jobs
  with Conversions {

  def initialize(): Unit = {
    loadPrivateCollections()
    actions.initialize().run
  }

  def loadPrivateCollections(): Unit = {
    getPrivateCollections.resolveAsyncUi2(
      onPreTask = () => actions.showLoading(),
      onResult = (privateCollections: Seq[Collection]) => {
        if (privateCollections.isEmpty) {
          actions.showEmptyMessageInScreen()
        } else {
          actions.addPrivateCollections(privateCollections)
        }
      },
      onException = (ex: Throwable) => actions.showErrorLoadingCollectionInScreen())
  }

  def saveCollection(privateCollection: PrivateCollection): Unit = {
    di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection)).resolveAsyncUi2(
      onResult = (c) => actions.addCollection(c) ~ actions.close(),
      onException = (ex) => actions.showErrorSavingCollectionInScreen())
  }

  private[this] def getPrivateCollections:
  TaskService[Seq[PrivateCollection]] =
    for {
      collections <- di.collectionProcess.getCollections
      moments <- di.momentProcess.getMoments
      apps <- di.deviceProcess.getSavedApps(GetByName)
      newCollections <- di.collectionProcess.generatePrivateCollections(apps)
    } yield {
      val privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) => (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      privateCollections
    }

}

trait PrivateCollectionsActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorLoadingCollectionInScreen(): Ui[Any]

  def showErrorSavingCollectionInScreen(): Ui[Any]

  def showEmptyMessageInScreen(): Ui[Any]

  def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def close(): Ui[Any]
}