package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
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

  trait MomentPersistenceServicesScope
    extends RepositoryServicesScope
      with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class MomentPersistenceServicesImplSpec extends MomentPersistenceServicesSpecification {

  "addMoment" should {

    "return a Moment for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Xor.right(repoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
      }
    }

    "return a Moment with a empty wifi sequence for a valid request with a empty wifi sequence" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Xor.right(createSeqRepoMoment(data = createRepoMomentData(wifiString = ""))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest(wifi = Seq.empty)).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual Seq.empty
      }
    }

    "return a Moment with an empty timeslot sequence for a valid request with an empty timeslot" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Xor.right(createSeqRepoMoment(data = createRepoMomentData(timeslot = "[]"))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Xor.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest(timeslot = Seq.empty)).value.run

      result must beLike {
        case Xor.Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
          moment.timeslot shouldEqual Seq.empty
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAllMoments" should {

    "return the number of elements deleted for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoments() returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteAllMoments().value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoments() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAllMoments().value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteMoment" should {

    "return the number of elements deleted for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoment(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoment(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchMoments" should {

    "return a list of Moment elements for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments() returns TaskService(Task(Xor.right(seqRepoMoment)))
      val result = persistenceServices.fetchMoments.value.run
      result must beLike {
        case Xor.Right(momentItems) => momentItems.size shouldEqual seqMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchMoments.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "findMomentById" should {

    "return a Moment for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Xor.right(Option(repoMoment))))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run

      result must beLike {
        case Xor.Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.id shouldEqual momentId
          }
      }
    }

    "return None when a non-existent id is given" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Xor.right(None)))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = nonExistentMomentId)).value.run
      result shouldEqual Xor.Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "updateMoment" should {

    "return the number of elements updated for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run
      result shouldEqual Xor.Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }
}
