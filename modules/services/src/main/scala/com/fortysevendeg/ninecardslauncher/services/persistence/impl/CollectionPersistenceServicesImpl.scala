package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.model.{Card => RepositoryCard, Collection => RepositoryCollection, Moment => RepositoryMoment}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, MomentEntity}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{Card, Collection, Moment}
import rapture.core.{Answer, Result}

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
      _ <- createOrUpdateMoments(Seq(request.moment map (_.copy(collectionId = Option(collection.id)))).flatten)
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
      _ <- createOrUpdateMoments(moments)
    } yield collections map toCollection).resolve[PersistenceServiceException]
  }

  def deleteAllCollections() =
    (for {
      deleted <- collectionRepository.deleteCollections()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCollection(request: DeleteCollectionRequest) = {
    (for {
      _ <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = ${request.collection.id}")
      deletedCollection <- collectionRepository.deleteCollection(toRepositoryCollection(request.collection))
      _ <- unlinkCollectionInMoment(request.collection.moment)
    } yield deletedCollection).resolve[PersistenceServiceException]
  }

  def fetchCollections =
    (for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections
      collectionWithCards <- fetchCards(collectionsWithoutCards)
    } yield collectionWithCards.sortWith(_.position < _.position)).resolve[PersistenceServiceException]

  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest) = {

    def fetchCollection(): ServiceDef2[Option[RepositoryCollection], RepositoryException] =
      if(request.original) {
        collectionRepository.fetchCollectionByOriginalSharedCollectionId(request.sharedCollectionId)
      } else {
        collectionRepository.fetchCollectionBySharedCollectionId(request.sharedCollectionId)
      }

    (for {
      collection <- fetchCollection()
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]
  }

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

  private[this] def unlinkCollectionInMoment(maybeMoment: Option[Moment]): ServiceDef2[Unit, RepositoryException] = {
    maybeMoment match {
      case Some(moment) => momentRepository.updateMoment(toRepositoryMomentWithoutCollection(moment)) map (_ => ())
      case None => Service(Task(Result.answer[Unit, RepositoryException]((): Unit)))
    }
  }

  private[this] def getMomentsByCollection(maybeCollection: Option[RepositoryCollection]): ServiceDef2[Seq[RepositoryMoment], RepositoryException] = {
    maybeCollection match {
      case Some(collection) => momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
      case None => Service(Task(Result.answer[Seq[RepositoryMoment], RepositoryException](Seq.empty)))
    }
  }

  private[this] def createOrUpdateMoments(requests: Seq[AddMomentRequest]) = {

    def createOrUpdateMoment(request: AddMomentRequest) = {

      def createOrUpdate(maybeMoment: Option[Moment]) = maybeMoment match {
        case Some(moment) => updateMoment(UpdateMomentRequest(
          id = moment.id,
          collectionId = request.collectionId,
          timeslot = moment.timeslot,
          wifi = moment.wifi,
          headphone = moment.headphone,
          momentType = moment.momentType
        ))
        case None => addMoment(request)
      }

      for {
        moments <- fetchMoments
        _ <- createOrUpdate(moments find (_.momentType == request.momentType))
      } yield ()
    }

    val s = requests map (r => createOrUpdateMoment(r).run)

    Service(Task.gatherUnordered(s) map (list =>
      CatchAll[PersistenceServiceException](list.collect { case Answer(value) => value })))

  }

}
