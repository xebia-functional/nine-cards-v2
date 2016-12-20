package cards.nine.process.sharedcollections.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.types._
import cards.nine.models.{Collection, _}
import cards.nine.process.sharedcollections._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceConfigurationException, ApiServices}
import cards.nine.services.persistence.PersistenceServices

class SharedCollectionsProcessImpl(
    apiServices: ApiServices,
    persistenceServices: PersistenceServices)
    extends SharedCollectionsProcess {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSharedCollection(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig       <- apiUtils.getRequestConfig
      sharedCollection <- apiServices.getSharedCollection(sharedCollectionId)(userConfig)
      maybeCollection <- persistenceServices.fetchCollectionBySharedCollectionId(
        sharedCollectionId)
    } yield syncCollectionStatus(maybeCollection, sharedCollection)).resolveLeft(mapLeft)

  override def getSharedCollectionsByCategory(
      category: NineCardsCategory,
      typeShareCollection: TypeSharedCollection,
      offset: Int = 0,
      limit: Int = 50)(implicit context: ContextSupport): TaskService[Seq[SharedCollection]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      sharedCollections <- apiServices.getSharedCollectionsByCategory(
        category.name,
        typeShareCollection.name,
        offset,
        limit)(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(sharedCollections.map(_.sharedCollectionId))
    } yield
      sharedCollections map { sharedCollection =>
        syncCollectionStatus(
          localCollectionMap.get(sharedCollection.sharedCollectionId),
          sharedCollection)
      }).resolveLeft(mapLeft)

  override def getPublishedCollections()(implicit context: ContextSupport) =
    (for {
      userConfig         <- apiUtils.getRequestConfig
      sharedCollections  <- apiServices.getPublishedCollections()(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(sharedCollections.map(_.sharedCollectionId))
    } yield
      sharedCollections map { sharedCollection =>
        syncCollectionStatus(
          localCollectionMap.get(sharedCollection.sharedCollectionId),
          sharedCollection)
      }).resolveLeft(mapLeft)

  private[this] def fetchSharedCollectionMap(
      sharedCollectionsIds: Seq[String]): TaskService[Map[String, Collection]] =
    for {
      localCollections <- persistenceServices.fetchCollectionsBySharedCollectionIds(
        sharedCollectionsIds)
    } yield localCollections.flatMap(c => c.sharedCollectionId.map(id => id -> c)).toMap

  private[this] def syncCollectionStatus(
      maybeLocalCollection: Option[Collection],
      sharedCol: SharedCollection): SharedCollection = maybeLocalCollection match {
    case Some(c) =>
      sharedCol.copy(locallyAdded = Some(true), publicCollectionStatus = c.publicCollectionStatus)
    case None => sharedCol.copy(locallyAdded = Some(false))
  }

  override def createSharedCollection(
      name: String,
      author: String,
      packages: Seq[String],
      category: NineCardsCategory,
      icon: String,
      community: Boolean)(implicit context: ContextSupport) = {
    (for {
      userConfig <- apiUtils.getRequestConfig
      sharedCollectionId <- apiServices.createSharedCollection(
        name,
        author,
        packages,
        category.name,
        icon,
        community)(userConfig)
    } yield sharedCollectionId).resolveLeft(mapLeft)
  }

  override def updateSharedCollection(
      sharedCollectionId: String,
      name: String,
      packages: Seq[String])(implicit context: ContextSupport) = {
    (for {
      userConfig <- apiUtils.getRequestConfig
      sharedCollectionId <- apiServices.updateSharedCollection(
        sharedCollectionId,
        Option(name),
        packages)(userConfig)
    } yield sharedCollectionId).resolveLeft(mapLeft)
  }

  override def getSubscriptions()(implicit context: ContextSupport) = {

    def toSubscription(subscriptions: (String, Collection)): Subscription = {
      val (sharedCollectionId, collection) = subscriptions
      Subscription(
        id = collection.id,
        sharedCollectionId = sharedCollectionId,
        name = collection.name,
        apps = collection.cards.count(card => card.cardType == AppCardType),
        icon = collection.icon,
        themedColorIndex = collection.themedColorIndex,
        subscribed = collection.sharedCollectionSubscribed)
    }

    (for {
      collections <- persistenceServices.fetchCollections
    } yield {

      val publicationsIds = collections filter { collection =>
        collection.sharedCollectionId.isDefined & collection.originalSharedCollectionId != collection.sharedCollectionId
      } flatMap (_.sharedCollectionId)

      val collectionsWithOriginalSharedCollectionId: Seq[(String, Collection)] =
        collections
          .flatMap(collection => collection.originalSharedCollectionId.map((_, collection)))
          .filter {
            case (sharedCollectionId: String, _) => !publicationsIds.contains(sharedCollectionId)
          }

      (collectionsWithOriginalSharedCollectionId map {
        case (sharedCollectionId: String, collection: Collection) =>
          (sharedCollectionId, collection)
      }) map toSubscription

    }).resolveLeft(mapLeft)
  }

  override def subscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _          <- apiServices.subscribe(sharedCollectionId)(userConfig)
      collection <- getCollectionBySharedCollectionId(sharedCollectionId)
      _          <- persistenceServices.updateCollection(collection.copy(sharedCollectionSubscribed = true))
    } yield ()).resolveLeft(mapLeft)

  override def unsubscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _          <- apiServices.unsubscribe(sharedCollectionId)(userConfig)
      collection <- getCollectionBySharedCollectionId(sharedCollectionId)
      _ <- persistenceServices.updateCollection(
        collection.copy(sharedCollectionSubscribed = false))
    } yield ()).resolveLeft(mapLeft)

  override def updateViewSharedCollection(sharedCollectionId: String)(
      implicit context: ContextSupport): TaskService[Unit] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _          <- apiServices.updateViewShareCollection(sharedCollectionId)(userConfig)
    } yield ()).resolveLeft(mapLeft)

  private[this] def getCollectionBySharedCollectionId(sharedCollectionId: String) =
    persistenceServices
      .fetchCollectionBySharedCollectionId(sharedCollectionId)
      .resolveSides(
        mapRight = {
          case Some(collection) => Right(collection)
          case None =>
            Left(SharedCollectionsException("There is no collection with this sharedCollectionId"))
        },
        mapLeft = (e: Throwable) => Left(SharedCollectionsException(e.getMessage, Some(e)))
      )

  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException =>
      Left(SharedCollectionsConfigurationException(e.message, Some(e)))
    case e => Left(SharedCollectionsException(e.message, Some(e)))
  }

}
