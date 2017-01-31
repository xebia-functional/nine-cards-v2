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

package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Card, CardData}
import cards.nine.repository.provider.{CardEntity, NineCardsSqlHelper}
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions

trait CardPersistenceServicesImpl extends PersistenceServices {

  self: Conversions with PersistenceDependencies with ImplicitsPersistenceServiceExceptions =>

  def addCard(collectionId: Int, card: CardData) =
    (for {
      card <- cardRepository.addCard(collectionId, toRepositoryCardData(card))
    } yield toCard(card)).resolve[PersistenceServiceException]

  def addCards(cardsByCollectionId: Seq[(Int, Seq[CardData])]) =
    (for {
      cards <- cardRepository.addCards(cardsByCollectionId map toCardsWithCollectionId)
    } yield cards map toCard).resolve[PersistenceServiceException]

  def deleteAllCards() =
    (for {
      deleted <- cardRepository.deleteCards()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      deleted <- cardRepository.deleteCard(collectionId, cardId)
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCards(collectionId: Int, cardIds: Seq[Int]) = {
    val where = s"${NineCardsSqlHelper.id} IN (${cardIds.mkString(",")})"
    (for {
      deleted <- cardRepository.deleteCards(Some(collectionId), where)
    } yield deleted).resolve[PersistenceServiceException]
  }

  def deleteCardsByCollection(collectionId: Int) =
    (for {
      deleted <- cardRepository.deleteCards(
        maybeCollectionId = None,
        where = s"${CardEntity.collectionId} = $collectionId")
    } yield deleted).resolve[PersistenceServiceException]

  def fetchCardsByCollection(collectionId: Int) =
    (for {
      cards <- cardRepository.fetchCardsByCollection(collectionId)
    } yield cards map toCard).resolve[PersistenceServiceException]

  def fetchCards =
    (for {
      cards <- cardRepository.fetchCards
    } yield cards map toCard).resolve[PersistenceServiceException]

  def findCardById(cardId: Int) =
    (for {
      maybeCard <- cardRepository.findCardById(cardId)
    } yield maybeCard map toCard).resolve[PersistenceServiceException]

  def updateCard(card: Card) =
    (for {
      updated <- cardRepository.updateCard(toRepositoryCard(card))
    } yield updated).resolve[PersistenceServiceException]

  def updateCards(cards: Seq[Card]) =
    (for {
      updated <- cardRepository.updateCards(cards map toRepositoryCard)
    } yield updated).resolve[PersistenceServiceException]

}
