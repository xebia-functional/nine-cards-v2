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

  override def deleteCollection(deleteCollectionRequest: DeleteCollectionRequest) =
    (for {
      Some(collection) <- findCollectionById(deleteCollectionRequest.id)
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      collectionList <- getCollections
      _ <- updateCollectionList(moveCollectionList(collectionList, collection.position))
    } yield ()).resolve[CollectionException]

  override def reorderCollection(reorderCollectionRequest: ReorderCollectionRequest) =
    (for {
      Some(collection) <- persistenceServices.fetchCollectionByPosition(toFetchCollectionByPositionRequest(reorderCollectionRequest.position))
      collectionList <- getCollections
      _ <- updateCollectionList(reorderCollectionList(collectionList, reorderCollectionRequest.newPosition, reorderCollectionRequest.position))
    } yield ()).resolve[CollectionException]

  override def editCollection(editCollectionRequest: EditCollectionRequest) =
    (for {
      Some(collection) <- findCollectionById(editCollectionRequest.id)
      updatedCollection = toUpdatedCollection(toCollection(collection), editCollectionRequest.name, editCollectionRequest.appsCategory)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  override def getCardsByCollectionId(collectionId: Int) = (
    persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId)) map toCardSeq).resolve[CardException]

  override def addCard(addCardRequest: AddCardRequest) =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(addCardRequest.collectionId))
      card <- persistenceServices.addCard(toAddCardRequest(addCardRequest, cardList.size))
    } yield toCard(card)).resolve[CardException]

  override def deleteCard(deleteCardRequest: DeleteCardRequest) =
    (for {
      cardList <- getCardsByCollectionId(deleteCardRequest.collectionId)
      Some(card) <- persistenceServices.findCardById(toFindCardByIdRequest(deleteCardRequest.cardId))
      _ <- persistenceServices.deleteCard(ServicesDeleteCardRequest(card))
      _ <- updateCardList(moveCardList(cardList, card.position))
    } yield ()).resolve[CardException]

  override def reorderCard(reorderCardRequest: ReorderCardRequest) =
    (for {
      Some(card) <-persistenceServices.findCardById(toFindCardByIdRequest(reorderCardRequest.cardId))
      cardList <- getCardsByCollectionId(reorderCardRequest.collectionId)
      _ <- updateCardList(reorderCardList(cardList, reorderCardRequest.newPosition, card.position))
    } yield ()).resolve[CardException]

  private[this] def moveCollectionList(collectionList: Seq[Collection], position: Int) =
    collectionList map { collection =>
      if (collection.position > position) toNewPositionCollection(collection, position - 1) else collection
    }

  private[this] def reorderCollectionList(collectionList: Seq[Collection], newPosition: Int, oldPosition: Int): Seq[Collection] =
    collectionList map { collection =>
      val position = collection.position
      if (newPosition < oldPosition)
        if (position > newPosition && position < oldPosition) toNewPositionCollection(collection, position + 1) else collection
      else if (newPosition > position)
        if (position < newPosition && position > oldPosition) toNewPositionCollection(collection, position - 1) else collection
      else toNewPositionCollection(collection, newPosition)
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

  private[this] def moveCardList(cardList: Seq[Card], position: Int) =
    cardList map { card =>
      if (card.position > position) toNewPositionCard(card, position - 1) else card
    }

  private[this] def reorderCardList(cardList: Seq[Card], newPosition: Int, oldPosition: Int): Seq[Card] =
    cardList map { card =>
      val position = card.position
      if (newPosition < oldPosition)
        if (position > newPosition && position < oldPosition) toNewPositionCard(card, position + 1) else card
      else if (newPosition > position)
        if (position < newPosition && position > oldPosition) toNewPositionCard(card, position - 1) else card
      else toNewPositionCard(card, newPosition)
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
