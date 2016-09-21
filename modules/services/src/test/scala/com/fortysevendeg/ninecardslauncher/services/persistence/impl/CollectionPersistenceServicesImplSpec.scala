package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.CardEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import cats.syntax.either._


trait CollectionPersistenceServicesDataSpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait CollectionServicesResponses
    extends RepositoryServicesScope
      with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class CollectionPersistenceServicesImplSpec extends CollectionPersistenceServicesDataSpecification {

  "addCollection" should {

    "return a Collection value for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.addCollection(any) returns TaskService(Task(Either.right(repoCollection)))
      mockCardRepository.addCards(any) returns TaskService(Task(Either.right(Seq(repoCard))))
      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.right(seqRepoMoment)))
      mockMomentRepository.updateMoment(repoMoment) returns TaskService(Task(Either.right(item)))

      val result = persistenceServices.addCollection(addCollectionRequest).value.run

      result must beLike {
        case Right(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.addCollection(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCollection(addCollectionRequest).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addCollections" should {

    "return a Seq Collection value for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.addCollections(any) returns TaskService(Task(Either.right(seqRepoCollection)))
      mockCardRepository.addCards(any) returns TaskService(Task(Either.right(Seq(repoCard))))
      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.right(seqRepoMoment)))
      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.right(item)))

      val result = persistenceServices.addCollections(seqAddCollectionRequest).value.run
      result must beLike {
        case Right(collections) => collections.size shouldEqual seqAddCollectionRequest.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.addCollections(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCollections(seqAddCollectionRequest).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }

  }

  "deleteAllCollections" should {

    "return the number of elements deleted for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollections() returns TaskService(Task(Either.right(items)))
      mockCardRepository.deleteCards() returns TaskService(Task(Either.right(items)))

      val result = persistenceServices.deleteAllCollections().value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollections() returns TaskService(Task(Either.left(exception)))
      mockCardRepository.deleteCards() returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.deleteAllCollections().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCollection" should {

    "return the number of elements deleted for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.right(item)))
      mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Either.right(items)))
      mockMomentRepository.updateMoment(repoMoment.copy(data = repoMoment.data.copy(collectionId = None))) returns TaskService(Task(Either.right(item)))

      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run
      result shouldEqual Right(item)
    }

    "return Right of Unit if Moment in Request is None" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.right(item)))
      mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Either.right(items)))
      mockMomentRepository.updateMoment(repoMoment.copy(data = repoMoment.data.copy(collectionId = None))) returns TaskService(Task(Either.right(item)))

      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collectionWithoutMoment)).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.left(exception)))
      mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCollectionByPosition" should {

    "return a Collection for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(Option(repoCollection))))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run

      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.position shouldEqual position
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(nonExistentPosition)).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCollectionBySharedCollectionId" should {

    "return a Collection for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionBySharedCollectionId(any) returns TaskService(Task(Either.right(Option(repoCollection))))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId).value.run

      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionBySharedCollectionId(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.fetchCollectionBySharedCollectionId(nonExistentSharedCollectionId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionBySharedCollectionId(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCollectionsBySharedCollectionIds" should {

    "return a sequence of collections for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsBySharedCollectionIds(any) returns TaskService(Task(Either.right(Seq(repoCollection))))

      val result = persistenceServices.fetchCollectionsBySharedCollectionIds(Seq(sharedCollectionId)).value.run

      result must beLike {
        case Right(collections) =>
          collections.headOption must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsBySharedCollectionIds(any) returns TaskService(Task(Either.right(Seq.empty)))
      val result = persistenceServices.fetchCollectionsBySharedCollectionIds(Seq(nonExistentSharedCollectionId)).value.run
      result shouldEqual Right(Seq.empty)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsBySharedCollectionIds(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCollectionsBySharedCollectionIds(Seq(sharedCollectionId)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCollections" should {

    "return a list of Collection elements for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchSortedCollections returns TaskService(Task(Either.right(seqRepoCollection)))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchCollections.value.run

      result must beLike {
        case Right(collections) => collections.size shouldEqual seqCollection.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchSortedCollections returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCollections.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findCollectionById" should {

    "return a Collection for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.findCollectionById(any) returns TaskService(Task(Either.right(Option(repoCollection))))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run

      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.findCollectionById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = nonExistentCollectionId)).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.findCollectionById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findCollectionByCategory" should {

    "return a Collection for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsByCategory(any) returns TaskService(Task(Either.right(seqRepoCollection)))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.findCollectionByCategory(appsCategory).value.run
      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.appsCategory shouldEqual Some(appsCategory)
          }
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsByCategory(any) returns TaskService(Task(Either.right(Seq.empty)))
      val result = persistenceServices.findCollectionByCategory(appsCategory).value.run
      result shouldEqual Right(None)
    }


    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsByCategory(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCollectionByCategory(appsCategory).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateCollection" should {

    "return the number of elements updated for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollection(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollection(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateCollections" should {

    "return the number of elements updated for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollections(any) returns TaskService(Task(Either.right(Seq(item))))
      val result = persistenceServices.updateCollections(updateCollectionsRequest).value.run
      result shouldEqual Right(Seq(item))
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollections(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCollections(updateCollectionsRequest).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }
}
