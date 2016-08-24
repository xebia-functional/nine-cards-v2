package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.Conversions._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.IterableCursor._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.NotificationUri._
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.{ContentResolverWrapper, IterableCursor, UriCreator}
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCard
import com.fortysevendeg.ninecardslauncher.repository.model.{Card, CardData, CardsWithCollectionId}
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsUri._
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, NineCardsUri}
import com.fortysevendeg.ninecardslauncher.repository.repositories.RepositoryUtils._
import com.fortysevendeg.ninecardslauncher.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps
import scalaz.concurrent.Task

class CardRepository(
  contentResolverWrapper: ContentResolverWrapper,
  uriCreator: UriCreator)
  extends ImplicitsRepositoryExceptions {

  val cardUri = uriCreator.parse(cardUriString)

  val cardNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$cardUriPath")
  val collectionNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$collectionUriPath")

  def addCard(collectionId: Int, data: CardData): CatsService[Card] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(data) + (CardEntity.collectionId -> collectionId)

          val id = contentResolverWrapper.insert(
            uri = cardUri,
            values = values,
            notificationUris = Seq(cardNotificationUri, uriCreator.withAppendedPath(collectionNotificationUri, collectionId.toString)))

          Card(id = id, data = data)
        }
      }
    }

  def addCards(datas: Seq[CardsWithCollectionId]): CatsService[Seq[Card]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = datas flatMap { dataWithCollectionId =>
            dataWithCollectionId.data map { data =>
              createMapValues(data) +
                (CardEntity.collectionId -> dataWithCollectionId.collectionId)
            }
          }

          val collectionNotificationUris = datas.map(_.collectionId).distinct.map { id =>
            uriCreator.withAppendedPath(collectionNotificationUri, id.toString)
          }

          val ids = contentResolverWrapper.inserts(
            authority = NineCardsUri.authorityPart,
            uri = cardUri,
            allValues = values,
            notificationUris = collectionNotificationUris :+ cardNotificationUri)

          (datas flatMap (_.data)) zip ids map {
            case (data, id) => Card(id = id, data = data)
          }
        }
      }
    }

  def deleteCards(where: String = ""): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.delete(
            uri = cardUri,
            where = where,
            notificationUris = Seq(cardNotificationUri))
        }
      }
    }

  def deleteCard(collectionId: Int, card: Card): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.deleteById(
            uri = cardUri,
            id = card.id,
            notificationUris = Seq(cardNotificationUri, uriCreator.withAppendedPath(collectionNotificationUri, collectionId.toString)))
        }
      }
    }

  def findCardById(id: Int): CatsService[Option[Card]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.findById(
            uri = cardUri,
            id = id,
            projection = allFields)(getEntityFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchCardsByCollection(collectionId: Int): CatsService[Seq[Card]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = cardUri,
            projection = allFields,
            where = s"${CardEntity.collectionId} = ?",
            whereParams = Seq(collectionId.toString),
            orderBy = s"${CardEntity.position} asc")(getListFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchCards: CatsService[Seq[Card]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.fetchAll(
            uri = cardUri,
            projection = allFields)(getListFromCursor(cardEntityFromCursor)) map toCard
        }
      }
    }

  def fetchIterableCards(
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): CatsService[IterableCursor[Card]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          contentResolverWrapper.getCursor(
            uri = cardUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy).toIterator(cardFromCursor)
        }
      }
    }

  def updateCard(card: Card): CatsService[Int] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = createMapValues(card.data)

          contentResolverWrapper.updateById(
            uri = cardUri,
            id = card.id,
            values = values,
            notificationUris = Seq(cardNotificationUri))
        }
      }
    }

  def updateCards(cards: Seq[Card]): CatsService[Seq[Int]] =
    CatsService {
      Task {
        XorCatchAll[RepositoryException] {
          val values = cards map { card =>
            (card.id, createMapValues(card.data))
          }

          contentResolverWrapper.updateByIds(
            authority = NineCardsUri.authorityPart,
            uri = cardUri,
            idAndValues = values,
            notificationUris = Seq(cardNotificationUri))
        }
      }
    }

  private[this] def createMapValues(data: CardData) =
    Map[String, Any](
      position -> data.position,
      term -> data.term,
      packageName -> flatOrNull(data.packageName),
      cardType -> data.cardType,
      intent -> data.intent,
      imagePath -> data.imagePath,
      notification -> flatOrNull(data.notification))
}
