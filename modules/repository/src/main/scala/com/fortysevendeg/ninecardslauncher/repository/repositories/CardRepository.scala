package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository.commons.{CardUri, ContentResolverWrapper}
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData}
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, DBUtils}

import scalaz.\/
import scalaz.concurrent.Task

class CardRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addCard(collectionId: Int, data: CardData): Task[NineCardsException \/ Card] =
    Task {
      \/.fromTryCatchThrowable[Card, NineCardsException] {
        val values = Map[String, Any](
          position -> data.position,
          CardEntity.collectionId -> collectionId,
          term -> data.term,
          packageName -> (data.packageName getOrElse ""),
          cardType -> data.cardType,
          intent -> data.intent,
          imagePath -> data.imagePath,
          starRating -> (data.starRating getOrElse 0.0d),
          micros -> data.micros,
          numDownloads -> (data.numDownloads getOrElse ""),
          notification -> (data.notification getOrElse ""))

        val id = contentResolverWrapper.insert(
          nineCardsUri = CardUri,
          values = values)

        Card(id = id, data = data)
      }
    }

  def deleteCard(card: Card): Task[NineCardsException \/ Int] =
    Task {
      \/.fromTryCatchThrowable[Int, NineCardsException] {
        contentResolverWrapper.deleteById(nineCardsUri = CardUri, id = card.id)
      }
    }

  def findCardById(id: Int): Task[NineCardsException \/ Option[Card]] =
    Task {
      \/.fromTryCatchThrowable[Option[Card], NineCardsException] {
        contentResolverWrapper.findById(
          nineCardsUri = CardUri,
          id = id,
          projection = allFields)(getEntityFromCursor(cardEntityFromCursor)) map toCard
      }
    }

  def fetchCardsByCollection(collectionId: Int): Task[NineCardsException \/ Seq[Card]] =
    Task {
      \/.fromTryCatchThrowable[Seq[Card], NineCardsException] {
        contentResolverWrapper.fetchAll(
          nineCardsUri = CardUri,
          projection = allFields,
          where = s"${CardEntity.collectionId} = ?",
          whereParams = Seq(collectionId.toString))(getListFromCursor(cardEntityFromCursor)) map toCard
      }
    }

  def updateCard(card: Card): Task[NineCardsException \/ Int] =
    Task {
      \/.fromTryCatchThrowable[Int, NineCardsException] {
        val values = Map[String, Any](
          position -> card.data.position,
          term -> card.data.term,
          packageName -> (card.data.packageName getOrElse ""),
          cardType -> card.data.cardType,
          intent -> card.data.intent,
          imagePath -> card.data.imagePath,
          starRating -> (card.data.starRating getOrElse 0.0d),
          micros -> card.data.micros,
          numDownloads -> (card.data.numDownloads getOrElse ""),
          notification -> (card.data.notification getOrElse ""))

        contentResolverWrapper.updateById(
          nineCardsUri = CardUri,
          id = card.id,
          values = values)
      }
    }
}
