package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.net.Uri
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.commons.RichContentValues._
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.commons.OptionTFutureConversion._
import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity.{Position, Type, _}
import com.fortysevendeg.ninecardslauncher.provider.{CollectionEntity, DBUtils, NineCardsContentProvider}
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCollection
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, Collection}
import com.fortysevendeg.ninecardslauncher.utils._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scalaz.OptionT.optionT

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
            NineCardsContentProvider.ContentUriCollection,
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
            withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.collection.id.toString),
            "",
            Array.empty)

          DeleteCollectionResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCollectionResponse(success = false)
        }
      }

  def getCollectionById: Service[GetCollectionByIdRequest, GetCollectionByIdResponse] =
    request => {
      val collectionInfo = for {
        collection <- tryToFuture(getCollection(withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.id.toString)))
        getCardByCollectionResponse <- getCardByCollection(GetCardByCollectionRequest(request.id))
      } yield (collection, getCardByCollectionResponse.result)

      collectionInfo map {
        case (Some(collection), cards) =>
          GetCollectionByIdResponse(result = Some(buildCollectionWithCards(collection, cards)))
        case _ => GetCollectionByIdResponse(result = None)
      }
    }

  def getCollectionByOriginalSharedCollectionId:
  Service[GetCollectionByOriginalSharedCollectionIdRequest, GetCollectionByOriginalSharedCollectionIdResponse] =
    request => {
      val result = for {
        collection <- optionT(tryToFuture(getCollection(
          selection = s"$OriginalSharedCollectionId = ?",
          selectionArgs = Array(request.sharedCollectionId.toString))))
        getCardByCollectionResponse <- optionT(getCardByCollection(GetCardByCollectionRequest(collection.id)) map { item => Option(item) })
      } yield GetCollectionByOriginalSharedCollectionIdResponse(Some(buildCollectionWithCards(collection, getCardByCollectionResponse.result)))

      result.run.flatten
    }

  def getCollectionByPosition: Service[GetCollectionByPositionRequest, GetCollectionByPositionResponse] =
    request => {
      val result = for {
        collection <- optionT(tryToFuture(getCollection(
          selection = s"$Position = ?",
          selectionArgs = Array(request.position.toString))))
        getCardByCollectionResponse <- optionT(getCardByCollection(GetCardByCollectionRequest(collection.id)) map { item => Option(item) })
      } yield GetCollectionByPositionResponse(Some(buildCollectionWithCards(collection, getCardByCollectionResponse.result)))

      result.run.flatten
    }

  def getSortedCollections:
  Service[GetSortedCollectionsRequest, GetSortedCollectionsResponse] =
    request => {
      val result = for {
        collections <- tryToFuture(getCollections(
          sortOrder = s"$Position asc"))
        collectionsWithCards <- buildCollectionsWithCards(collections)
      } yield GetSortedCollectionsResponse(collectionsWithCards)

      result
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
            withAppendedPath(NineCardsContentProvider.ContentUriCollection, request.collection.id.toString),
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
      uri: Uri = NineCardsContentProvider.ContentUriCollection,
      projection: Array[String] = CollectionEntity.AllFields,
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
      uri: Uri = NineCardsContentProvider.ContentUriCollection,
      projection: Array[String] = CollectionEntity.AllFields,
      selection: String = "",
      selectionArgs: Array[String] = Array.empty[String],
      sortOrder: String = "") =
    Try {
      Option(contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)) match {
        case Some(cursor) => getListFromCursor(cursor, collectionEntityFromCursor) map toCollection
        case _ => Seq.empty
      }
    }

  private def buildCollectionWithCards(collection: Collection, cards: Seq[Card]) =
    collection.copy(data = collection.data.copy(cards = cards))

  private def buildCollectionsWithCards(collections: Seq[Collection]) =
    Future.sequence(
      collections map {
        collection =>
          getCardByCollection(GetCardByCollectionRequest(collection.id)) map {
            response =>
              buildCollectionWithCards(collection, response.result)
          }
      })
}
