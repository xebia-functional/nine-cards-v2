package com.fortysevendeg.ninecardslauncher.app.ui.launcher.actions.publicollections

import com.fortysevendeg.ninecardslauncher.app.commons.Conversions
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{LauncherExecutor, Presenter}
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Communication, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.GetByName
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{SharedCollection, SharedCollectionPackage}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{TopSharedCollection, TypeSharedCollection}
import macroid.{ActivityContextWrapper, Ui}

import scalaz.concurrent.Task

class PublicCollectionsPresenter (actions: PublicCollectionsUiActions)(implicit contextWrapper: ActivityContextWrapper)
  extends Presenter
  with LauncherExecutor
  with Conversions {

  protected var statuses = PublicCollectionStatuses(Communication, TopSharedCollection)

  def initialize(): Unit = {
    loadPublicCollections()
    actions.initialize().run
  }

  def loadPublicCollectionsByCategory(category: NineCardCategory): Unit = {
    statuses = statuses.copy(category = category)
    actions.updateCategory(category).run
    loadPublicCollections()
  }

  def loadPublicCollectionsByTypeSharedCollection(typeSharedCollection: TypeSharedCollection): Unit = {
    statuses = statuses.copy(typeSharedCollection = typeSharedCollection)
    actions.updateTypeCollection(typeSharedCollection).run
    loadPublicCollections()
  }

  def loadPublicCollections(): Unit = {
    Task.fork(getSharedCollections(statuses.category, statuses.typeSharedCollection).value).resolveAsyncUi(
      onPreTask = () => actions.showLoading(),
      onResult = (sharedCollections: Seq[SharedCollection]) => {
        if (sharedCollections.isEmpty) {
          actions.showEmptyMessageInScreen()
        } else {
          actions.loadPublicCollections(sharedCollections)
        }
      },
      onException = (ex: Throwable) => actions.showErrorLoadingCollectionInScreen())
  }

  def saveSharedCollection(sharedCollection: SharedCollection): Unit = {
    Task.fork(addCollection(sharedCollection).value).resolveAsyncUi(
      onResult = (c) => actions.addCollection(c) ~ actions.close(),
      onException = (ex) => actions.showErrorSavingCollectionInScreen())
  }

  private[this] def getSharedCollections(
    category: NineCardCategory,
    typeSharedCollection: TypeSharedCollection): TaskService[Seq[SharedCollection]] =
    di.sharedCollectionsProcess.getSharedCollectionsByCategory(category, typeSharedCollection)

  private[this] def addCollection(sharedCollection: SharedCollection):
  TaskService[Collection] =
    for {
      appsInstalled <- di.deviceProcess.getSavedApps(GetByName)
      collection <- di.collectionProcess.addCollection(toAddCollectionRequest(sharedCollection, getCards(appsInstalled, sharedCollection.resolvedPackages)))
    } yield collection

  private[this] def getCards(appsInstalled: Seq[App], packages: Seq[SharedCollectionPackage]) =
    packages map { pck =>
      appsInstalled find (_.packageName == pck.packageName) map { app =>
        toAddCardRequest(app)
      } getOrElse toAddCardRequest(pck)
    }

}

trait PublicCollectionsUiActions {

  def initialize(): Ui[Any]

  def showLoading(): Ui[Any]

  def showErrorLoadingCollectionInScreen(): Ui[Any]

  def showErrorSavingCollectionInScreen(): Ui[Any]

  def showEmptyMessageInScreen(): Ui[Any]

  def addCollection(collection: Collection): Ui[Any]

  def loadPublicCollections(sharedCollections: Seq[SharedCollection]): Ui[Any]

  def updateCategory(category: NineCardCategory): Ui[Any]

  def updateTypeCollection(typeSharedCollection: TypeSharedCollection): Ui[Any]

  def close(): Ui[Any]

}

case class PublicCollectionStatuses(category: NineCardCategory, typeSharedCollection: TypeSharedCollection)