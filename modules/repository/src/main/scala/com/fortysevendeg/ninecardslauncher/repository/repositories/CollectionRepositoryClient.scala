package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapperComponent, CollectionUri, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CollectionRepositoryClient extends DBUtils {

  self: ContentResolverWrapperComponent =>

  implicit val executionContext: ExecutionContext

  def addCollection: Service[AddCollectionRequest, AddCollectionResponse] =
    request =>
      tryToFuture {
        Try {

          val values = Map[String, Any](
            Position -> request.data.position,
            Name -> request.data.name,
            Type -> request.data.`type`,
            Icon -> request.data.icon,
            ThemedColorIndex -> request.data.themedColorIndex,
            AppsCategory -> (request.data.appsCategory getOrElse ""),
            Constrains -> (request.data.constrains getOrElse ""),
            OriginalSharedCollectionId -> (request.data.originalSharedCollectionId getOrElse ""),
            SharedCollectionId -> (request.data.sharedCollectionId getOrElse ""),
            SharedCollectionSubscribed -> request.data.sharedCollectionSubscribed)

          val id = contentResolverWrapper.insert(
            nineCardsUri = CollectionUri,
            values = values)

          AddCollectionResponse(
            collection = Some(Collection(
              id = id,
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCollectionResponse(collection = None)
        }
      }

  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolverWrapper.deleteById(
            nineCardsUri = CollectionUri,
            id = request.collection.id)

          DeleteCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCollectionResponse(success = false)
        }
      }

  def getCollectionById: Service[GetCollectionByIdRequest, GetCollectionByIdResponse] =
    request =>
      tryToFuture {
        getCollectionById(id = request.id) map {
          collection => GetCollectionByIdResponse(collection)
        }
      }

  def getCollectionByOriginalSharedCollectionId:
  Service[GetCollectionByOriginalSharedCollectionIdRequest, GetCollectionByOriginalSharedCollectionIdResponse] =
    request =>
      tryToFuture {
        getCollection(
          selection = s"$OriginalSharedCollectionId = ?",
          selectionArgs = Array(request.sharedCollectionId.toString)) map {
          collection => GetCollectionByOriginalSharedCollectionIdResponse(collection)
        }
      }

  def getCollectionByPosition: Service[GetCollectionByPositionRequest, GetCollectionByPositionResponse] =
    request =>
      tryToFuture {
        getCollection(selection = s"$Position = ?", selectionArgs = Array(request.position.toString)) map {
          collection => GetCollectionByPositionResponse(collection)
        }
      }

  def getSortedCollections:
  Service[GetSortedCollectionsRequest, GetSortedCollectionsResponse] =
    request =>
      tryToFuture {
        getCollections(sortOrder = s"$Position asc") map {
          collections => GetSortedCollectionsResponse(collections)
        }
      }

  def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            Position -> request.collection.data.position,
            Name -> request.collection.data.name,
            Type -> request.collection.data.`type`,
            Icon -> request.collection.data.icon,
            ThemedColorIndex -> request.collection.data.themedColorIndex,
            AppsCategory -> (request.collection.data.appsCategory getOrElse ""),
            Constrains -> (request.collection.data.constrains getOrElse ""),
            OriginalSharedCollectionId -> (request.collection.data.originalSharedCollectionId getOrElse ""),
            SharedCollectionId -> (request.collection.data.sharedCollectionId getOrElse ""),
            SharedCollectionSubscribed -> request.collection.data.sharedCollectionSubscribed)

          contentResolverWrapper.updateById(
            nineCardsUri = CollectionUri,
            id = request.collection.id,
            values = values)

          UpdateCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCollectionResponse(success = false)
        }
      }

  private def getCollection(
      nineCardsUri: NineCardsUri = CollectionUri,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.query(
        nineCardsUri = nineCardsUri,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor), None) map toCollection
    }

  private def getCollectionById(
      nineCardsUri: NineCardsUri = CollectionUri,
      id: Int,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.queryById(
        nineCardsUri = nineCardsUri,
        id = id,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor), None) map toCollection
    }

  private def getCollections(
      nineCardsUri: NineCardsUri = CollectionUri,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.query(
        nineCardsUri = nineCardsUri,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getListFromCursor(collectionEntityFromCursor), List.empty) map toCollection
    }
}
