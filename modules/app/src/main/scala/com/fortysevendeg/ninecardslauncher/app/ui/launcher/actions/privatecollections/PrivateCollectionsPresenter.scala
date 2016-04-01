package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.Presenter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException, PrivateCollection}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class PrivateCollectionsPresenter(actions: PrivateCollectionsActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with Conversions {

  def loadPrivateCollections(): Ui[Any] = Ui {
    Task.fork(getPrivateCollections.run).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (privateCollections: Seq[PrivateCollection]) => actions.addPrivateCollections(privateCollections),
      onException = (ex: Throwable) => actions.showContactUsError())
  }

  def saveCollection(privateCollection: PrivateCollection): Ui[Any] = Ui {
    Task.fork(addCollection(privateCollection).run).resolveAsyncUi(
      onResult = (c) => actions.addCollection(c),
      onException = (ex) => actions.showContactUsError())
  }

  private[this] def getPrivateCollections:
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException] =
    for {
      collections <- di.collectionProcess.getCollections
      apps <- di.deviceProcess.getSavedApps(GetByName)
      newCollections <- di.collectionProcess.generatePrivateCollections(toSeqUnformedApp(apps))
    } yield newCollections filterNot { newCollection =>
      newCollection.appsCategory match {
        case Some(category) => (collections flatMap (_.appsCategory)) contains category
        case _ => false
      }
    }

  private[this] def addCollection(privateCollection: PrivateCollection):
  ServiceDef2[Collection, CollectionException with CardException] =
    for {
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection))
      cards <- di.collectionProcess.addCards(collection.id, privateCollection.cards map toAddCollectionRequest)
    } yield collection.copy(cards = cards)

}

trait PrivateCollectionsActions {

  def showLoading(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

}