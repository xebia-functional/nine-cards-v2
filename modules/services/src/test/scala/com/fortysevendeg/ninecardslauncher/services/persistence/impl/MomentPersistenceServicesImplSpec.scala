package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.MomentEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification



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

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.right(repoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
      }
    }

    "return a Moment with a empty wifi sequence for a valid request with a empty wifi sequence" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.right(createSeqRepoMoment(data = createRepoMomentData(wifiString = ""))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest(wifi = Seq.empty)).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual Seq.empty
      }
    }

    "return a Moment with an empty timeslot sequence for a valid request with an empty timeslot" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.right(createSeqRepoMoment(data = createRepoMomentData(timeslot = "[]"))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(createAddMomentRequest(timeslot = Seq.empty)).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
          moment.timeslot shouldEqual Seq.empty
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addMoment(createAddMomentRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "addMoments" should {

    "return a Seq to Moment for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns TaskService(Task(Either.right(seqRepoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))

      val result = persistenceServices.addMoments(createSeqAddMomentRequest()).value.run
      result must beLike {
        case Right(seqMoment) => seqMoment.size shouldEqual seqRepoMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns TaskService(Task(Either.right(seqRepoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.addMoments(createSeqAddMomentRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns  TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addMoments(createSeqAddMomentRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

  }

  "deleteAllMoments" should {

    "return the number of elements deleted for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoments() returns TaskService(Task(Either.right(items)))
      val result = persistenceServices.deleteAllMoments().value.run
      result shouldEqual Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoments() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllMoments().value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "deleteMoment" should {

    "return the number of elements deleted for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoment(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "fetchMoments" should {

    "return a list of Moment elements for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.right(seqRepoMoment)))
      val result = persistenceServices.fetchMoments.value.run
      result must beLike {
        case Right(momentItems) => momentItems.size shouldEqual seqMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchMoments.value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "findMomentById" should {

    "return a Moment for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Either.right(Option(repoMoment))))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run

      result must beLike {
        case Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.id shouldEqual momentId
          }
      }
    }

    "return None when a non-existent id is given" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = nonExistentMomentId)).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "getMomentByType" should {

    "return a Moment by Type for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType1)) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.getMomentByType(momentType = momentType1).value.run
      result must beLike {
        case Right(moment) => moment.momentType shouldEqual Some(momentType1)
      }
    }

    "return a  PersistenceServiceException if the service return a Seq empty." in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType1)) returns TaskService(Task(Either.right(Seq.empty)))

      val result = persistenceServices.getMomentByType(momentType = momentType1).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType1)) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.getMomentByType(momentType = momentType1).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

  }

  "fetchMomentByType" should {

    "return a Moment by Type for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchMomentByType(momentType = momentType1).value.run
      result must beLike {
        case Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.momentType shouldEqual Some(momentType1)
          }
      }
    }

    "return a None if the service return a Seq empty" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType1)) returns TaskService(Task(Either.right(Seq.empty)))
      val result = persistenceServices.fetchMomentByType(momentType = momentType1).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentType1)) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchMomentByType(momentType = momentType1).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

  }

  "updateMoment" should {

    "return the number of elements updated for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }
}
