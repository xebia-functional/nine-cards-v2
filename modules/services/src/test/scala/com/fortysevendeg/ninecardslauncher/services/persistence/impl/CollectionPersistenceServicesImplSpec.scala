package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.CollectionTestData
import cards.nine.models.Collection
import cards.nine.repository.RepositoryException
import cards.nine.repository.provider.CardEntity
import cards.nine.services.persistence.data.CollectionPersistenceServicesData
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.services.persistence.data.{CardPersistenceServicesData, MomentPersistenceServicesData}
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.MomentValues._

trait CollectionPersistenceServicesDataSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CollectionServicesResponses
    extends RepositoryServicesScope
    with CollectionTestData
    with CollectionPersistenceServicesData
    with CardPersistenceServicesData
    with MomentPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class CollectionPersistenceServicesImplSpec extends CollectionPersistenceServicesDataSpecification {

  "addCollection" should {

    "return a Collection value for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.addCollection(any) returns TaskService(Task(Either.right(repoCollection)))
      mockCardRepository.addCards(any) returns TaskService(Task(Either.right(Seq(repoCard))))
      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.right(seqRepoMoment)))
      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.right(updatedMoment)))

      val result = persistenceServices.addCollection(collectionData).value.run

      result must beLike {
        case Right(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.addCollection(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCollection(collectionData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addCollections" should {

    "return a Seq Collection value for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.addCollections(any) returns TaskService(Task(Either.right(seqRepoCollection)))
      mockCardRepository.addCards(any) returns TaskService(Task(Either.right(Seq(repoCard))))
      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.right(seqRepoMoment)))
      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.right(updatedMoment)))

      val result = persistenceServices.addCollections(seqCollectionData).value.run
      result must beLike {
        case Right(collections) => collections.size shouldEqual seqCollectionData.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.addCollections(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addCollections(seqCollectionData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }

  }

  "deleteAllCollections" should {

    "return the number of elements deleted for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollections() returns TaskService(Task(Either.right(deletedCollections)))
      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(deletedCards)))

      val result = persistenceServices.deleteAllCollections().value.run
      result shouldEqual Right(deletedCollections)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollections() returns TaskService(Task(Either.left(exception)))
      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.deleteAllCollections().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteCollection" should {

    "return the number of elements deleted for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.right(deletedCollection)))
      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(deletedCards)))
      mockMomentRepository.updateMoment(repoMoment.copy(data = repoMoment.data.copy(collectionId = None))) returns TaskService(Task(Either.right(updatedMoment)))

      val result = persistenceServices.deleteCollection(collection).value.run
      result shouldEqual Right(deletedCollection)
      there was one(mockCardRepository).deleteCards(None, where = s"${CardEntity.collectionId} = $collectionId")
    }

    "return Right of Unit if Moment in Request is None" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.right(deletedCollection)))
      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.right(deletedCards)))
      mockMomentRepository.updateMoment(repoMoment.copy(data = repoMoment.data.copy(collectionId = None))) returns TaskService(Task(Either.right(updatedMoment)))

      val result = persistenceServices.deleteCollection(collection.copy(moment = None)).value.run
      result shouldEqual Right(deletedCollection)
      there was one(mockCardRepository).deleteCards(None, where = s"${CardEntity.collectionId} = $collectionId")
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.deleteCollection(any) returns TaskService(Task(Either.left(exception)))
      mockCardRepository.deleteCards(any, any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.deleteCollection(collection).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
      there was one(mockCardRepository).deleteCards(None, where = s"${CardEntity.collectionId} = $collectionId")
    }
  }

  "fetchCollectionByPosition" should {

    "return a Collection for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(Option(repoCollection))))
      List.tabulate(5) { index =>
        mockCardRepository.fetchCardsByCollection(collectionId + index) returns TaskService(Task(Either.right(seqRepoCard)))
      }
      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchCollectionByPosition(collectionPosition).value.run

      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.position shouldEqual collectionPosition
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.fetchCollectionByPosition(nonExistentPosition).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionByPosition(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCollectionByPosition(collectionPosition).value.run
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

      val result = persistenceServices.findCollectionById(collectionId).value.run

      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
          }
      }
    }

    "return None when a non-existent id is given" in new CollectionServicesResponses {

      mockCollectionRepository.findCollectionById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findCollectionById(nonExistentCollectionId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.findCollectionById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCollectionById(collectionId).value.run
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

      val result = persistenceServices.findCollectionByCategory(appsCategoryStr).value.run
      result must beLike {
        case Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.appsCategory map (_.name) shouldEqual Some(appsCategoryStr)
          }
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsByCategory(any) returns TaskService(Task(Either.right(Seq.empty)))
      val result = persistenceServices.findCollectionByCategory(appsCategoryStr).value.run
      result shouldEqual Right(None)
    }


    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.fetchCollectionsByCategory(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findCollectionByCategory(appsCategoryStr).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateCollection" should {

    "return the number of elements updated for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollection(any) returns TaskService(Task(Either.right(updatedCollection)))
      val result = persistenceServices.updateCollection(collection).value.run
      result shouldEqual Right(updatedCollection)
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollection(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCollection(collection).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateCollections" should {

    "return the number of elements updated for a valid request" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollections(any) returns TaskService(Task(Either.right(Seq(updatedCollections))))
      val result = persistenceServices.updateCollections(seqCollection).value.run
      result shouldEqual Right(Seq(updatedCollection))
    }

    "return a PersistenceServiceException if the service throws a exception" in new CollectionServicesResponses {

      mockCollectionRepository.updateCollections(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateCollections(seqCollection).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }
}
