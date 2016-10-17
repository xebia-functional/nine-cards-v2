package cards.nine.process.collection.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.NineCardCategory._
import cards.nine.models.types.{NineCardCategory, NoInstalledAppCardType}
import cards.nine.models.{Application, ApplicationData}
import cards.nine.process.collection.models.FormedCollection
import cards.nine.process.collection.{AddCollectionRequest, _}
import cards.nine.process.commons.models.Collection
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.CategorizedDetailPackage
import cards.nine.services.persistence.{AddCardWithCollectionIdRequest, FetchCardsByCollectionRequest, FindCollectionByIdRequest, ImplicitsPersistenceServiceExceptions, OrderByCategory, DeleteCollectionRequest => ServicesDeleteCollectionRequest}
import cats.syntax.either._
import monix.eval.Task

trait CollectionsProcessImpl extends CollectionProcess {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with FormedCollectionDependencies
    with ImplicitsPersistenceServiceExceptions =>

  val minAppsGenerateCollections = 1

  val apiUtils = new ApiUtils(persistenceServices)

  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    (for {
      apps <- appsServices.getInstalledApplications
      collectionsRequest = toAddCollectionRequestByFormedCollection(adaptCardsToAppsInstalled(items, apps))
      collections <- persistenceServices.addCollections(collectionsRequest)
    } yield collections map toCollection).resolve[CollectionException]

  def generatePrivateCollections(apps: Seq[ApplicationData])(implicit context: ContextSupport) = TaskService {
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
      collection <- findCollectionById(collectionId)
        .resolveOption(s"Can't find the collection with id $collectionId")
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
    editCollectionWith(collectionId)(_.copy(sharedCollectionId = Some(sharedCollectionId), sharedCollectionSubscribed = false))

  def addPackages(collectionId: Int, packages: Seq[String])(implicit context: ContextSupport) = {

    def fetchPackagesNotAddedToCollection(): TaskService[(Int, Seq[String])] =
      for {
        cards <- persistenceServices.fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
        actualCollectionSize = cards.size
        notAdded = packages.filterNot(packageName => cards.exists(_.packageName.contains(packageName)))
      } yield (cards.size, notAdded)

    def fetchInstalledPackages(packages: Seq[String]): TaskService[Seq[Application]] =
      if (packages.isEmpty) {
        TaskService(Task(Either.right(Seq.empty)))
      } else {
        persistenceServices.fetchAppByPackages(packages)
      }

    def categorizeNotInstalledPackages(installedApps: Seq[Application], notAdded: Seq[String]): TaskService[Seq[CategorizedDetailPackage]] = {
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
      installedApps: Seq[Application],
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
      _ <- persistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId))
        .resolveOption(s"Can't find the collection with id $collectionId")
      tuple <- fetchPackagesNotAddedToCollection()
      (actualCollectionSize, notAdded) = tuple
      installedApps <- fetchInstalledPackages(notAdded)
      fetchedPackages <- categorizeNotInstalledPackages(installedApps, notAdded)
      _ <- addCards(actualCollectionSize, installedApps, fetchedPackages)
    } yield ()).resolve[CollectionException]
  }

  def rankApps()(implicit context: ContextSupport) = {

    def mapValues(seq: Seq[(NineCardCategory, String)]): Seq[(NineCardCategory, Seq[String])] =
      seq.groupBy(_._1).mapValues(_.map(_._2)).toSeq

    def getPackagesByCategory: TaskService[Seq[(NineCardCategory, Seq[String])]] =
      for {
        appList <- persistenceServices.fetchApps(OrderByCategory)
      } yield mapValues(appList map (app => (app.category, app.packageName)))

      (for {
        requestConfig <- apiUtils.getRequestConfig
        packagesByCategory <- getPackagesByCategory
        location <- awarenessServices.getLocation.map(Option(_)).resolveLeftTo(None)
        result <- apiServices.rankApps(
          packagesByCategory map toServicesPackagesByCategory,
          location flatMap (_.countryCode))(requestConfig)
      } yield result.items map toPackagesByCategory).resolve[CollectionException]
  }

  private[this] def editCollectionWith(collectionId: Int)(f: (Collection) => Collection) =
    (for {
      collection <- findCollectionById(collectionId)
        .resolveOption(s"Can't find the collection with id $collectionId")
      updatedCollection = f(toCollection(collection))
      _ <- persistenceServices.updateCollection(toServicesUpdateCollectionRequest(updatedCollection))
    } yield updatedCollection).resolve[CollectionException]

  private[this] def findCollectionById(id: Int) =
    persistenceServices.findCollectionById(toFindCollectionByIdRequest(id))

  private[this] def updateCollectionList(collectionList: Seq[Collection]) =
    persistenceServices.updateCollections(toServicesUpdateCollectionsRequest(collectionList))

}
