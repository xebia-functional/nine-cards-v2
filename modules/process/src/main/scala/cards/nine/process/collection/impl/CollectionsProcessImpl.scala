package cards.nine.process.collection.impl

import cards.nine.commons.CatchAll
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Application.ApplicationDataOps
import cards.nine.models.types.NineCardsCategory._
import cards.nine.models.types._
import cards.nine.models.{RankApps, _}
import cards.nine.process.collection._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions
import cats.syntax.either._
import monix.eval.Task

import scala.annotation.tailrec

trait CollectionsProcessImpl
  extends CollectionProcess
  with NineCardsIntentConversions
  with ImplicitsCollectionException {

  self: CollectionProcessDependencies
    with ImplicitsPersistenceServiceExceptions =>

  val minAppsGenerateCollections = 1

  val apiUtils = new ApiUtils(persistenceServices)

  def createCollectionsFromCollectionData(collectionDataSeq: Seq[CollectionData])(implicit context: ContextSupport) = {

    def adaptCardToApp(card: CardData, apps: Seq[ApplicationData]) = {
      val packageName = card.intent.extractPackageName()
      apps find (app => packageName.contains(app.packageName)) match {
        case Some(app) if card.intent.extractClassName().contains(app.className) => card.copy(cardType = AppCardType)
        case Some(app) => card.copy(intent = toNineCardIntent(app), cardType = AppCardType)
        case None => card.copy(cardType = NoInstalledAppCardType)
      }
    }

    def adaptCardsToAppsInstalled(collections: Seq[CollectionData], apps: Seq[ApplicationData]): Seq[CollectionData] = {
      collections map { collection =>
        val cardsWithPath = collection.cards map {
          case card if card.cardType == AppCardType || card.cardType == RecommendedAppCardType => adaptCardToApp(card, apps)
          case card => card
        }
        collection.copy(cards = cardsWithPath)
      }
    }

    (for {
      apps <- appsServices.getInstalledApplications
      collectionsRequest = adaptCardsToAppsInstalled(collectionDataSeq, apps)
      collections <- persistenceServices.addCollections(collectionsRequest)
    } yield collections).resolve[CollectionException]
  }


  def generatePrivateCollections(apps: Seq[ApplicationData])(implicit context: ContextSupport) = TaskService {
      CatchAll[CollectionException] {

        @tailrec
        def createPrivateCollections(
          items: Seq[ApplicationData],
          categories: Seq[NineCardsCategory],
          acc: Seq[CollectionData]): Seq[CollectionData] = categories match {
          case Nil => acc
          case h :: t =>
            val insert = createPrivateCollection(items, h, acc.length)
            val a = if (insert.cards.nonEmpty) acc :+ insert else acc
            createPrivateCollections(items, t, a)
        }

        def createPrivateCollection(items: Seq[ApplicationData], category: NineCardsCategory, position: Int): CollectionData = {
          // TODO We should sort the application using an endpoint in the new sever
          val appsByCategory = items.filter(_.category.toAppCategory == category)
          CollectionData (
            position = position,
            name = collectionProcessConfig.namesCategories.getOrElse(category, category.getStringResource),
            collectionType = AppsCollectionType,
            icon = category.getStringResource,
            themedColorIndex = 0,
            appsCategory = Some(category),
            cards = appsByCategory map (_.toCardData),
            moment = None,
            originalSharedCollectionId = None,
            sharedCollectionId = None,
            sharedCollectionSubscribed = false,
            publicCollectionStatus = NotPublished)
        }

        createPrivateCollections(apps, appsCategories, Seq.empty)
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
      collection <- findCollectionById(collectionId)
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

  def editCollection(collectionId: Int, collectionData: CollectionData) =
    editCollectionWith(collectionId) { collection =>
      collection.copy(
        name = collectionData.name,
        icon = collectionData.icon,
        themedColorIndex = collectionData.themedColorIndex,
        appsCategory = collectionData.appsCategory)
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
          packages <- apiServices.googlePlayPackagesDetail(notInstalledApps)(requestConfig)
        } yield packages
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
      _ <- findCollectionById(collectionId)
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

    def generatePackagesByCategoryFromRankApps(item: RankApps) =
      PackagesByCategory(
        category = item.category,
        packages = item.packages)

    def getPackagesByCategory: TaskService[Seq[(NineCardsCategory, Seq[String])]] =
      for {
        appList <- persistenceServices.fetchApps(OrderByCategory)
      } yield mapValues(appList filterNot (_.category == Misc) map (app => (app.category, app.packageName)))

    (for {
      requestConfig <- apiUtils.getRequestConfig
      packagesByCategory <- getPackagesByCategory
      location <- awarenessServices.getLocation.map(Option(_)).resolveLeftTo(None)
      result <- apiServices.rankApps(
        packagesByCategory map generatePackagesByCategory,
        location flatMap (_.countryCode))(requestConfig)
    } yield result map generatePackagesByCategoryFromRankApps).resolve[CollectionException]
  }

  def rankAppsByMoment(limit: Int)(implicit context: ContextSupport) = {

    def toPackagesByMoment(rankAppsByMoment: Seq[RankAppsByMoment]) =
      rankAppsByMoment map (ra => PackagesByMoment(ra.moment, ra.packages))

    (for {
      requestConfig <- apiUtils.getRequestConfig
      appList <- persistenceServices.fetchApps(OrderByName)
      momentList <- persistenceServices.fetchMoments
      location <- awarenessServices.getLocation.map(Option(_)).resolveLeftTo(None)
      result <- apiServices.rankAppsByMoment(
        appList map (_.packageName),
        momentList map (_.momentType.name),
        location flatMap (_.countryCode),
        limit = limit)(requestConfig)
    } yield toPackagesByMoment(result)).resolve[CollectionException]
  }

  def rankWidgetsByMoment(limit: Int, moments: Seq[NineCardsMoment])(implicit context: ContextSupport) = {

    def toAppWidget(rankWidget: RankWidget, appWidgets: Seq[AppWidget]) =
      appWidgets.find(appWidget => appWidget.packageName == rankWidget.packageName && appWidget.className == rankWidget.className)

    def toWidgetsByMoment(rankWidgetsByMoment: Seq[RankWidgetsByMoment], appWidgets: Seq[AppWidget]) =
      rankWidgetsByMoment map { rankWidget =>
        WidgetsByMoment(
          moment = rankWidget.moment,
          widgets = rankWidget.widgets flatMap (widget => toAppWidget(widget, appWidgets)))
      }

    (for {
      requestConfig <- apiUtils.getRequestConfig
      appList <- persistenceServices.fetchApps(OrderByName)
      location <- awarenessServices.getLocation.map(Option(_)).resolveLeftTo(None)
      appWidgets <- widgetsServices.getWidgets
      result <- apiServices.rankWidgetsByMoment(
        appList map (_.packageName),
        moments map (_.name),
        location flatMap (_.countryCode),
        limit = limit)(requestConfig)
    } yield toWidgetsByMoment(result, appWidgets)).resolve[CollectionException]
  }

  private[this] def editCollectionWith(collectionId: Int)(f: (Collection) => Collection) =
    (for {
      collection <- findCollectionById(collectionId)
      updatedCollection = f(collection)
      _ <- persistenceServices.updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  private[this] def findCollectionById(collectionId: Int) =
    persistenceServices.findCollectionById(collectionId)
      .resolveOption(s"Can't find the collection with id $collectionId")

  private[this] def updateCollectionList(collectionList: Seq[Collection]) =
    persistenceServices.updateCollections(collectionList)

}
