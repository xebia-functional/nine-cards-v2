package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{CardUri, ContentResolverWrapper}
import com.fortysevendeg.ninecardslauncher.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.Card
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class CardRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addCard(request: AddCardRequest)(implicit executionContext: ExecutionContext): Future[AddCardResponse] =
    tryToFuture {
      Try {

        val values = Map[String, Any](
          position -> request.data.position,
          collectionId -> request.collectionId,
          term -> request.data.term,
          packageName -> (request.data.packageName getOrElse ""),
          cardType -> request.data.cardType,
          intent -> request.data.intent,
          imagePath -> request.data.imagePath,
          starRating -> (request.data.starRating getOrElse 0.0d),
          micros -> request.data.micros,
          numDownloads -> (request.data.numDownloads getOrElse ""),
          notification -> (request.data.notification getOrElse ""))

        val id = contentResolverWrapper.insert(
          nineCardsUri = CardUri,
          values = values)

        AddCardResponse(
          card = Card(
            id = id,
            data = request.data))

      } recover {
        case NonFatal(e) => throw RepositoryInsertException()
      }
    }

  def deleteCard(request: DeleteCardRequest)(implicit executionContext: ExecutionContext): Future[DeleteCardResponse] =
    tryToFuture {
      Try {
        val deleted = contentResolverWrapper.deleteById(nineCardsUri = CardUri, id = request.card.id)

        DeleteCardResponse(deleted = deleted)

      } recover {
        case NonFatal(e) => throw RepositoryDeleteException()
      }
    }

  def findCardById(request: FindCardByIdRequest)(implicit executionContext: ExecutionContext): Future[FindCardByIdResponse] =
    tryToFuture {
      Try {
        val card = contentResolverWrapper.findById(
          nineCardsUri = CardUri,
          id = request.id,
          projection = allFields)(getEntityFromCursor(cardEntityFromCursor)) map toCard

        FindCardByIdResponse(card)

      } recover {
        case e: Exception =>
          FindCardByIdResponse(card = None)
      }
    }

  def fetchCardsByCollection(request: FetchCardsByCollectionRequest)(implicit executionContext: ExecutionContext): Future[FetchCardsByCollectionResponse] =
    tryToFuture {
      Try {
        val cards = contentResolverWrapper.fetchAll(
          nineCardsUri = CardUri,
          projection = allFields,
          where = s"$collectionId = ?",
          whereParams = Array(request.collectionId.toString))(getListFromCursor(cardEntityFromCursor)) map toCard

        FetchCardsByCollectionResponse(cards)
      } recover {
        case e: Exception =>
          FetchCardsByCollectionResponse(cards = Seq.empty[Card])
      }
    }

  def updateCard(request: UpdateCardRequest)(implicit executionContext: ExecutionContext): Future[UpdateCardResponse] =
    tryToFuture {
      Try {
        val values = Map[String, Any](
          position -> request.card.data.position,
          term -> request.card.data.term,
          packageName -> (request.card.data.packageName getOrElse ""),
          cardType -> request.card.data.cardType,
          intent -> request.card.data.intent,
          imagePath -> request.card.data.imagePath,
          starRating -> (request.card.data.starRating getOrElse 0.0d),
          micros -> request.card.data.micros,
          numDownloads -> (request.card.data.numDownloads getOrElse ""),
          notification -> (request.card.data.notification getOrElse ""))

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
