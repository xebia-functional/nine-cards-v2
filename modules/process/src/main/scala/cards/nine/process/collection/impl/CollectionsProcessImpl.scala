package cards.nine.process.collection.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models._
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types._
import cards.nine.process.collection._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{CategorizedDetailPackage, RankAppsResponse}
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions
import cats.syntax.either._
import monix.eval.Task

trait CollectionsProcessImpl extends CollectionProcess with NineCardsIntentConversions {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with FormedCollectionDependencies
    with ImplicitsPersistenceServiceExceptions =>

  val minAppsGenerateCollections = 1

  val apiUtils = new ApiUtils(persistenceServices)

  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    (for {
      apps <- appsServices.getInstalledApplications
      collectionsRequest = toCollectionDataByFormedCollection(adaptCardsToAppsInstalled(items, apps))
      collections <- persistenceServices.addCollections(collectionsRequest)
    } yield collections).resolve[CollectionException]

  def generatePrivateCollections(apps: Seq[ApplicationData])(implicit context: ContextSupport) = TaskService {
      CatchAll[CollectionException] {
        createPrivateCollections(apps, appsCategories, minAppsGenerateCollections)
    }
  }

  def getCollections = persistenceServices.fetchCollections.resolve[CollectionException]

  def getCollectionById(collectionId: Int) =
    persistenceServices.findCollectionById(collectionId).resolve[CollectionException]

  def getCollectionByCategory(category: NineCardsCategory) =
    persistenceServices.findCollectionByCategory(category.name).resolve[CollectionException]

  def getCollectionBySharedCollectionId(sharedCollectionId: String) =
    persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) .resolve[CollectionException]

  def addCollection(collection: CollectionData) =
    (for {
      collectionList <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(collection.copy(position = collectionList.size))
    } yield collection).resolve[CollectionException]

  def deleteCollection(collectionId: Int) = {

    def moveCollectionList(collectionList: Seq[Collection], position: Int) =
      collectionList flatMap {
        case collection if collection.position > position => Option(collection.copy(position = collection.position - 1))
        case _ => None
      }

    (for {
      collection <- findCollectionById(collectionId).resolveOption()
      _ <- persistenceServices.deleteCollection(collection)
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

  def editCollection(collectionId: Int, collection: CollectionData) =
    editCollectionWith(collectionId) { collection =>
      collection.copy(
        name = collection.name,
        icon = collection.icon,
        themedColorIndex = collection.themedColorIndex,
        appsCategory = collection.appsCategory)
    }

  def updateSharedCollection(collectionId: Int, sharedCollectionId: String) =
    editCollectionWith(collectionId)(_.copy(sharedCollectionId = Some(sharedCollectionId), sharedCollectionSubscribed = false))

  def addPackages(collectionId: Int, packages: Seq[String])(implicit context: ContextSupport) = {

    def fetchPackagesNotAddedToCollection(): TaskService[(Int, Seq[String])] =
      for {
        cards <- persistenceServices.fetchCardsByCollection(collectionId)
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

      def toCardDataFromInstalled(app: Application, position: Int) =
        CardData (
          position = position,
          term = app.name,
          packageName = Option(app.packageName),
          cardType = AppCardType,
          intent = toNineCardIntent(app),
          imagePath = None)

      def toCardDataFromNotInstalled(categorizedPackage: CategorizedDetailPackage, cardType: CardType, position: Int) =
        CardData (
          term = categorizedPackage.title,
          position = position,
          packageName = Option(categorizedPackage.packageName),
          cardType = cardType,
          intent = packageToNineCardIntent(categorizedPackage.packageName),
          imagePath = None)

      if (installedApps.isEmpty && categorizedPackages.isEmpty) {
        TaskService(Task(Either.right((): Unit)))
      } else {
        val installedRequests = installedApps map (app => (collectionId, toCardDataFromInstalled(app, 0)))
        val notInstalledRequests = categorizedPackages map { detailPackage =>
          (collectionId, toCardDataFromNotInstalled(detailPackage, NoInstalledAppCardType, 0))
        }
        val cardsToAdd = (installedRequests ++ notInstalledRequests).zipWithIndex.map {
          case ((_, card), index) => card.copy(position = actualCollectionSize + index)
        }

        persistenceServices.addCards(Seq((collectionId, cardsToAdd))).map(_ => ())
      }
    }

    (for {
      _ <- persistenceServices.findCollectionById(collectionId).resolveOption()
      tuple <- fetchPackagesNotAddedToCollection()
      (actualCollectionSize, notAdded) = tuple
      installedApps <- fetchInstalledPackages(notAdded)
      fetchedPackages <- categorizeNotInstalledPackages(installedApps, notAdded)
      _ <- addCards(actualCollectionSize, installedApps, fetchedPackages)
    } yield ()).resolve[CollectionException]
  }

  def rankApps()(implicit context: ContextSupport) = {

    def mapValues(seq: Seq[(NineCardsCategory, String)]): Seq[(NineCardsCategory, Seq[String])] =
      seq.groupBy(_._1).mapValues(_.map(_._2)).toSeq

    def generatePackagesByCategory(packagesByCategory: (NineCardsCategory, Seq[String])) = {
      val (category, packages) = packagesByCategory
      PackagesByCategory(
        category = category,
        packages = packages)
    }

    def generatePackagesByCategoryFromRankApps(item: RankAppsResponse) =
      PackagesByCategory(
        category = NineCardsCategory(item.category),
        packages = item.packages)

    def getPackagesByCategory: TaskService[Seq[(NineCardsCategory, Seq[String])]] =
      for {
        appList <- persistenceServices.fetchApps(OrderByCategory)
      } yield mapValues(appList map (app => (app.category, app.packageName)))

      (for {
        requestConfig <- apiUtils.getRequestConfig
        packagesByCategory <- getPackagesByCategory
        location <- awarenessServices.getLocation.map(Option(_)).resolveLeftTo(None)
        result <- apiServices.rankApps(
          packagesByCategory map generatePackagesByCategory,
          location flatMap (_.countryCode))(requestConfig)
      } yield result.items map generatePackagesByCategoryFromRankApps).resolve[CollectionException]
  }

  private[this] def editCollectionWith(collectionId: Int)(f: (Collection) => Collection) =
    (for {
      collection <- findCollectionById(collectionId).resolveOption()
      updatedCollection = f(collection)
      _ <- persistenceServices.updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  private[this] def findCollectionById(collectionId: Int) =
    persistenceServices.findCollectionById(collectionId)

  private[this] def updateCollectionList(collectionList: Seq[Collection]) =
    persistenceServices.updateCollections(collectionList)

}
