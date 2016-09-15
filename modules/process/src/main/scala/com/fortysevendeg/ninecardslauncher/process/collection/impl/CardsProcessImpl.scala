package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CardException, CollectionProcess}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Card
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard}
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddCardWithCollectionIdRequest, ImplicitsPersistenceServiceExceptions}
import monix.eval.Task

trait CardsProcessImpl extends CollectionProcess {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with ImplicitsPersistenceServiceExceptions =>

  override def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId))
      size = cardList.size
      cards = addCardListRequest.zipWithIndex map {
        case (item, index) => toAddCardRequest(collectionId, item, index + size)
      }
      addedCardList <- persistenceServices.addCards(Seq(AddCardWithCollectionIdRequest(collectionId, cards)))
    } yield toCardSeq(addedCardList)).resolve[CardException]

  override def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      _ <- persistenceServices.deleteCard(collectionId, cardId)
      cardList <- getCardsByCollectionId(collectionId)
      _ <- updateCardList(reloadPositions(cardList))
    } yield ()).resolve[CardException]

  override def deleteCards(collectionId: Int, cardIds: Seq[Int]) =
    (for {
      _ <- persistenceServices.deleteCards(collectionId, cardIds)
      cardList <- getCardsByCollectionId(collectionId)
      _ <- updateCardList(reloadPositions(cardList))
    } yield ()).resolve[CardException]

  override def reorderCard(collectionId: Int, cardId: Int, newPosition: Int) = {

    def reorderList(cardList: Seq[Card], oldPosition: Int): Seq[Card] = {
      val (init, end) = if (oldPosition > newPosition) (newPosition, oldPosition) else (oldPosition, newPosition)
      cardList
        .reorderRange(oldPosition, newPosition)
        .zip(init to end)
        .map( { case (card, index) => card.copy(position = index) })
    }

    def reorderAux(card: ServicesCard) =
      if (card.position != newPosition)
        for {
          cardList <- getCardsByCollectionId(collectionId)
          _ <- updateCardList(reorderList(cardList,card.position))
        } yield ()
      else TaskService(Task(Right(Unit)))
    (for {
      card <- persistenceServices.findCardById(toFindCardByIdRequest(cardId)).resolveOption()
      _ <- reorderAux(card)
    } yield ()).resolve[CardException]
  }

  override def editCard(collectionId: Int, cardId: Int, name: String) =
    (for {
      card <- persistenceServices.findCardById(toFindCardByIdRequest(cardId)).resolveOption()
      updatedCard = toCard(card).copy(term = name)
      _ <- updateCard(updatedCard)
    } yield updatedCard).resolve[CardException]

  override def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      cardList <- persistenceServices.fetchCards
      cardsNoInstalled = cardList filter (card => CardType(card.cardType) == NoInstalledAppCardType && card.packageName.contains(packageName))
      cards = toCardSeq(toInstalledApp(cardsNoInstalled, app))
      _ <- updateCardList(cards)
    } yield ()).resolve[CardException]

  private[this] def reloadPositions(cardList: Seq[Card]) = cardList.zipWithIndex flatMap {
    case (card, position) if card.position != position => Option(card.copy(position = position))
    case _ => None
  }

  private[this] def updateCard(card: Card) =
    (for {
      _ <- persistenceServices.updateCard(toServicesUpdateCardRequest(card))
    } yield ()).resolve[CardException]

  private[this] def updateCardList(cardList: Seq[Card]) =
    (for {
      _ <- persistenceServices.updateCards(toServicesUpdateCardsRequest(cardList))
    } yield ()).resolve[CardException]

  private[this] def getCardsByCollectionId(collectionId: Int) = (
    persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId)) map toCardSeq).resolve[CardException]

}
