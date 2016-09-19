package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.types.NineCardCategory
import com.fortysevendeg.ninecardslauncher.process.sharedcollections._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection

class SharedCollectionsProcessImpl(apiServices: ApiServices, persistenceServices: PersistenceServices)
  extends SharedCollectionsProcess
  with Conversions
  with ImplicitsSharedCollectionsExceptions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSharedCollection(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getSharedCollection(sharedCollectionId)(userConfig)
      maybeCollection <- persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId)
    } yield toSharedCollection(response.sharedCollection, maybeCollection)).resolve[SharedCollectionsExceptions]

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
    } yield toSharedCollections(response.items, localCollectionMap)).resolve[SharedCollectionsExceptions]

  override def getPublishedCollections()
    (implicit context: ContextSupport) = {
    (for {
      userConfig <- apiUtils.getRequestConfig
      response <- apiServices.getPublishedCollections()(userConfig)
      localCollectionMap <- fetchSharedCollectionMap(response.items.map(_.sharedCollectionId))
    } yield toSharedCollections(response.items, localCollectionMap)).resolve[SharedCollectionsExceptions]
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
      result <- apiServices.createSharedCollection(name, description, author, packages, category.name, icon, community)(userConfig)
    } yield result.sharedCollectionId).resolve[SharedCollectionsExceptions]
  }

  override def updateSharedCollection(sharedCollection: UpdateSharedCollection)(implicit context: ContextSupport) = {
    import sharedCollection._
    (for {
      userConfig <- apiUtils.getRequestConfig
      result <- apiServices.updateSharedCollection(sharedCollectionId, Option(name), description, packages)(userConfig)
    } yield result.sharedCollectionId).resolve[SharedCollectionsExceptions]
  }

  override def getSubscriptions()(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      subscriptions <- apiServices.getSubscriptions()(userConfig)
      publications <- apiServices.getPublishedCollections()(userConfig)
      collections <- persistenceServices.fetchCollections
    } yield {

      val subscriptionsIds = subscriptions.items map (_.sharedCollectionId)
      val publicationsIds = publications.items map (_.sharedCollectionId)

      val collectionsWithOriginalSharedCollectionId: Seq[(String, Collection)] =
        collections.flatMap(collection => collection.originalSharedCollectionId.map((_, collection))).filter{
          case (sharedCollectionId: String, _) => !publicationsIds.contains(sharedCollectionId)
        }

      (collectionsWithOriginalSharedCollectionId map {
        case (sharedCollectionId: String, collection: Collection) =>
          (sharedCollectionId, collection, subscriptionsIds.contains(sharedCollectionId))
      }) map toSubscription

    }).resolve[SharedCollectionsExceptions]

  override def subscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.subscribe(sharedCollectionId)(userConfig)
    } yield ()).resolve[SharedCollectionsExceptions]

  override def unsubscribe(sharedCollectionId: String)(implicit context: ContextSupport) =
    (for {
      userConfig <- apiUtils.getRequestConfig
      _ <- apiServices.unsubscribe(sharedCollectionId)(userConfig)
    } yield ()).resolve[SharedCollectionsExceptions]

}
