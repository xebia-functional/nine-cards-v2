package cards.nine.process.collection.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models
import cards.nine.models._
import cards.nine.models.types.{AppCardType, NoInstalledAppCardType}
import cards.nine.process.collection.{
  CardException,
  CollectionProcess,
  ImplicitsCollectionException
}
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions
import monix.eval.Task

trait CardsProcessImpl
    extends CollectionProcess
    with NineCardsIntentConversions
    with ImplicitsCollectionException {

  self: CollectionProcessDependencies with ImplicitsPersistenceServiceExceptions =>

  override def addCards(collectionId: Int, cards: Seq[CardData]) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
      size = cardList.size
      cardsToAdd = cards.zipWithIndex map {
        case (card, index) => card.copy(position = index + size)
      }
      addedCardList <- persistenceServices.addCards(Seq((collectionId, cardsToAdd)))
    } yield addedCardList).resolve[CardException]

  override def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      _        <- persistenceServices.deleteCard(collectionId, cardId)
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
      _        <- updateCardList(reloadPositions(cardList))
    } yield ()).resolve[CardException]

  override def deleteAllCardsByPackageName(packageName: String) = {

    def removeAllCards(collections: Seq[Collection]): TaskService[Unit] = {
      val tasks = collections flatMap { collection =>
        collection.cards.filter(_.packageName == Option(packageName)) map { card =>
          deleteCard(collection.id, card.id).value
        }
      }
      TaskService(Task.gatherUnordered(tasks).map(_ => Right((): Unit)))
    }

    (for {
      collections <- persistenceServices.fetchCollections
      _           <- removeAllCards(collections)
    } yield ()).resolve[CardException]
  }

  override def deleteCards(collectionId: Int, cardIds: Seq[Int]) =
    (for {
      _        <- persistenceServices.deleteCards(collectionId, cardIds)
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
      _        <- updateCardList(reloadPositions(cardList))
    } yield ()).resolve[CardException]

  override def reorderCard(collectionId: Int, cardId: Int, newPosition: Int) = {

    def reorderList(cardList: Seq[Card], oldPosition: Int): Seq[Card] = {
      val (init, end) =
        if (oldPosition > newPosition) (newPosition, oldPosition) else (oldPosition, newPosition)
      cardList
        .reorderRange(oldPosition, newPosition)
        .zip(init to end)
        .map({ case (card, index) => card.copy(position = index) })
    }

    def reorderAux(card: models.Card) =
      if (card.position != newPosition)
        for {
          cardList <- persistenceServices.fetchCardsByCollection(collectionId)
          _        <- updateCardList(reorderList(cardList, card.position))
        } yield ()
      else TaskService(Task(Right(Unit)))
    (for {
      card <- persistenceServices
        .findCardById(cardId)
        .resolveOption(s"Can't find the card with id $cardId")
      _ <- reorderAux(card)
    } yield ()).resolve[CardException]
  }

  override def editCard(collectionId: Int, cardId: Int, name: String) =
    (for {
      card <- persistenceServices
        .findCardById(cardId)
        .resolveOption(s"Can't find the card with id $cardId")
      updatedCard = card.copy(term = name)
      _ <- updateCard(updatedCard)
    } yield updatedCard).resolve[CardException]

  override def updateNoInstalledCardsInCollections(packageName: String)(
      implicit contextSupport: ContextSupport) = {

    def toCard(cards: Seq[Card], app: ApplicationData)(
        implicit contextSupport: ContextSupport): Seq[Card] = {
      val intent = toNineCardIntent(app)
      cards map (_.copy(term = app.name, cardType = AppCardType, intent = intent))
    }

    (for {
      app      <- appsServices.getApplication(packageName)
      cardList <- persistenceServices.fetchCards
      cardsNoInstalled = cardList filter (card =>
                                            card.cardType == NoInstalledAppCardType && card.packageName
                                              .contains(packageName))
      cards = toCard(cardsNoInstalled, app)
      _ <- updateCardList(cards)
    } yield ()).resolve[CardException]

  }

  private[this] def reloadPositions(cardList: Seq[Card]) = cardList.zipWithIndex flatMap {
    case (card, position) if card.position != position => Option(card.copy(position = position))
    case _                                             => None
  }

  private[this] def updateCard(card: Card) =
    (for {
      _ <- persistenceServices.updateCard(card)
    } yield ()).resolve[CardException]

  private[this] def updateCardList(cardList: Seq[Card]) =
    (for {
      _ <- persistenceServices.updateCards(cardList)
    } yield ()).resolve[CardException]

}
