package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.{CardEntity, MomentEntity}
import com.fortysevendeg.ninecardslauncher.services.persistence.FetchCollectionBySharedCollectionRequest
import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scalaz.concurrent.Task


trait CollectionPersistenceServicesDataSpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Xor.right(repoCard)))

    mockCardRepository.addCards(any) returns TaskService(Task(Xor.right(Seq(repoCard))))

    mockCardRepository.deleteCards() returns TaskService(Task(Xor.right(items)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.right(items)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(collectionId, repoCard) returns TaskService(Task(Xor.right(item)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Xor.right(seqRepoCard)))
    }

    mockMomentRepository.addMoments(any) returns TaskService(Task(Xor.right(Seq(repoMoment))))

    mockMomentRepository.updateMoment(repoMoment.copy(data = repoMoment.data.copy(collectionId = None))) returns TaskService(Task(Xor.right(item)))

    mockMomentRepository.updateMoment(repoMoment) returns TaskService(Task(Xor.right(item)))

    mockMomentRepository.deleteMoment(repoMoment) returns TaskService(Task(Xor.right(item)))

    mockMomentRepository.fetchMoments() returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 1}") returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 2}") returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 3}") returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 4}") returns TaskService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns TaskService(Task(Xor.right(Seq.empty)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns TaskService(Task(Xor.right(Seq.empty)))

    mockCollectionRepository.addCollection(repoCollectionData) returns TaskService(Task(Xor.right(repoCollection)))

    mockCollectionRepository.deleteCollections() returns TaskService(Task(Xor.right(items)))

    mockCollectionRepository.deleteCollection(repoCollection) returns TaskService(Task(Xor.right(item)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns TaskService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionByPosition(nonExistentPosition) returns TaskService(Task(Xor.right(None)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns TaskService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(nonExistentSharedCollectionId) returns TaskService(Task(Xor.right(None)))

    mockCollectionRepository.fetchCollectionByOriginalSharedCollectionId(sharedCollectionId) returns TaskService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionByOriginalSharedCollectionId(nonExistentSharedCollectionId) returns TaskService(Task(Xor.right(None)))

    mockCollectionRepository.fetchSortedCollections returns TaskService(Task(Xor.right(seqRepoCollection)))

    mockCollectionRepository.findCollectionById(collectionId) returns TaskService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.findCollectionById(nonExistentCollectionId) returns TaskService(Task(Xor.right(None)))

    mockCollectionRepository.updateCollection(repoCollection) returns TaskService(Task(Xor.right(item)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockCardRepository.addCard(collectionId, repoCardData) returns TaskService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards() returns TaskService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.left(exception)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(collectionId, repoCard) returns TaskService(Task(Xor.left(exception)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Xor.left(exception)))
    }

    mockMomentRepository.fetchMoments() returns TaskService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns TaskService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns TaskService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.addCollection(repoCollectionData) returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.deleteCollections() returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.deleteCollection(repoCollection) returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchSortedCollections returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.findCollectionById(collectionId) returns TaskService(Task(Xor.left(exception)))

    mockCollectionRepository.updateCollection(repoCollection) returns TaskService(Task(Xor.left(exception)))

  }

}

class CollectionPersistenceServicesImplSpec extends CollectionPersistenceServicesDataSpecification {

  "addCollection" should {

    "return a Collection value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).value.run

      result must beLike {
        case Xor.Right(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllCollections" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollectionByPosition" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.position shouldEqual position
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(nonExistentPosition)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) => maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollectionBySharedCollection" should {

    "return a Collection for a valid request and original to true" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(
        FetchCollectionBySharedCollectionRequest(sharedCollectionId, original = true)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return a Collection for a valid request and original to false" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(
        FetchCollectionBySharedCollectionRequest(sharedCollectionId, original = false)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return None when a non-existent id and original to true is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(
        FetchCollectionBySharedCollectionRequest(nonExistentSharedCollectionId, original = true)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return None when a non-existent id and original to false is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(
        FetchCollectionBySharedCollectionRequest(nonExistentSharedCollectionId, original = false)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(
        FetchCollectionBySharedCollectionRequest(sharedCollectionId, original = false)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollections" should {

    "return a list of Collection elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.value.run

      result must beLike {
        case Xor.Right(collections) => collections.size shouldEqual seqCollection.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCollectionById" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = nonExistentCollectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) => maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCollection" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run

      result must beLike {
        case Xor.Right(updated) => updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }
}