package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.{DBUtils, CollectionEntity, NineCardsContentProvider}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try

trait CollectionRepositoryClient extends DBUtils {

  self: AppContextProvider =>

  def addCollection: Service[AddCollectionRequest, AddCollectionResponse] =
    request =>
      tryToFuture {
        Try {

          val contentValues = new ContentValues()
          contentValues.put(Position, request.data.position.asInstanceOf[java.lang.Integer])
          contentValues.put(Name, request.data.name)
          contentValues.put(Type, request.data.`type`)
          contentValues.put(Icon, request.data.icon)
          contentValues.put(ThemedColorIndex, request.data.themedColorIndex.asInstanceOf[java.lang.Integer])
          contentValues.put(AppsCategory, request.data.appsCategory)
          contentValues.put(Constrains, request.data.constrains)
          contentValues.put(OriginalSharedCollectionId, request.data.originalSharedCollectionId)
          contentValues.put(SharedCollectionId, request.data.sharedCollectionId)
          contentValues.put(SharedCollectionSubscribed, request.data.sharedCollectionSubscribed)

          val uri = appContextProvider.get.getContentResolver.insert(
            NineCardsContentProvider.ContentUriCollection,
            contentValues)

          AddCollectionResponse(
            success = true,
            collectionEntity = Some(CollectionEntity(
              id = Integer.parseInt(uri.getPathSegments.get(1)),
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCollectionResponse(
              success = false,
              collectionEntity = None)
        }
      }

  def deleteCollection: Service[DeleteCollectionRequest, DeleteCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          appContextProvider.get.getContentResolver.delete(
            withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.entity.id.toString),
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
        Try {
          val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
            withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.id.toString),
            Array.empty,
            "",
            Array.empty,
            ""))

          GetCollectionByIdResponse(result = getEntityFromCursor(cursor, collectionEntityFromCursor))

        } recover {
          case e: Exception =>
            GetCollectionByIdResponse(result = None)
        }
      }

  def getCollectionByOriginalSharedCollectionId:
  Service[GetCollectionByOriginalSharedCollectionIdRequest, GetCollectionByOriginalSharedCollectionIdResponse] =
    request =>
      tryToFuture {
        Try {
          val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
            NineCardsContentProvider.ContentUriCollection,
            AllFields.toArray,
            s"$OriginalSharedCollectionId = ?",
            Array(request.sharedCollectionId.toString),
            ""))

          GetCollectionByOriginalSharedCollectionIdResponse(
            result = getListFromCursor(cursor, collectionEntityFromCursor))

        } recover {
          case e: Exception =>
            GetCollectionByOriginalSharedCollectionIdResponse(result = Seq.empty)
        }
      }

  def getCollectionByPosition: Service[GetCollectionByPositionRequest, GetCollectionByPositionResponse] =
    request =>
      tryToFuture {
        Try {
          val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
            NineCardsContentProvider.ContentUriCollection,
            AllFields.toArray,
            s"$Position = ?",
            Array(request.position.toString),
            ""))

          GetCollectionByPositionResponse(result = getListFromCursor(cursor, collectionEntityFromCursor))

        } recover {
          case e: Exception =>
            GetCollectionByPositionResponse(result = Seq.empty)
        }
      }

  def updateCollection: Service[UpdateCollectionRequest, UpdateCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(Position, request.entity.data.position.asInstanceOf[java.lang.Integer])
          contentValues.put(Name, request.entity.data.name)
          contentValues.put(Type, request.entity.data.`type`)
          contentValues.put(Icon, request.entity.data.icon)
          contentValues.put(ThemedColorIndex, request.entity.data.themedColorIndex.asInstanceOf[java.lang.Integer])
          contentValues.put(AppsCategory, request.entity.data.appsCategory)
          contentValues.put(Constrains, request.entity.data.constrains)
          contentValues.put(OriginalSharedCollectionId, request.entity.data.originalSharedCollectionId)
          contentValues.put(SharedCollectionId, request.entity.data.sharedCollectionId)
          contentValues.put(SharedCollectionSubscribed, request.entity.data.sharedCollectionSubscribed)

          appContextProvider.get.getContentResolver.update(
            withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.entity.id.toString),
            contentValues,
            "",
            Array.empty)

          UpdateCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCollectionResponse(success = false)
        }
      }
}
