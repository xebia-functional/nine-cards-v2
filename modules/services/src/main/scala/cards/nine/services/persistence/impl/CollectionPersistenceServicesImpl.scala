package cards.nine.services.persistence.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{Collection, CollectionData, Moment, MomentData}
import cards.nine.repository.model.{Card => RepositoryCard, Collection => RepositoryCollection}
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
      addedCards      <- addCards(Seq((collectionAdded.id, collection.cards)))
      _ <- createOrUpdateMoments(
        Seq(collection.moment map (_.copy(collectionId = Option(collectionAdded.id)))).flatten)
    } yield toCollection(collectionAdded).copy(cards = addedCards))
      .resolve[PersistenceServiceException]

  def addCollections(collections: Seq[CollectionData]) = {
    val collectionsData = collections map toRepositoryCollectionData
    val cardsData       = collections map (_.cards)
    val momentsData     = collections map (_.moment)
    (for {
      collections <- collectionRepository.addCollections(collectionsData)
      cards = collections.zip(cardsData) map {
        case (collection, cardsRequest) => (collection.id, cardsRequest)
      }
      addedCards <- addCards(cards)
      moments = collections.zip(momentsData) flatMap {
        case (collection, momentRequest) =>
          momentRequest map (_.copy(collectionId = Option(collection.id)))
      }
      _ <- createOrUpdateMoments(moments)
    } yield collections map toCollection).resolve[PersistenceServiceException]
  }

  def deleteAllCollections() =
    (for {
      deleted <- collectionRepository.deleteCollections()
    } yield deleted).resolve[PersistenceServiceException]

  def deleteCollection(collection: Collection) = {

    def unlinkCollectionInMoment(maybeMoment: Option[Moment]): TaskService[Unit] = {
      maybeMoment match {
        case Some(moment) =>
          momentRepository.updateMoment(toRepositoryMomentWithoutCollection(moment)) map (_ => ())
        case None => TaskService(Task(Right((): Unit)))
      }
    }

    (for {
      _ <- cardRepository.deleteCards(where = s"${CardEntity.collectionId} = ${collection.id}")
      deletedCollection <- collectionRepository.deleteCollection(
        toRepositoryCollection(collection))
      _ <- unlinkCollectionInMoment(collection.moment)
    } yield deletedCollection).resolve[PersistenceServiceException]
  }

  def fetchCollections: EitherT[Task, NineCardException, Seq[Collection]] = {

    def populateCollections(collections: Seq[RepositoryCollection]): TaskService[Seq[Collection]] = {
      val tasks = collections map (collection =>
                                     populateCollection(TaskService.right(Option(collection))).value)
      TaskService(Task.gatherUnordered(tasks) map { list =>
        Right(list.collect {
          case Right(Some(collection)) => collection
        })
      })
    }

    (for {
      collectionsWithoutCards <- collectionRepository.fetchSortedCollections
      collectionWithCards     <- populateCollections(collectionsWithoutCards)
    } yield collectionWithCards.sortWith(_.position < _.position))
      .resolve[PersistenceServiceException]
  }

  def fetchCollectionBySharedCollectionId(sharedCollectionId: String) =
    populateCollection(
      collectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId))

  def fetchCollectionsBySharedCollectionIds(sharedCollectionIds: Seq[String]) =
    (for {
      collections <- collectionRepository.fetchCollectionsBySharedCollectionIds(
        sharedCollectionIds)
    } yield collections map (toCollection(_, cards = Seq.empty)))
      .resolve[PersistenceServiceException]

  def fetchCollectionByPosition(position: Int) =
    populateCollection(collectionRepository.fetchCollectionByPosition(position))

  def findCollectionById(collectionId: Int) =
    populateCollection(collectionRepository.findCollectionById(collectionId))

  def findCollectionByCategory(category: String) =
    populateCollection(collectionRepository.fetchCollectionsByCategory(category).map(_.headOption))

  def updateCollection(collection: Collection) =
    (for {
      updated <- collectionRepository.updateCollection(toRepositoryCollection(collection))
    } yield updated).resolve[PersistenceServiceException]

  def updateCollections(collections: Seq[Collection]) =
    (for {
      updated <- collectionRepository.updateCollections(collections map toRepositoryCollection)
    } yield updated).resolve[PersistenceServiceException]

  private[this] def populateCollection(
      service: TaskService[Option[RepositoryCollection]]): TaskService[Option[Collection]] = {

    def getMomentsByCollection(
        maybeCollection: Option[RepositoryCollection]): TaskService[Seq[Moment]] = {
      maybeCollection match {
        case Some(collection) =>
          for {
            moments <- momentRepository.fetchMoments(
              where = s"${MomentEntity.collectionId} = ${collection.id}")
          } yield moments map toMoment

        case None => TaskService(Task(Right(Seq.empty)))
      }
    }

    def fetchCards(
        maybeCollection: Option[RepositoryCollection]): TaskService[Seq[RepositoryCard]] = {
      maybeCollection match {
        case Some(collection) => cardRepository.fetchCardsByCollection(collection.id)
        case None             => TaskService(Task(Right(Seq.empty)))
      }
    }

    (for {
      maybeCollection <- service
      cards           <- fetchCards(maybeCollection)
      moments         <- getMomentsByCollection(maybeCollection)
    } yield maybeCollection map (toCollection(_, cards).copy(moment = moments.headOption)))
      .resolve[PersistenceServiceException]
  }

  private[this] def createOrUpdateMoments(moments: Seq[MomentData]): TaskService[Unit] = {

    def createOrUpdateMoment(momentData: MomentData) = {

      def createOrUpdate(maybeMoment: Option[Moment]) = maybeMoment match {
        case Some(moment) =>
          updateMoment(
            moment.copy(
              id = moment.id,
              collectionId = momentData.collectionId,
              timeslot = moment.timeslot,
              wifi = moment.wifi,
              headphone = moment.headphone,
              momentType = moment.momentType
            ))
        case None => addMoment(momentData)
      }

      for {
        moments <- fetchMoments
        _       <- createOrUpdate(moments find (_.momentType == momentData.momentType))
      } yield ()
    }

    val tasks = moments map (r => createOrUpdateMoment(r).value)

    TaskService(Task.gatherUnordered(tasks) map (_ => Right((): Unit)))

  }

}
