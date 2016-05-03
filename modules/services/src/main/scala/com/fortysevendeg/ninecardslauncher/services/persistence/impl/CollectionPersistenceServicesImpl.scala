package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard, Collection => RepositoryCollection}
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card, Collection, Moment}
import rapture.core.{Answer, Result}

import scalaz.concurrent.Task

trait CollectionPersistenceServicesImpl {

  self: Conversions
    with PersistenceDependencies
    with ImplicitsPersistenceServiceExceptions
    with CardPersistenceServicesImpl =>

  def addCollection(request: AddCollectionRequest) =
    (for {
      collection <- collectionRepository.addCollection(toRepositoryCollectionData(request))
      addedCards <- addCards(request.cards map (_.copy(collectionId = Option(collection.id))))
      _ <- addMoment(request.moment map (_.copy(collectionId = Option(collection.id))))
    } yield toCollection(collection).copy(cards = addedCards)).resolve[PersistenceServiceException]

  def deleteAllCollections() =
    (for {
      deleted <- collectionRepository.deleteCollections()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCollection(request: DeleteCollectionRequest) = {
    (for {
      deletedCards <- deleteCards(request.collection.cards)
      deletedCollection <- collectionRepository.deleteCollection(toRepositoryCollection(request.collection))
      _ <- deleteMoment(request.collection.moment)
    } yield deletedCollection).resolve[PersistenceServiceException]
  }

  def fetchCollections =
    (for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections
      collectionWithCards <- fetchCards(collectionsWithoutCards)
    } yield collectionWithCards.sortWith(_.position < _.position)).resolve[PersistenceServiceException]

  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest) =
    (for {
      collection <- collectionRepository.fetchCollectionBySharedCollectionId(request.sharedCollectionId)
      cards <- fetchCards(collection)
      moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${(collection map (_.id)).orNull}")
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest) =
    (for {
      collection <- collectionRepository.fetchCollectionByPosition(request.position)
      cards <- fetchCards(collection)
      moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${(collection map (_.id)).orNull}")
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def findCollectionById(request: FindCollectionByIdRequest) =
    (for {
      collection <- collectionRepository.findCollectionById(request.id)
      cards <- fetchCards(collection)
      moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${request.id}")
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def updateCollection(request: UpdateCollectionRequest) =
    (for {
      updated <- collectionRepository.updateCollection(toRepositoryCollection(request))
    } yield updated).resolve[PersistenceServiceException]

  def updateCollections(request: UpdateCollectionsRequest) =
    (for {
      updated <- collectionRepository.updateCollections(request.updateCollectionsRequests map toRepositoryCollection)
    } yield updated).resolve[PersistenceServiceException]

  private[this] def addCards(cards: Seq[AddCardRequest]): ServiceDef2[Seq[Card], PersistenceServiceException] = {
    val addedCards = cards map {
      addCard(_).run
    }

    Service(
      Task.gatherUnordered(addedCards) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(card) => card })))
  }

  private[this] def deleteCards(cards: Seq[Card]): ServiceDef2[Int, PersistenceServiceException] = {
    val deletedCards = cards map {
      card =>
        cardRepository.deleteCard(toRepositoryCard(card)).run
    }

    Service(
      Task.gatherUnordered(deletedCards) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(value) => value }.sum)))
  }

  private[this] def fetchCards(maybeCollection: Option[RepositoryCollection]): ServiceDef2[Seq[RepositoryCard], RepositoryException] = {
    maybeCollection match {
      case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
      case None => Service(Task(Result.answer[Seq[RepositoryCard], RepositoryException](Seq.empty)))
    }
  }

  private[this] def fetchCards(collections: Seq[RepositoryCollection]): ServiceDef2[Seq[Collection], PersistenceServiceException] = {
    val result = collections map {
      collection =>
        (for {
          cards <- cardRepository.fetchCardsByCollection(collection.id)
          moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
        } yield toCollection(collection, cards, moments.headOption)).run
    }

    Service(
      Task.gatherUnordered(result) map (
        list =>
          CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => collection })))
  }

  private[this] def addMoment(maybeMoment: Option[AddMomentRequest]): ServiceDef2[Unit, RepositoryException] = {
    maybeMoment match {
      case Some(moment) => momentRepository.addMoment(toRepositoryMomentData(moment)) map (_ => ())
      case None => Service(Task(Result.answer[Unit, RepositoryException]((): Unit)))
    }
  }

  private[this] def deleteMoment(maybeMoment: Option[Moment]): ServiceDef2[Unit, RepositoryException] = {
    maybeMoment match {
      case Some(moment) => momentRepository.deleteMoment(toRepositoryMoment(moment)) map (_ => ())
      case None => Service(Task(Result.answer[Unit, RepositoryException]((): Unit)))
    }
  }
}
