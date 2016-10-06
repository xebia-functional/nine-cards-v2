package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Card, CardData}
import cards.nine.repository.provider.{CardEntity, NineCardsSqlHelper}
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions
import monix.eval.Task


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
      deleted <- cardRepository.deleteCards(where)
    } yield deleted).resolve[PersistenceServiceException]
  }

  def deleteCardsByCollection(collectionId: Int) =
    (for {
      deleted <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId")
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
