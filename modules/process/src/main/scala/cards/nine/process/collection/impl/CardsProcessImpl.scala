package cards.nine.process.collection.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.ops.SeqOps._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models
import cards.nine.models.Collection
import cards.nine.models.types.{CardType, NoInstalledAppCardType}
import cards.nine.process.collection.{AddCardRequest, CardException, CollectionProcess}
import cards.nine.process.commons.models.Card
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions
import monix.eval.Task

trait CardsProcessImpl extends CollectionProcess {

  self: CollectionProcessDependencies
    with FormedCollectionConversions
    with ImplicitsPersistenceServiceExceptions =>

  override def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
      size = cardList.size
      cards = addCardListRequest.zipWithIndex map {
        case (item, index) => (collectionId, item, index + size)
      }
      addedCardList <- persistenceServices.addCards(Seq((collectionId, cards)))
    } yield addedCardList).resolve[CardException]

  override def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      _ <- persistenceServices.deleteCard(collectionId, cardId)
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
      _ <- updateCardList(reloadPositions(cardList))
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
      _ <- removeAllCards(collections)
    } yield ()).resolve[CardException]
  }

  override def deleteCards(collectionId: Int, cardIds: Seq[Int]) =
    (for {
      _ <- persistenceServices.deleteCards(collectionId, cardIds)
      cardList <- persistenceServices.fetchCardsByCollection(collectionId)
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

    def reorderAux(card: models.Card) =
      if (card.position != newPosition)
        for {
          cardList <- persistenceServices.fetchCardsByCollection(collectionId)
          _ <- updateCardList(reorderList(cardList,card.position))
        } yield ()
      else TaskService(Task(Right(Unit)))
    (for {
      card <- persistenceServices.findCardById(cardId).resolveOption()
      _ <- reorderAux(card)
    } yield ()).resolve[CardException]
  }

  override def editCard(collectionId: Int, cardId: Int, name: String) =
    (for {
      card <- persistenceServices.findCardById(cardId).resolveOption()
      updatedCard = toCard(card).copy(term = name)
      _ <- updateCard(updatedCard)
    } yield updatedCard).resolve[CardException]

  override def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      cardList <- persistenceServices.fetchCards
      cardsNoInstalled = cardList filter (card => CardType(card.cardType) == NoInstalledAppCardType && card.packageName.contains(packageName))
      cards = toInstalledApp(cardsNoInstalled, app)
      _ <- updateCardList(cards)
    } yield ()).resolve[CardException]

  private[this] def reloadPositions(cardList: Seq[Card]) = cardList.zipWithIndex flatMap {
    case (card, position) if card.position != position => Option(card.copy(position = position))
    case _ => None
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
