/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.repository.repositories

import cards.nine.commons.CatchAll
import cards.nine.commons.contentresolver.Conversions._
import cards.nine.commons.contentresolver.NotificationUri._
import cards.nine.commons.contentresolver.{ContentResolverWrapper, UriCreator}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.IterableCursor
import cards.nine.models.IterableCursor._
import cards.nine.repository.Conversions.toCard
import cards.nine.repository.model.{Card, CardData, CardsWithCollectionId}
import cards.nine.repository.provider.CardEntity._
import cards.nine.repository.provider.NineCardsUri._
import cards.nine.repository.provider.{CardEntity, NineCardsUri}
import cards.nine.repository.repositories.RepositoryUtils._
import cards.nine.repository.{ImplicitsRepositoryExceptions, RepositoryException}

import scala.language.postfixOps

class CardRepository(contentResolverWrapper: ContentResolverWrapper, uriCreator: UriCreator)
    extends ImplicitsRepositoryExceptions {

  val cardUri = uriCreator.parse(cardUriString)

  val cardNotificationUri = uriCreator.parse(s"$baseUriNotificationString/$cardUriPath")
  val collectionNotificationUri =
    uriCreator.parse(s"$baseUriNotificationString/$collectionUriPath")

  def addCard(collectionId: Int, data: CardData): TaskService[Card] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(data) + (CardEntity.collectionId -> collectionId)

        val id = contentResolverWrapper.insert(
          uri = cardUri,
          values = values,
          notificationUris = Seq(
            cardNotificationUri,
            uriCreator.withAppendedPath(collectionNotificationUri, collectionId.toString)))

        Card(id = id, data = data)
      }
    }

  def addCards(datas: Seq[CardsWithCollectionId]): TaskService[Seq[Card]] =
    TaskService {
      CatchAll[RepositoryException] {
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

  def deleteCards(maybeCollectionId: Option[Int] = None, where: String = ""): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val collectionUri = maybeCollectionId match {
          case Some(id) if id != 0 =>
            uriCreator.withAppendedPath(collectionNotificationUri, id.toString)
          case _ => collectionNotificationUri
        }
        contentResolverWrapper.delete(
          uri = cardUri,
          where = where,
          notificationUris = Seq(cardNotificationUri, collectionUri))
      }
    }

  def deleteCard(collectionId: Int, cardId: Int): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.deleteById(
          uri = cardUri,
          id = cardId,
          notificationUris = Seq(
            cardNotificationUri,
            uriCreator.withAppendedPath(collectionNotificationUri, collectionId.toString)))
      }
    }

  def findCardById(id: Int): TaskService[Option[Card]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.findById(uri = cardUri, id = id, projection = allFields)(
          getEntityFromCursor(cardEntityFromCursor)) map toCard
      }
    }

  def fetchCardsByCollection(collectionId: Int): TaskService[Seq[Card]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetchAll(
          uri = cardUri,
          projection = allFields,
          where = s"${CardEntity.collectionId} = ?",
          whereParams = Seq(collectionId.toString),
          orderBy = s"${CardEntity.position} asc")(getListFromCursor(cardEntityFromCursor)) map toCard
      }
    }

  def fetchCards: TaskService[Seq[Card]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper.fetchAll(uri = cardUri, projection = allFields)(
          getListFromCursor(cardEntityFromCursor)) map toCard
      }
    }

  def fetchIterableCards(
      where: String = "",
      whereParams: Seq[String] = Seq.empty,
      orderBy: String = ""): TaskService[IterableCursor[Card]] =
    TaskService {
      CatchAll[RepositoryException] {
        contentResolverWrapper
          .getCursor(
            uri = cardUri,
            projection = allFields,
            where = where,
            whereParams = whereParams,
            orderBy = orderBy)
          .toIterator(cardFromCursor)
      }
    }

  def updateCard(card: Card): TaskService[Int] =
    TaskService {
      CatchAll[RepositoryException] {
        val values = createMapValues(card.data)

        contentResolverWrapper.updateById(
          uri = cardUri,
          id = card.id,
          values = values,
          notificationUris = Seq(cardNotificationUri))
      }
    }

  def updateCards(cards: Seq[Card]): TaskService[Seq[Int]] =
    TaskService {
      CatchAll[RepositoryException] {
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

  private[this] def createMapValues(data: CardData) =
    Map[String, Any](
      position     -> data.position,
      term         -> data.term,
      packageName  -> flatOrNull(data.packageName),
      cardType     -> data.cardType,
      intent       -> data.intent,
      imagePath    -> flatOrNull(data.imagePath),
      notification -> flatOrNull(data.notification))
}
