package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.privatecollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{Presenter, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.CollectionException
import com.fortysevendeg.ninecardslauncher.process.commons.models._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{AppCardType, AppsCollectionType, Social}
import com.fortysevendeg.ninecardslauncher.process.device.{AppException, GetByName}
import com.fortysevendeg.ninecardslauncher.process.moment.{MomentConversions, MomentException}
import macroid.{ActivityContextWrapper, Ui}
import rapture.core.Answer

import scalaz.concurrent.Task

class PrivateCollectionsPresenter(actions: PrivateCollectionsActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with Conversions
  with MomentConversions {

  def initialize(): Unit = {
    loadPrivateCollections()
    actions.initialize().run
  }

  def loadPrivateCollections(): Unit = {
    Task.fork(getPrivateCollections.run).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (privateCollections: Seq[PrivateCollection]) => {
        if (privateCollections.isEmpty) {
          actions.showEmptyMessage()
        } else {
          actions.addPrivateCollections(privateCollections)
        }
      },
      onException = (ex: Throwable) => actions.showContactUsError())
  }

  def saveCollection(privateCollection: PrivateCollection): Unit = {
    Task.fork(di.collectionProcess.addCollection(toAddCollectionRequest(privateCollection)).run).resolveAsyncUi(
      onResult = (c) => actions.addCollection(c) ~ actions.close(),
      onException = (ex) => actions.showContactUsError())
  }

  private[this] def getPrivateCollections:
  ServiceDef2[Seq[PrivateCollection], AppException with CollectionException with MomentException] =
    for {
      collections <- di.collectionProcess.getCollections
      moments <- di.momentProcess.getMoments
      apps <- di.deviceProcess.getSavedApps(GetByName)
      unformedApps = toSeqUnformedApp(apps)
      newCollections <- di.collectionProcess.generatePrivateCollections(unformedApps)
      newMomentCollections <- di.momentProcess.generatePrivateMoments(unformedApps map toApp, newCollections.length)
    } yield {
      val privateCollections = newCollections filterNot { newCollection =>
        newCollection.appsCategory match {
          case Some(category) => (collections flatMap (_.appsCategory)) contains category
          case _ => false
        }
      }
      val privateMoments = newMomentCollections filterNot (newMomentCollection => moments map (_.momentType) contains newMomentCollection.moment)
      privateCollections ++ privateMoments
    }

}

trait PrivateCollectionsActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showContactUsError(): Ui[Any]

  def showEmptyMessage(): Ui[Any]

  def addPrivateCollections(privateCollections: Seq[PrivateCollection]): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def close(): Ui[Any]
}