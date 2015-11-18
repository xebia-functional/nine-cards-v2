package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.CardType
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{DeleteCardRequest => ServicesDeleteCardRequest, DeleteCollectionRequest => ServicesDeleteCollectionRequest, FindCollectionByIdRequest, ImplicitsPersistenceServiceExceptions, PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card => ServicesCard}
import rapture.core.Answer
import rapture.core.scalazInterop.ResultT
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._

import scalaz.concurrent.Task

class CollectionProcessImpl(
  val collectionProcessConfig: CollectionProcessConfig,
  val persistenceServices: PersistenceServices,
  val contactsServices: ContactsServices,
  val appsServices: AppsServices)
  extends CollectionProcess
  with ImplicitsPersistenceServiceExceptions
  with FormedCollectionConversions
  with FormedCollectionDependencies {

  override val resourceUtils: ResourceUtils = new ResourceUtils

  val minAppsGenerateCollections = 1

  override def createCollectionsFromUnformedItems(apps: Seq[UnformedApp], contacts: Seq[UnformedContact])(implicit context: ContextSupport) = Service {
    val tasks = createCollections(apps, contacts, categories, minAppsToAdd) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    (for {
      apps <- appsServices.getInstalledApplications
      collections <- createCollectionsAndFillData(items, apps)
    } yield collections).resolve[CollectionException]

  override def generatePrivateCollections(apps: Seq[UnformedApp])(implicit context: ContextSupport) = Service {
    Task {
      CatchAll[CollectionException] {
        createPrivateCollections(apps, categories, minAppsGenerateCollections)
      }
    }
  }

  override def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  override def getCollectionById(id: Int) =
    (for {
      collection <- persistenceServices.findCollectionById(FindCollectionByIdRequest(id))
    } yield collection map toCollection).resolve[CollectionException]

  override def addCollection(addCollectionRequest: AddCollectionRequest) =
    (for {
      collectionList <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(toAddCollectionRequest(addCollectionRequest, collectionList.size))
    } yield toCollection(collection)).resolve[CollectionException]

  override def deleteCollection(collectionId: Int) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      _ <- removeCards(collection.cards)
      collectionList <- getCollections
      _ <- updateCollectionList(moveCollectionList(collectionList, collection.position))
    } yield ()).resolve[CollectionException]

  override def reorderCollection(position: Int, newPosition: Int) =
    (for {
      Some(collection) <- persistenceServices.fetchCollectionByPosition(toFetchCollectionByPositionRequest(position))
      collectionList <- getCollections
      _ <- updateCollectionList(reorderCollectionList(collectionList, newPosition, position))
    } yield ()).resolve[CollectionException]

  override def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest) =
    (for {
      Some(collection) <- findCollectionById(collectionId)
      updatedCollection = toUpdatedCollection(toCollection(collection), editCollectionRequest)
      _ <- updateCollection(updatedCollection)
    } yield updatedCollection).resolve[CollectionException]

  override def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]): ResultT[Task, Seq[Card], CardException] =
    (for {
      cardList <- persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId))
      addedCardList <- addCardList(collectionId, addCardListRequest, cardList.size)
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

  override def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport) =
    (for {
      app <- appsServices.getApplication(packageName)
      cardList <- persistenceServices.fetchCards
      cardsNoInstalled = cardList filter (card => card.cardType == CardType.noInstalledApp && card.packageName.contains(packageName))
      card = toCardSeq(toInstalledApp(cardsNoInstalled, app))
      _ <- updateCardList(toCardSeq(toInstalledApp(cardsNoInstalled, app)))
    } yield ()).resolve[CardException]

  private[this] def createCollectionsAndFillData(items: Seq[FormedCollection], apps: Seq[Application])
    (implicit context: ContextSupport): ServiceDef2[List[Collection], PersistenceServiceException] = Service {
    val tasks = toAddCollectionRequestByFormedCollection(fillImageUri(items, apps)) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }

  private[this] def getCardsByCollectionId(collectionId: Int) = (
    persistenceServices.fetchCardsByCollection(toFetchCardsByCollectionRequest(collectionId)) map toCardSeq).resolve[CardException]

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

  private[this] def addCardList(collectionId: Int, cardList: Seq[AddCardRequest], position: Int) = Service {
    val tasks = cardList.indices map (item => persistenceServices.addCard(toAddCardRequest(collectionId, cardList(item), position + item)).run)
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

  private[this] def removeCards(cards: Seq[ServicesCard]) = Service {
    val tasks = cards map (card => persistenceServices.deleteCard(ServicesDeleteCardRequest(card)).run)
    Task.gatherUnordered(tasks) map (c => CatchAll[CardException](c.collect { case Answer(r) => r}))
  }

}
