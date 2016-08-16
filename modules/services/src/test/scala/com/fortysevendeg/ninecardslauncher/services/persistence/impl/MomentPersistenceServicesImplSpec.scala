package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito

import scalaz.concurrent.Task


trait MomentPersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    mockMomentRepository.addMoment(repoMomentData) returns CatsService(Task(Xor.right(repoMoment)))

    mockMomentRepository.addMoment(createRepoMomentData(wifiString = "")) returns CatsService(Task(Xor.right(createSeqRepoMoment(data = createRepoMomentData(wifiString = ""))(0))))

    mockMomentRepository.addMoment(createRepoMomentData(timeslot = "[]")) returns CatsService(Task(Xor.right(createSeqRepoMoment(data = createRepoMomentData(timeslot = "[]"))(0))))

    mockMomentRepository.addMoments(any) returns CatsService(Task(Xor.right(Seq(repoMoment))))

    mockMomentRepository.deleteMoments() returns CatsService(Task(Xor.right(items)))

    mockMomentRepository.deleteMoment(repoMoment) returns CatsService(Task(Xor.right(item)))

    mockMomentRepository.fetchMoments() returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 1}") returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 2}") returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 3}") returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 4}") returns CatsService(Task(Xor.right(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns CatsService(Task(Xor.right(Seq.empty)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns CatsService(Task(Xor.right(Seq.empty)))

    mockMomentRepository.findMomentById(momentId) returns CatsService(Task(Xor.right(Option(repoMoment))))

    mockMomentRepository.findMomentById(nonExistentMomentId) returns CatsService(Task(Xor.right(None)))

    mockMomentRepository.updateMoment(repoMoment) returns CatsService(Task(Xor.right(item)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockMomentRepository.addMoment(repoMomentData) returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.deleteMoments() returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.deleteMoment(repoMoment) returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments() returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.findMomentById(momentId) returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.findMomentById(nonExistentMomentId) returns CatsService(Task(Xor.left(exception)))

    mockMomentRepository.updateMoment(repoMoment) returns CatsService(Task(Xor.left(exception)))

  }

}

class MomentPersistenceServicesImplSpec extends MomentPersistenceServicesSpecification {

  "addMoment" should {

    "return a Moment for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
      }
    }

    "return a Moment with a empty wifi sequence for a valid request with a empty wifi sequence" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest(wifi = Seq.empty)).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual Seq.empty
      }
    }

    "return a Moment with an empty timeslot sequence for a valid request with an empty timeslot" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest(timeslot = Seq.empty)).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
          moment.timeslot shouldEqual Seq.empty
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllMoments" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllMoments().value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllMoments().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteMoment" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchMoments" should {

    "return a list of Moment elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchMoments.value.run

      result must beLike {
        case Xor.Right(momentItems) =>
          momentItems.size shouldEqual seqMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchMoments.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findMomentById" should {

    "return a Moment for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run

      result must beLike {
        case Xor.Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.id shouldEqual momentId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = nonExistentMomentId)).value.run

      result must beLike {
        case Xor.Right(maybeMoment) =>
          maybeMoment must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateMoment" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }
}
