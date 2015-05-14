package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CollectionUri, ContentResolverWrapperComponent, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

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
            SharedCollectionSubscribed -> (request.data.sharedCollectionSubscribed getOrElse false))

          val id = contentResolverWrapper.insert(
            nineCardsUri = CollectionUri,
            values = values)

          AddCollectionResponse(
            collection = Some(Collection(
              id = id,
              data = request.data)))

        } recover {
          case NonFatal(e) => throw RepositoryInsertException()
        }
      }

  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          val deleted = contentResolverWrapper.deleteById(
            nineCardsUri = CollectionUri,
            id = request.collection.id)

          DeleteCollectionResponse(deleted = deleted)

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def getCollectionById: Service[FindCollectionByIdRequest, FindCollectionByIdResponse] =
    request =>
      tryToFuture {
        getCollectionById(id = request.id) map {
          collection => FindCollectionByIdResponse(collection)
        }
      }

  def getCollectionByOriginalSharedCollectionId:
  Service[FetchCollectionByOriginalSharedCollectionIdRequest, FetchCollectionByOriginalSharedCollectionIdResponse] =
    request =>
      tryToFuture {
        getCollection(
          selection = s"$OriginalSharedCollectionId = ?",
          selectionArgs = Seq(request.sharedCollectionId.toString)) map {
          collection => FetchCollectionByOriginalSharedCollectionIdResponse(collection)
        }
      }

  def getCollectionByPosition: Service[FetchCollectionByPositionRequest, FetchCollectionByPositionResponse] =
    request =>
      tryToFuture {
        getCollection(selection = s"$Position = ?", selectionArgs = Array(request.position.toString)) map {
          collection => FetchCollectionByPositionResponse(collection)
        }
      }

  def getSortedCollections:
  Service[FetchSortedCollectionsRequest, FetchSortedCollectionsResponse] =
    request =>
      tryToFuture {
        getCollections(sortOrder = s"$Position asc") map {
          collections => FetchSortedCollectionsResponse(collections)
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
            SharedCollectionSubscribed -> (request.collection.data.sharedCollectionSubscribed getOrElse false))

          val updated = contentResolverWrapper.updateById(
            nineCardsUri = CollectionUri,
            id = request.collection.id,
            values = values)

          UpdateCollectionResponse(updated = updated)

        } recover {
          case NonFatal(e) => throw RepositoryUpdateException()
        }
      }

  private def getCollection(
      nineCardsUri: NineCardsUri = CollectionUri,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.fetch(
        nineCardsUri = nineCardsUri,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
    }

  private def getCollectionById(
      nineCardsUri: NineCardsUri = CollectionUri,
      id: Int,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.findById(
        nineCardsUri = nineCardsUri,
        id = id,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getEntityFromCursor(collectionEntityFromCursor)) map toCollection
    }

  private def getCollections(
      nineCardsUri: NineCardsUri = CollectionUri,
      projection: Seq[String] = AllFields,
      selection: String = "",
      selectionArgs: Seq[String] = Seq.empty[String],
      sortOrder: String = "") =
    Try {
      contentResolverWrapper.fetchAll(
        nineCardsUri = nineCardsUri,
        projection = projection,
        where = selection,
        whereParams = selectionArgs,
        orderBy = sortOrder)(getListFromCursor(collectionEntityFromCursor)) map toCollection
    }
}
