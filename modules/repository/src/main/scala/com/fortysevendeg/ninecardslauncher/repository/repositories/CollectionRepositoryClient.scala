package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.net.Uri
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.provider.NineCardsContentProvider._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Collection
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CollectionRepositoryClient
    extends CardRepositoryClient
    with DBUtils {

  self: ContentResolverProvider =>

  implicit val executionContext: ExecutionContext

  def addCollection(): Service[AddCollectionRequest, AddCollectionResponse] =
    request =>
      tryToFuture {
        Try {

          val contentValues = new ContentValues()
          contentValues.put(Position, request.data.position)
          contentValues.put(Name, request.data.name)
          contentValues.put(Type, request.data.`type`)
          contentValues.put(Icon, request.data.icon)
          contentValues.put(ThemedColorIndex, request.data.themedColorIndex)
          contentValues.put(AppsCategory, request.data.appsCategory getOrElse "")
          contentValues.put(Constrains, request.data.constrains getOrElse "")
          contentValues.put(OriginalSharedCollectionId, request.data.originalSharedCollectionId getOrElse "")
          contentValues.put(SharedCollectionId, request.data.sharedCollectionId getOrElse "")
          contentValues.put(SharedCollectionSubscribed, request.data.sharedCollectionSubscribed)

          val uri = contentResolver.insert(
            ContentUriCollection,
            contentValues)

          AddCollectionResponse(
            collection = Some(Collection(
              id = Integer.parseInt(uri.getPathSegments.get(1)),
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCollectionResponse(collection = None)
        }
      }

  def deleteCollection(): Service[DeleteCollectionRequest, DeleteCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolver.delete(
            withAppendedPath(ContentUriCollection, request.collection.id.toString),
            "",
            Array.empty)

          DeleteCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCollectionResponse(success = false)
        }
      }

  def getCollectionById: Service[GetCollectionByIdRequest, GetCollectionByIdResponse] =
    request =>
      tryToFuture {
        getCollection(uri = withAppendedPath(ContentUriCollection, request.id.toString)) map {
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

  def updateCollection(): Service[UpdateCollectionRequest, UpdateCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(Position, request.collection.data.position)
          contentValues.put(Name, request.collection.data.name)
          contentValues.put(Type, request.collection.data.`type`)
          contentValues.put(Icon, request.collection.data.icon)
          contentValues.put(ThemedColorIndex, request.collection.data.themedColorIndex)
          contentValues.put(AppsCategory, request.collection.data.appsCategory getOrElse "")
          contentValues.put(Constrains, request.collection.data.constrains getOrElse "")
          contentValues.put(OriginalSharedCollectionId, request.collection.data.originalSharedCollectionId getOrElse "")
          contentValues.put(SharedCollectionId, request.collection.data.sharedCollectionId getOrElse "")
          contentValues.put(SharedCollectionSubscribed, request.collection.data.sharedCollectionSubscribed)

          contentResolver.update(
            withAppendedPath(ContentUriCollection, request.collection.id.toString),
            contentValues,
            "",
            Array.empty)

          UpdateCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCollectionResponse(success = false)
        }
      }

  private def getCollection(
      uri: Uri = ContentUriCollection,
      projection: Array[String] = AllFields,
      selection: String = "",
      selectionArgs: Array[String] = Array.empty[String],
      sortOrder: String = "") =
    Try {
      Option(contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) match {
        case Some(cursor) => getEntityFromCursor(cursor, collectionEntityFromCursor) map toCollection
        case _ => None
      }
    }

  private def getCollections(
      uri: Uri = ContentUriCollection,
      projection: Array[String] = AllFields,
      selection: String = "",
      selectionArgs: Array[String] = Array.empty[String],
      sortOrder: String = "") =
    Try {
      Option(contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) match {
        case Some(cursor) => getListFromCursor(cursor, collectionEntityFromCursor) map toCollection
        case _ => Seq.empty
      }
    }
}
