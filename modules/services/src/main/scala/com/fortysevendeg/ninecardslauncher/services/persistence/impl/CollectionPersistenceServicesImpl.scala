package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.XorCatchAll
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard, Collection => RepositoryCollection, Moment => RepositoryMoment}
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card, Collection, Moment}

import scalaz.concurrent.Task

trait CollectionPersistenceServicesImpl extends PersistenceServices {

  self: Conversions
    with PersistenceDependencies
    with ImplicitsPersistenceServiceExceptions
    with MomentPersistenceServicesImpl
    with CardPersistenceServicesImpl =>

  def addCollection(request: AddCollectionRequest) =
    (for {
      collection <- collectionRepository.addCollection(toRepositoryCollectionData(request))
      addedCards <- addCards(Seq(AddCardWithCollectionIdRequest(collection.id, request.cards)))
      _ <- addMoments(Seq(request.moment map (_.copy(collectionId = Option(collection.id)))).flatten)
    } yield toCollection(collection).copy(cards = addedCards)).resolve[PersistenceServiceException]

  def addCollections(requests: Seq[AddCollectionRequest]) = {
    val collectionsData = requests map toRepositoryCollectionData
    val cardsData = requests map (_.cards)
    val momentsData = requests map (_.moment)
    (for {
      collections <- collectionRepository.addCollections(collectionsData)
      cards = collections.zip(cardsData) map {
        case (collection, cardsRequest) => AddCardWithCollectionIdRequest(collection.id, cardsRequest)
      }
      addedCards <- addCards(cards)
      moments = collections.zip(momentsData) flatMap {
        case (collection, momentRequest) => momentRequest map (_.copy(collectionId = Option(collection.id)))
      }
      _ <- addMoments(moments)
    } yield collections map toCollection).resolve[PersistenceServiceException]
  }

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
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest) =
    (for {
      collection <- collectionRepository.fetchCollectionByPosition(request.position)
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def findCollectionById(request: FindCollectionByIdRequest) =
    (for {
      collection <- collectionRepository.findCollectionById(request.id)
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def updateCollection(request: UpdateCollectionRequest) =
    (for {
      updated <- collectionRepository.updateCollection(toRepositoryCollection(request))
    } yield updated).resolve[PersistenceServiceException]

  def updateCollections(request: UpdateCollectionsRequest) =
    (for {
      updated <- collectionRepository.updateCollections(request.updateCollectionsRequests map toRepositoryCollection)
    } yield updated).resolve[PersistenceServiceException]

  private[this] def deleteCards(cards: Seq[Card]): CatsService[Int] = {
    val deletedCards = cards map {
      card =>
        cardRepository.deleteCard(toRepositoryCard(card)).value
    }

    CatsService(
      Task.gatherUnordered(deletedCards) map (list =>
          XorCatchAll[PersistenceServiceException](list.collect { case Xor.Right(value) => value }.sum)))
  }

  private[this] def fetchCards(maybeCollection: Option[RepositoryCollection]): CatsService[Seq[RepositoryCard]] = {
    maybeCollection match {
      case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
      case None => CatsService(Task(Xor.Right(Seq.empty)))
    }
  }

  private[this] def fetchCards(collections: Seq[RepositoryCollection]): CatsService[Seq[Collection]] = {
    val result = collections map {
      collection =>
        (for {
          cards <- cardRepository.fetchCardsByCollection(collection.id)
          moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
        } yield toCollection(collection, cards, moments.headOption)).value
    }

    CatsService(
      Task.gatherUnordered(result) map (list =>
          XorCatchAll[PersistenceServiceException](list.collect { case Xor.Right(collection) => collection })))
  }

  private[this] def deleteMoment(maybeMoment: Option[Moment]): CatsService[Unit] = {
    maybeMoment match {
      case Some(moment) => momentRepository.deleteMoment(toRepositoryMoment(moment)) map (_ => ())
      case None => CatsService(Task(Xor.right((): Unit)))
    }
  }

  private[this] def getMomentsByCollection(maybeCollection: Option[RepositoryCollection]): CatsService[Seq[RepositoryMoment]] = {
    maybeCollection match {
      case Some(collection) => momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
      case None => CatsService(Task(Xor.Right(Seq.empty)))
    }
  }

}
