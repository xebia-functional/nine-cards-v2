package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapperComponent, CardUri}
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CardRepositoryClient extends DBUtils {

  self: ContentResolverWrapperComponent =>

  implicit val executionContext: ExecutionContext

  def addCard: Service[AddCardRequest, AddCardResponse] =
    request =>
      tryToFuture {
        Try {

          val values = Map[String, Any](
            Position -> request.data.position,
            CollectionId -> request.collectionId,
            Term -> request.data.term,
            PackageName -> (request.data.packageName getOrElse ""),
            Type -> request.data.`type`,
            Intent -> request.data.intent,
            ImagePath -> request.data.imagePath,
            StarRating -> (request.data.starRating getOrElse 0.0d),
            Micros -> request.data.micros,
            NumDownloads -> (request.data.numDownloads getOrElse ""),
            Notification -> (request.data.notification getOrElse ""))

          val id = contentResolverWrapper.insert(
            nineCardsUri = CardUri,
            values = values)

          AddCardResponse(
            card = Some(Card(
              id = id,
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCardResponse(card = None)
        }
      }

  def deleteCard: Service[DeleteCardRequest, DeleteCardResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolverWrapper.deleteById(nineCardsUri = CardUri, id = request.card.id)

          DeleteCardResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCardResponse(success = false)
        }
      }

  def getCardById: Service[GetCardByIdRequest, GetCardByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val card = contentResolverWrapper.queryById(
            nineCardsUri = CardUri,
            id = request.id,
            projection = AllFields)(getEntityFromCursor(cardEntityFromCursor), None) map toCard

          GetCardByIdResponse(card)

        } recover {
          case e: Exception =>
            GetCardByIdResponse(result = None)
        }
      }

  def getCardByCollection: Service[GetAllCardsByCollectionRequest, GetAllCardsByCollectionResponse] =
    request =>
      tryToFuture {
        Try {
          val cards = contentResolverWrapper.query(
            nineCardsUri = CardUri,
            projection = AllFields,
            where = s"$CollectionId = ?",
            whereParams = Array(request.collectionId.toString))(getListFromCursor(cardEntityFromCursor), List.empty) map toCard

          GetAllCardsByCollectionResponse(cards)
        } recover {
          case e: Exception =>
            GetAllCardsByCollectionResponse(result = Seq.empty[Card])
        }
      }

  def updateCard: Service[UpdateCardRequest, UpdateCardResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            Position -> request.card.data.position,
            Term -> request.card.data.term,
            PackageName -> (request.card.data.packageName getOrElse ""),
            Type -> request.card.data.`type`,
            Intent -> request.card.data.intent,
            ImagePath -> request.card.data.imagePath,
            StarRating -> (request.card.data.starRating getOrElse 0.0d),
            Micros -> request.card.data.micros,
            NumDownloads -> (request.card.data.numDownloads getOrElse ""),
            Notification -> (request.card.data.notification getOrElse ""))

          contentResolverWrapper.updateById(
            nineCardsUri = CardUri,
            id = request.card.id,
            values = values)

          UpdateCardResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCardResponse(success = false)
        }
      }
}
