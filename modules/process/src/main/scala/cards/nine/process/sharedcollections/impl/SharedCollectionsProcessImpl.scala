package cards.nine.process.sharedcollections.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.process.commons.types.NineCardCategory
import cards.nine.process.sharedcollections._
import cards.nine.process.sharedcollections.models._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceConfigurationException, ApiServices}
import cards.nine.services.persistence.PersistenceServices
import cards.nine.services.persistence.models.Collection

class SharedCollectionsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends SharedCollectionsProcess
  with Conversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSharedCollection(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollection(sharedCollectionId)(userConfig)
      maybeCollection <- persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId)
    } yield toSharedCollection(response.sharedCollection, maybeCollection)).resolveLeft(mapLeft)

  override def getSharedCollectionsByCategory(
    category: NineCardCategory,
    typeShareCollection: TypeSharedCollection,
    offset: Int = 0,
    limit: Int = 50)
    (implicit context: ContextSupport): TaskService[Seq[SharedCollection]] =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollectionsByCategory(category.name, typeShareCollection.name, offset, limit)(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(response.items.map(_.sharedCollectionId))
    } yield toSharedCollections(response.items, localCollectionMap)).resolveLeft(mapLeft)

  override def getPublishedCollections()
    (implicit context: ContextSupport) = {
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getPublishedCollections()(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(response.items.map(_.sharedCollectionId))
    } yield toSharedCollections(response.items, localCollectionMap)).resolveLeft(mapLeft)
  }

  private[this] def fetchSharedCollectionMap(sharedCollectionsIds: Seq[String]): TaskService[Map[String, Collection]] =
    for {
      localCollections <- persistenceServices.fetchCollectionsBySharedCollectionIds(sharedCollectionsIds)
    } yield localCollections.flatMap(c => c.sharedCollectionId.map(id => id -> c)).toMap

  override def createSharedCollection(
    sharedCollection: CreateSharedCollection)
    (implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.createSharedCollection(name, author, packages, category.name, icon, community)(userConfig)
    } yield result.sharedCollectionId).resolveLeft(mapLeft)
  }

  override def updateSharedCollection(sharedCollection: UpdateSharedCollection)(implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.updateSharedCollection(sharedCollectionId, Option(name), packages)(userConfig)
    } yield result.sharedCollectionId).resolveLeft(mapLeft)
  }

  override def getSubscriptions()(implicit context: ContextSupport) =
    (for {
      collections <- persistenceServices.fetchCollections
    } yield {

      val publicationsIds = collections filter {
        case collection =>
          collection.sharedCollectionId.isDefined & collection.originalSharedCollectionId != collection.sharedCollectionId
      } flatMap (_.sharedCollectionId)

      val collectionsWithOriginalSharedCollectionId: Seq[(String, Collection)] =
        collections.flatMap(collection => collection.originalSharedCollectionId.map((_, collection))).filter{
          case (sharedCollectionId: String, _) => !publicationsIds.contains(sharedCollectionId)
        }

      (collectionsWithOriginalSharedCollectionId map {
        case (sharedCollectionId: String, collection: Collection) =>
          (sharedCollectionId, collection)
      }) map toSubscription

    }).resolveLeft(mapLeft)

  override def subscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.subscribe(sharedCollectionId)(userConfig)
      collection <- getCollectionBySharedCollectionId(sharedCollectionId)
      _ <- persistenceServices.updateCollection(toUpdateCollectionRequest(collection, sharedCollectionSubscribed= true))
    } yield ()).resolveLeft(mapLeft)

  override def unsubscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.unsubscribe(sharedCollectionId)(userConfig)
      collection <- getCollectionBySharedCollectionId(sharedCollectionId)
      _ <- persistenceServices.updateCollection(toUpdateCollectionRequest(collection, sharedCollectionSubscribed = false))
    } yield ()).resolveLeft(mapLeft)

  private[this] def getCollectionBySharedCollectionId(sharedCollectionId: String) =
    persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId).resolveSides(
      mapRight = {
        case Some(collection) => Right(collection)
        case None => Left(SharedCollectionsException("There is no collection with this sharedCollectionId"))
      },
      mapLeft = (e: Throwable) => Left(SharedCollectionsException(e.getMessage, Some(e)))
    )

  private[this] def mapLeft[T]: (NineCardException) => Either[NineCardException, T] = {
    case e: ApiServiceConfigurationException => Left(SharedCollectionsConfigurationException(e.message, Some(e)))
    case e => Left(SharedCollectionsException(e.message, Some(e)))
  }

}
