package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CardUri, ContentResolverWrapper}
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try
import scala.util.control.NonFatal

class CardRepositoryClient(contentResolverWrapper: ContentResolverWrapper)
  extends DBUtils {

  def addCard(request: AddCardRequest)(implicit ec: ExecutionContext): Future[AddCardResponse] =
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

  def deleteCard(request: DeleteCardRequest)(implicit ec: ExecutionContext): Future[DeleteCardResponse] =
      tryToFuture {
        Try {
          val deleted = contentResolverWrapper.deleteById(nineCardsUri = CardUri, id = request.card.id)

          DeleteCardResponse(deleted = deleted)

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def getCardById(request: GetCardByIdRequest)(implicit ec: ExecutionContext): Future[GetCardByIdResponse] =
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

  def getCardByCollection(request: GetAllCardsByCollectionRequest)(implicit ec: ExecutionContext): Future[GetAllCardsByCollectionResponse] =
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

  def updateCard(request: UpdateCardRequest)(implicit ec: ExecutionContext): Future[UpdateCardResponse] =
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

          val updated = contentResolverWrapper.updateById(
            nineCardsUri = CardUri,
            id = request.card.id,
            values = values)

          UpdateCardResponse(updated = updated)

        } recover {
          case NonFatal(e) => throw RepositoryUpdateException()
        }
      }
}
