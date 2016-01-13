package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, FormedCollection, UnformedApp, UnformedContact}
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCollectionRequest, CollectionException, EditCollectionRequest}
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.persistence.{DeleteCollectionRequest => ServicesDeleteCollectionRequest, FindCollectionByIdRequest, ImplicitsPersistenceServiceExceptions, PersistenceServiceException}
import rapture.core.Answer

import scalaz.concurrent.Task

trait CollectionsProcessImpl {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with FormedCollectionDependencies
    with ImplicitsPersistenceServiceExceptions =>

  val minAppsGenerateCollections = 1

  def createCollectionsFromUnformedItems(apps: Seq[UnformedApp], contacts: Seq[UnformedContact])(implicit context: ContextSupport) = Service {
    val tasks = createCollections(apps, contacts, appsCategories, minAppsToAdd) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    (for {
      apps <- appsServices.getInstalledApplications
      collections <- createCollectionsAndFillData(items, apps)
    } yield collections).resolve[CollectionException]

  def generatePrivateCollections(apps: Seq[UnformedApp])(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[CollectionException] {
        createPrivateCollections(apps, appsCategories, minAppsGenerateCollections)
      }
    }
  }

  def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  def getCollectionById(id: Int) =
    (for {
      collection <- persistenceServices.findCollectionById(FindCollectionByIdRequest(id))
    } yield collection map toCollection).resolve[CollectionException]

  def addCollection(addCollectionRequest: AddCollectionRequest) =
    (for {
      collectionList <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(toAddCollectionRequest(addCollectionRequest, collectionList.size))
    } yield toCollection(collection)).resolve[CollectionException]

  def deleteCollection(collectionId: Int) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      _ <- persistenceServices.deleteCardsByCollection(collectionId)
      collectionList <- getCollections
      _ <- updateCollectionList(moveCollectionList(collectionList, collection.position))
    } yield ()).resolve[CollectionException]

  def reorderCollection(position: Int, newPosition: Int) =
    (for {
      Some(collection) <- persistenceServices.fetchCollectionByPosition(toFetchCollectionByPositionRequest(position))
      collectionList <- getCollections
      _ <- updateCollectionList(reorderCollectionList(collectionList, newPosition, position))
    } yield ()).resolve[CollectionException]

  def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedCollection(toCollection(collection), editCollectionRequest)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  private[this] def createCollectionsAndFillData(items: Seq[FormedCollection], apps: Seq[Application])
    (implicit context: ContextSupport): ServiceDef2[List[Collection], PersistenceServiceException] = Service {
    val tasks = toAddCollectionRequestByFormedCollection(fillImageUri(items, apps)) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }

  private[this] def moveCollectionList(collectionList: Seq[Collection], position: Int) =
    collectionList map { collection =>
      if (collection.position > position) collection.copy(position = collection.position - 1) else collection
    }

  private[this] def reorderCollectionList(collectionList: Seq[Collection], newPosition: Int, oldPosition: Int): Seq[Collection] =
    collectionList map { collection =>
      val position = collection.position
      (newPosition, oldPosition, position) match {
        case (n, o, p) if n < o && p > n && p < o => collection.copy(position = p + 1)
        case (n, o, p) if n > o && p < n && p > o => collection.copy(position = p - 1)
        case (n, o, _) if n < o || n > o => collection
        case _ => collection.copy(position = newPosition)
      }
    }

  private[this] def findCollectionById(id: Int) =
    (for {
      collection <- persistenceServices.findCollectionById(toFindCollectionByIdRequest(id))
    } yield collection).resolve[CollectionException]

  private[this] def updateCollection(collection: Collection) =
    (for {
      _ <- persistenceServices.updateCollection(toServicesUpdateCollectionRequest(collection))
    } yield ()).resolve[CollectionException]

  private[this] def updateCollectionList(collectionList: Seq[Collection]) = Service {
    val tasks = collectionList map (collection => updateCollection(collection).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CollectionException](c.collect { case Answer(r) => r}))
  }

}
