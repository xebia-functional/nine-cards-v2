package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CardUri, ContentResolverWrapperComponent}
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

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
          case NonFatal(e) => throw RepositoryInsertException()
        }
      }

  def deleteCard: Service[DeleteCardRequest, DeleteCardResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolverWrapper.deleteById(nineCardsUri = CardUri, id = request.card.id)

          DeleteCardResponse()

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def getCardById: Service[GetCardByIdRequest, GetCardByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val card = contentResolverWrapper.findById(
            nineCardsUri = CardUri,
            id = request.id,
            projection = AllFields)(getEntityFromCursor(cardEntityFromCursor)) map toCard

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
          val cards = contentResolverWrapper.fetchAll(
            nineCardsUri = CardUri,
            projection = AllFields,
            where = s"$CollectionId = ?",
            whereParams = Array(request.collectionId.toString))(getListFromCursor(cardEntityFromCursor)) map toCard

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

          UpdateCardResponse()

        } recover {
          case NonFatal(e) => throw RepositoryUpdateException()
        }
      }
}
