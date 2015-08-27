package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions,
  PersistenceServiceException, PersistenceServices, DeleteCollectionRequest => ServicesDeleteCollectionRequest, DeleteCardRequest => ServicesDeleteCardRequest}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.Answer
import rapture.core.scalazInterop.ResultT

import scalaz.concurrent.Task

class CollectionProcessImpl(
  val collectionProcessConfig: CollectionProcessConfig,
  val persistenceServices: PersistenceServices,
  val contactsServices: ContactsServices)
  extends CollectionProcess
  with ImplicitsPersistenceServiceExceptions
  with FormedCollectionConversions
  with FormedCollectionDependencies {

  override val resourceUtils: ResourceUtils = new ResourceUtils

  override def createCollectionsFromUnformedItems(items: Seq[UnformedItem])(implicit context: ContextSupport) = Service {
    val tasks = createCollections(items, categories) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) = Service {
    val tasks = toAddCollectionRequestByFormedCollection(items) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  override def addCollection(addCollectionRequest: AddCollectionRequest) =
    (for {
      collectionList <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(toAddCollectionRequest(addCollectionRequest, collectionList.size))
    } yield toCollection(collection)).resolve[CollectionException]

  override def deleteCollection(collectionId: Int) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      collectionList <- getCollections
      _ <- updateCollectionList(moveCollectionList(collectionList, collection.position))
    } yield ()).resolve[CollectionException]

  override def reorderCollection(position: Int, newPosition: Int) =
    (for {
      Some(collection) <- persistenceServices.fetchCollectionByPosition(toFetchCollectionByPositionRequest(position))
      collectionList <- getCollections
      _ <- updateCollectionList(reorderCollectionList(collectionList, newPosition, position))
    } yield ()).resolve[CollectionException]

  override def editCollection(collectionId: Int, name: String, appsCategory: Option[String] = None) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedCollection(toCollection(collection), name, appsCategory)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  override def getCardsByCollectionId(collectionId: Int) = (
    persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId)) map toCardSeq).resolve[CardException]

  override def addCardList(collectionId: Int, addCardListRequest: Seq[AddCardRequest]) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId))
      addedCardList <- addCardListService(collectionId, addCardListRequest)
    } yield toCardSeq(addedCardList)).resolve[CardException]

  override def deleteCard(collectionId: Int, cardId: Int) =
    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      cardList <- getCardsByCollectionId(collectionId)
      _ <- persistenceServices.deleteCard(ServicesDeleteCardRequest(card))
      _ <- updateCardList(moveCardList(cardList, card.position))
    } yield ()).resolve[CardException]

  override def reorderCard(collectionId: Int, cardId: Int, newPosition: Int) =
    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      cardList <- getCardsByCollectionId(collectionId)
      _ <- updateCardList(reorderCardList(cardList, newPosition, card.position))
    } yield ()).resolve[CardException]

  override def editCard(collectionId: Int, cardId: Int, name: String) =
    (for {
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(cardId))
      updatedCard = toUpdatedCard(toCard(card), name)
      _ <- updateCard(updatedCard)
    } yield updatedCard).resolve[CardException]

  private[this] def moveCollectionList(collectionList: Seq[Collection], position: Int) =
    collectionList map { collection =>
      if (collection.position > position) toNewPositionCollection(collection, position - 1) else collection
    }

  private[this] def reorderCollectionList(collectionList: Seq[Collection], newPosition: Int, oldPosition: Int): Seq[Collection] =
    collectionList map { collection =>
      val position = collection.position
      (newPosition, oldPosition, position) match {
        case (n, o, p) if n < o && p > n && p < o => toNewPositionCollection(collection, p + 1)
        case (n, o, p) if n > o && p < n && p > o => toNewPositionCollection(collection, p - 1)
        case (n, o, _) if n < o || n > o => collection
        case _ => toNewPositionCollection(collection, newPosition)
      }
    }

  private[this] def findCollectionById(id: Int) =
    (for {
      collection <- persistenceServices.findCollectionById(toFindCollectionByIdRequest(id))
    } yield collection).resolve[CollectionException]

  private[this] def updateCollection(collection: Collection) =
    (for {
      _ <- persistenceServices.updateCollection(toServicesUpdateCollectionRequest(collection))
    } yield ()).resolve[CollectionException]

  private[this] def updateCollectionList(collectionList: Seq[Collection]) = Service {
    val tasks = collectionList map (collection => updateCollection(collection).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CollectionException](c.collect { case Answer(r) => r}))
  }

  private[this] def addCardListService(collectionId: Int, cardList: Seq[AddCardRequest]) = Service {
    val tasks = cardList map (card => persistenceServices.addCard(toAddCardRequest(collectionId, card, cardList.size)).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CardException](c.collect { case Answer(r) => r}))
  }

  private[this] def moveCardList(cardList: Seq[Card], position: Int) =
    cardList map { card =>
      if (card.position > position) toNewPositionCard(card, position - 1) else card
    }

  private[this] def reorderCardList(cardList: Seq[Card], newPosition: Int, oldPosition: Int): Seq[Card] =
    cardList map { card =>
      val position = card.position
      (newPosition, oldPosition, position) match {
        case (n, o, p) if n < o && p > n && p < o => toNewPositionCard(card, p + 1)
        case (n, o, p) if n > o && p < n && p > o => toNewPositionCard(card, p - 1)
        case (n, o, _) if n < o || n > o => card
        case _ => toNewPositionCard(card, newPosition)
      }
    }

  private[this] def updateCard(card: Card) =
    (for {
      _ <- persistenceServices.updateCard(toServicesUpdateCardRequest(card))
    } yield ()).resolve[CardException]

  private[this] def updateCardList(cardList: Seq[Card]) = Service {
    val tasks = cardList map (card => updateCard(card).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CardException](c.collect { case Answer(r) => r}))
  }

}
