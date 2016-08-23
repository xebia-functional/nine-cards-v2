package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedCollection, UnformedApp, UnformedContact}
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, CategorizedPackage}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardWithCollectionIdRequest, FetchCardsByCollectionRequest, FetchCollectionBySharedCollectionRequest, FindCollectionByIdRequest, ImplicitsPersistenceServiceExceptions, DeleteCollectionRequest => ServicesDeleteCollectionRequest}
import rapture.core.Answer

import scalaz.concurrent.Task

trait CollectionsProcessImpl extends CollectionProcess {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with FormedCollectionDependencies
    with ImplicitsPersistenceServiceExceptions =>

  val minAppsGenerateCollections = 1

  val apiUtils = new ApiUtils(persistenceServices)

  def createCollectionsFromUnformedItems(apps: Seq[UnformedApp], contacts: Seq[UnformedContact])(implicit context: ContextSupport) = {
    val collections = createCollections(apps, contacts, appsCategories, minAppsToAdd)
    (for {
      collections <- persistenceServices.addCollections(collections)
    } yield collections map toCollection).resolve[CollectionException]
  }

  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    (for {
      apps <- appsServices.getInstalledApplications
      collectionsRequest = toAddCollectionRequestByFormedCollection(fillImageUri(items, apps))
      collections <- persistenceServices.addCollections(collectionsRequest)
    } yield collections map toCollection).resolve[CollectionException]

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

  def getCollectionBySharedCollectionId(sharedCollectionId: String, original: Boolean) =
    (for {
      collection <- persistenceServices.fetchCollectionBySharedCollection(FetchCollectionBySharedCollectionRequest(sharedCollectionId, original))
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

  def cleanCollections() =
    (for {
      _ <- persistenceServices.deleteAllCollections()
      _ <- persistenceServices.deleteAllCards()
    } yield ()).resolve[CollectionException]

  def reorderCollection(position: Int, newPosition: Int) =
    (for {
      Some(collection) <- persistenceServices.fetchCollectionByPosition(toFetchCollectionByPositionRequest(position))
      if position != newPosition
      collectionList <- getCollections
      (from, to) = if (position > newPosition) (newPosition, position) else (position, newPosition)
      updatedCollections = collectionList.reorderRange(position, newPosition).zip(from to to) map {
        case (c, index) => c.copy(position = index)
      }
      _ <- updateCollectionList(updatedCollections)
    } yield ()).resolve[CollectionException]

  def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedCollection(toCollection(collection), editCollectionRequest)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  def updateSharedCollection(collectionId: Int, sharedCollectionId: String) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedSharedCollection(toCollection(collection), sharedCollectionId)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  def unsubscribeSharedCollection(collectionId: Int) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedSharedCollection(toCollection(collection), originalSharedCollectionId = None)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  def addPackages(collectionId: Int, packages: Seq[String])(implicit context: ContextSupport) = {

    def fetchPackages(packages: Seq[String]): ServiceDef2[Seq[CategorizedPackage], ApiServiceException] =
      if (packages.isEmpty) {
        Service(Task(Answer(Seq.empty)))
      } else {
        for {
          requestConfig <- apiUtils.getRequestConfig
          response <- apiServices.googlePlayPackages(packages)(requestConfig)
        } yield response.packages
      }

    (for {
      cards <- persistenceServices.fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
      actualCollectionSize = cards.size
      notAdded = packages.filterNot(packageName => cards.exists(_.packageName.contains(packageName)))
      installedApps <- persistenceServices.fetchAppByPackages(notAdded)
      installedAppsCards = installedApps.zipWithIndex.map {
        case (app, index) => toAddCardRequest(collectionId, app, index + actualCollectionSize)
      }
      tempCollectionSize = actualCollectionSize + installedAppsCards.size
      notInstalledApps = notAdded.filterNot(packageName => installedApps.exists(_.packageName == packageName))
      fetchedPackages <- fetchPackages(notInstalledApps)
      notInstalledAppsCards = fetchedPackages.zipWithIndex.map {
        case (categorizedPackage, index) => toAddCardRequest(collectionId, categorizedPackage, index + tempCollectionSize)
      }
      _ <- persistenceServices.addCards(Seq(AddCardWithCollectionIdRequest(collectionId, installedAppsCards ++ notInstalledAppsCards)))
    } yield ()).resolve[CollectionException]
  }

  private[this] def moveCollectionList(collectionList: Seq[Collection], position: Int) =
    collectionList map { collection =>
      if (collection.position > position) collection.copy(position = collection.position - 1) else collection
    }

  private[this] def findCollectionById(id: Int) =
    (for {
      collection <- persistenceServices.findCollectionById(toFindCollectionByIdRequest(id))
    } yield collection).resolve[CollectionException]

  private[this] def updateCollection(collection: Collection) =
    (for {
      _ <- persistenceServices.updateCollection(toServicesUpdateCollectionRequest(collection))
    } yield ()).resolve[CollectionException]

  private[this] def updateCollectionList(collectionList: Seq[Collection]) =
    (for {
      _ <- persistenceServices.updateCollections(toServicesUpdateCollectionsRequest(collectionList))
    } yield ()).resolve[CollectionException]

}
