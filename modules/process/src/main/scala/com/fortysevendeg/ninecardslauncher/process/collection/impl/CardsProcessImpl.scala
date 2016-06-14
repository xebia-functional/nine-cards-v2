package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.ops.SeqOps._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection.{AddCardRequest, CardException}
import com.fortysevendeg.ninecardslauncher.process.commons.models.Card
import com.fortysevendeg.ninecardslauncher.process.commons.types.{CardType, NoInstalledAppCardType}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard}
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, DeleteCardRequest => ServicesDeleteCardRequest}

trait CardsProcessImpl {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with ImplicitsPersistenceServiceExceptions =>

  def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId))
      size = cardList.size
      cards = addCardListRequest.zipWithIndex map {
        case (item, index) => toAddCardRequest(collectionId, item, index + size)
      }
      addedCardList <- persistenceServices.addCards(collectionId, cards)
    } yield toCardSeq(addedCardList)).resolve[CardException]

  def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      cardList <- getCardsByCollectionId(collectionId)
      _ <- persistenceServices.deleteCard(ServicesDeleteCardRequest(card))
      _ <- updateCardList(moveCardList(cardList, card.position))
    } yield ()).resolve[CardException]

  def reorderCard(collectionId: Int, cardId: Int, newPosition: Int) = {

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
      else Service.success[Unit,CardException](Unit)

    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      _ <- reorderAux(card)
    } yield ()).resolve[CardException]
  }

  def editCard(collectionId: Int, cardId: Int, name: String) =
    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      updatedCard = toUpdatedCard(toCard(card), name)
      _ <- updateCard(updatedCard)
    } yield updatedCard).resolve[CardException]

  def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      cardList <- persistenceServices.fetchCards
      cardsNoInstalled = cardList filter (card => CardType(card.cardType) == NoInstalledAppCardType && card.packageName.contains(packageName))
      cards = toCardSeq(toInstalledApp(cardsNoInstalled, app))
      _ <- updateCardList(cards)
    } yield ()).resolve[CardException]

  private[this] def moveCardList(cardList: Seq[Card], position: Int) =
    cardList map { card =>
      if (card.position > position) toNewPositionCard(card, position - 1) else card
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
