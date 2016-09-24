package com.fortysevendeg.ninecardslauncher.process.collection.impl

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.CatchAll
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.AddCollectionRequest
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedCollection, UnformedApp, UnformedContact}
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{NineCardCategory, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.CategorizedDetailPackage
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.OrderByCategory
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardWithCollectionIdRequest, FetchCardsByCollectionRequest, FindCollectionByIdRequest, ImplicitsPersistenceServiceExceptions, DeleteCollectionRequest => ServicesDeleteCollectionRequest}
import monix.eval.Task


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
      collectionsRequest = toAddCollectionRequestByFormedCollection(adaptCardsToAppsInstalled(items, apps))
      collections <- persistenceServices.addCollections(collectionsRequest)
    } yield collections map toCollection).resolve[CollectionException]

  def generatePrivateCollections(apps: Seq[UnformedApp])(implicit context: ContextSupport) = TaskService {
      CatchAll[CollectionException] {
        createPrivateCollections(apps, appsCategories, minAppsGenerateCollections)
    }
  }

  def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  def getCollectionById(id: Int) =
    persistenceServices.findCollectionById(FindCollectionByIdRequest(id))
      .map(_.map(toCollection))
      .resolve[CollectionException]

  def getCollectionByCategory(category: NineCardCategory) =
    persistenceServices.findCollectionByCategory(category.name)
      .map(_.map(toCollection))
      .resolve[CollectionException]

  def getCollectionBySharedCollectionId(sharedCollectionId: String) =
    persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId)
      .map(_.map(toCollection))
      .resolve[CollectionException]

  def addCollection(addCollectionRequest: AddCollectionRequest) =
    (for {
      collectionList <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(toAddCollectionRequest(addCollectionRequest, collectionList.size))
    } yield toCollection(collection)).resolve[CollectionException]

  def deleteCollection(collectionId: Int) = {

    def moveCollectionList(collectionList: Seq[Collection], position: Int) =
      collectionList flatMap {
        case collection if collection.position > position => Option(collection.copy(position = collection.position - 1))
        case _ => None
      }

    (for {
      collection <- findCollectionById(collectionId).resolveOption()
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      _ <- persistenceServices.deleteCardsByCollection(collectionId)
      collectionList <- getCollections
      _ <- updateCollectionList(moveCollectionList(collectionList, collection.position))
    } yield ()).resolve[CollectionException]
  }

  def cleanCollections() =
    (for {
      _ <- persistenceServices.deleteAllCollections()
      _ <- persistenceServices.deleteAllCards()
    } yield ()).resolve[CollectionException]

  def reorderCollection(position: Int, newPosition: Int) =
    (for {
      collectionList <- getCollections
      (from, to) = if (position > newPosition) (newPosition, position) else (position, newPosition)
      updatedCollections = collectionList.reorderRange(position, newPosition).zip(from to to) map {
        case (c, index) => c.copy(position = index)
      }
      _ <- updateCollectionList(updatedCollections)
    } yield ()).resolve[CollectionException]

  def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest) =
    editCollectionWith(collectionId) { collection =>
      collection.copy(
        name = editCollectionRequest.name,
        icon = editCollectionRequest.icon,
        themedColorIndex = editCollectionRequest.themedColorIndex,
        appsCategory = editCollectionRequest.appsCategory)
    }

  def updateSharedCollection(collectionId: Int, sharedCollectionId: String) =
    editCollectionWith(collectionId)(_.copy(sharedCollectionId = Some(sharedCollectionId)))

  def addPackages(collectionId: Int, packages: Seq[String])(implicit context: ContextSupport) = {

    def fetchPackagesNotAddedToCollection(): TaskService[(Int, Seq[String])] =
      for {
        cards <- persistenceServices.fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
        actualCollectionSize = cards.size
        notAdded = packages.filterNot(packageName => cards.exists(_.packageName.contains(packageName)))
      } yield (cards.size, notAdded)

    def fetchInstalledPackages(packages: Seq[String]): TaskService[Seq[App]] =
      if (packages.isEmpty) {
        TaskService(Task(Either.right(Seq.empty)))
      } else {
        persistenceServices.fetchAppByPackages(packages)
      }

    def categorizeNotInstalledPackages(installedApps: Seq[App], notAdded: Seq[String]): TaskService[Seq[CategorizedDetailPackage]] = {
      val notInstalledApps = notAdded.filterNot(packageName => installedApps.exists(_.packageName == packageName))
      if (notInstalledApps.isEmpty) {
        TaskService(Task(Either.right(Seq.empty)))
      } else {
        for {
          requestConfig <- apiUtils.getRequestConfig
          response <- apiServices.googlePlayPackagesDetail(notInstalledApps)(requestConfig)
        } yield response.packages
      }
    }

    def addCards(
      actualCollectionSize: Int,
      installedApps: Seq[App],
      categorizedPackages: Seq[CategorizedDetailPackage]): TaskService[Unit] = {

      if (installedApps.isEmpty && categorizedPackages.isEmpty) {
        TaskService(Task(Either.right((): Unit)))
      } else {
        val installedRequests = installedApps map (app => toAddCardRequest(collectionId, app, 0))
        val notInstalledRequests = categorizedPackages map { detailPackage =>
          toAddCardRequest(collectionId, detailPackage, NoInstalledAppCardType, 0)
        }
        val addCardsRequests = (installedRequests ++ notInstalledRequests).zipWithIndex.map {
          case (request, index) => request.copy(position = actualCollectionSize + index)
        }

        persistenceServices.addCards(Seq(AddCardWithCollectionIdRequest(collectionId, addCardsRequests))).map(_ => ())
      }
    }


    (for {
      _ <- persistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)).resolveOption()
      tuple <- fetchPackagesNotAddedToCollection()
      (actualCollectionSize, notAdded) = tuple
      installedApps <- fetchInstalledPackages(notAdded)
      fetchedPackages <- categorizeNotInstalledPackages(installedApps, notAdded)
      _ <- addCards(actualCollectionSize, installedApps, fetchedPackages)
    } yield ()).resolve[CollectionException]
  }

  def rankApps()(implicit context: ContextSupport) = {

    def mapValues(seq: Seq[(String, String)]): Seq[(String, Seq[String])] =
      seq.groupBy(_._1).mapValues(_.map(_._2)).toSeq

    def getPackagesByCategory: TaskService[Seq[(String, Seq[String])]] =
      for {
        appList <- persistenceServices.fetchApps(OrderByCategory)
      } yield mapValues(appList map (app => (app.category, app.packageName)))

      (for {
        requestConfig <- apiUtils.getRequestConfig
        packagesByCategory <- getPackagesByCategory
        location = None //TODO get current country location once the awareness service is moved to services layer
        result <- apiServices.rankApps(packagesByCategory map toServicesPackagesByCategory, location)(requestConfig)
      } yield result.items map toPackagesByCategory).resolve[CollectionException]
  }

  private[this] def editCollectionWith(collectionId: Int)(f: (Collection) => Collection) =
    (for {
      collection <- findCollectionById(collectionId).resolveOption()
      updatedCollection = f(toCollection(collection))
      _ <- persistenceServices.updateCollection(toServicesUpdateCollectionRequest(updatedCollection))
    } yield updatedCollection).resolve[CollectionException]

  private[this] def findCollectionById(id: Int) =
    persistenceServices.findCollectionById(toFindCollectionByIdRequest(id))

  private[this] def updateCollectionList(collectionList: Seq[Collection]) =
    persistenceServices.updateCollections(toServicesUpdateCollectionsRequest(collectionList))

}
