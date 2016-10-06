package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{CollectionData, Collection, Moment}
import cards.nine.repository.model.{Card => RepositoryCard, Collection => RepositoryCollection, Moment => RepositoryMoment}
import cards.nine.repository.provider.{CardEntity, MomentEntity}
import cards.nine.services.persistence._
import cards.nine.services.persistence.conversions.Conversions
import cats.data.EitherT
import monix.eval.Task

trait CollectionPersistenceServicesImpl extends PersistenceServices {

  self: Conversions
    with PersistenceDependencies
    with ImplicitsPersistenceServiceExceptions
    with MomentPersistenceServicesImpl
    with CardPersistenceServicesImpl =>

  def addCollection(collection: CollectionData) =
    (for {
      collectionAdded <- collectionRepository.addCollection(toRepositoryCollectionData(collection))
      addedCards <- addCards(Seq((collectionAdded.id, collection.cards)))
      _ <- createOrUpdateMoments(Seq(collection.moment map (_.copy(collectionId = Option(collectionAdded.id)))).flatten)
    } yield toCollection(collectionAdded).copy(cards = addedCards)).resolve[PersistenceServiceException]

  def addCollections(collections: Seq[CollectionData]) = {
    val collectionsData = collections map toRepositoryCollectionData
    val cardsData = collections map (_.cards)
    val momentsData = collections map (_.moment)
    (for {
      collections <- collectionRepository.addCollections(collectionsData)
      cards = collections.zip(cardsData) map {
        case (collection, cardsRequest) => (collection.id, cardsRequest)
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

  def deleteCollection(collection: Collection) = {
    (for {
      _ <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = ${collection.id}")
      deletedCollection <- collectionRepository.deleteCollection(toRepositoryCollection(collection))
      _ <- unlinkCollectionInMoment(collection.moment)
    } yield deletedCollection).resolve[PersistenceServiceException]
  }

  def fetchCollections: EitherT[Task, NineCardException, Seq[Collection]] =
    (for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections
      collectionWithCards <- fetchCards(collectionsWithoutCards)
    } yield collectionWithCards.sortWith(_.position < _.position)).resolve[PersistenceServiceException]

  def fetchCollectionBySharedCollectionId(sharedCollectionId: String) =
    (for {
      collection <- collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId)
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def fetchCollectionsBySharedCollectionIds(sharedCollectionIds: Seq[String]) =
    (for {
      collections <- collectionRepository.fetchCollectionsBySharedCollectionIds(sharedCollectionIds)
    } yield collections map (toCollection(_, cards = Seq.empty, moment = None))).resolve[PersistenceServiceException]

  def fetchCollectionByPosition(position: Int) =
    (for {
      collection <- collectionRepository.fetchCollectionByPosition(position)
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def findCollectionById(collectionId: Int) =
    (for {
      collection <- collectionRepository.findCollectionById(collectionId)
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def findCollectionByCategory(category: String) =
    (for {
      collections <- collectionRepository.fetchCollectionsByCategory(category)
      collection = collections.headOption
      cards <- fetchCards(collection)
      moments <- getMomentsByCollection(collection)
    } yield collection map (toCollection(_, cards, moments.headOption))).resolve[PersistenceServiceException]

  def updateCollection(collection: Collection) =
    (for {
      updated <- collectionRepository.updateCollection(toRepositoryCollection(collection))
    } yield updated).resolve[PersistenceServiceException]

  def updateCollections(collections: Seq[Collection]) =
    (for {
      updated <- collectionRepository.updateCollections(collections map toRepositoryCollection)
    } yield updated).resolve[PersistenceServiceException]

  private[this] def fetchCards(maybeCollection: Option[RepositoryCollection]): TaskService[Seq[RepositoryCard]] = {
    maybeCollection match {
      case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
      case None => TaskService(Task(Right(Seq.empty)))
    }
  }

  private[this] def fetchCards(collections: Seq[RepositoryCollection]): TaskService[Seq[Collection]] = {
    val tasks = collections map {
      collection =>
        (for {
          cards <- cardRepository.fetchCardsByCollection(collection.id)
          moments <- momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
        } yield toCollection(collection, cards, moments.headOption)).value
    }
    TaskService(Task.gatherUnordered(tasks) map { list =>
      Right(list.collect {
        case Right(collection) => collection
      })
    })
  }

  private[this] def unlinkCollectionInMoment(maybeMoment: Option[Moment]): TaskService[Unit] = {
    maybeMoment match {
      case Some(moment) => momentRepository.updateMoment(toRepositoryMomentWithoutCollection(moment)) map (_ => ())
      case None => TaskService(Task(Right((): Unit)))
    }
  }

  private[this] def getMomentsByCollection(maybeCollection: Option[RepositoryCollection]): TaskService[Seq[RepositoryMoment]] = {
    maybeCollection match {
      case Some(collection) => momentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collection.id}")
      case None => TaskService(Task(Right(Seq.empty)))
    }
  }

  private[this] def createOrUpdateMoments(requests: Seq[AddMomentRequest]): TaskService[Unit] = {

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

    val tasks = requests map (r => createOrUpdateMoment(r).value)

    TaskService(Task.gatherUnordered(tasks) map (_ => Right((): Unit)))

  }

}
