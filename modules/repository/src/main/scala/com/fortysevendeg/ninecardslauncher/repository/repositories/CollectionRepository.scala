package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CollectionUri, ContentResolverWrapper, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class CollectionRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addCollection(request: AddCollectionRequest)(implicit executionContext: ExecutionContext): Future[AddCollectionResponse] =
    tryToFuture {
      Try {
        val values = Map[String, Any](
          Position -> request.data.position,
          Name -> request.data.name,
          Type -> request.data.collectionType,
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
          collection = Collection(
            id = id,
            data = request.data))

      } recover {
        case NonFatal(e) => throw RepositoryInsertException()
      }
    }

  def deleteCollection(request: DeleteCollectionRequest)(implicit executionContext: ExecutionContext): Future[DeleteCollectionResponse] =
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

  def findCollectionById(request: FindCollectionByIdRequest)(implicit executionContext: ExecutionContext): Future[FindCollectionByIdResponse] =
    tryToFuture {
      findCollectionById(id = request.id) map {
        collection => FindCollectionByIdResponse(collection)
      }
    }

  def fetchCollectionByOriginalSharedCollectionId(request: FetchCollectionByOriginalSharedCollectionIdRequest)(implicit executionContext: ExecutionContext): Future[FetchCollectionByOriginalSharedCollectionIdResponse] =
    tryToFuture {
      fetchCollection(
        selection = s"$OriginalSharedCollectionId = ?",
        selectionArgs = Seq(request.sharedCollectionId.toString)) map {
        collection => FetchCollectionByOriginalSharedCollectionIdResponse(collection)
      }
    }

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest)(implicit executionContext: ExecutionContext): Future[FetchCollectionByPositionResponse] =
    tryToFuture {
      fetchCollection(selection = s"$Position = ?", selectionArgs = Array(request.position.toString)) map {
        collection => FetchCollectionByPositionResponse(collection)
      }
    }

  def fetchSortedCollections(request: FetchSortedCollectionsRequest)(implicit executionContext: ExecutionContext): Future[FetchSortedCollectionsResponse] =
    tryToFuture {
      fetchCollections(sortOrder = s"$Position asc") map {
        collections => FetchSortedCollectionsResponse(collections)
      }
    }

  def updateCollection(request: UpdateCollectionRequest)(implicit executionContext: ExecutionContext): Future[UpdateCollectionResponse] =
    tryToFuture {
      Try {
        val values = Map[String, Any](
          Position -> request.collection.data.position,
          Name -> request.collection.data.name,
          Type -> request.collection.data.collectionType,
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

  private def fetchCollection(nineCardsUri: NineCardsUri = CollectionUri,
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

  private def findCollectionById(nineCardsUri: NineCardsUri = CollectionUri,
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

  private def fetchCollections(nineCardsUri: NineCardsUri = CollectionUri,
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
