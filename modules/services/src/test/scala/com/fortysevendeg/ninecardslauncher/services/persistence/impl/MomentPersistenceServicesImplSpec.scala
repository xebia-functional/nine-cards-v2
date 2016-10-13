package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.{MomentData, Moment}
import cards.nine.repository.RepositoryException
import cards.nine.repository.provider.MomentEntity
import cards.nine.services.persistence.data.PersistenceServicesData
import cats.syntax.either._
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
      val result = persistenceServices.addMoment(momentData).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
      }
    }

    "return a Moment with a empty wifi sequence for a valid request with a empty wifi sequence" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.right(createSeqRepoMoment(data = createRepoMomentData(wifiString = ""))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(momentData.copy(wifi = Seq.empty)).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual Seq.empty
      }
    }

    "return a Moment with an empty timeslot sequence for a valid request with an empty timeslot" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.right(createSeqRepoMoment(data = createRepoMomentData(timeslot = "[]"))(0))))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))
      val result = persistenceServices.addMoment(momentData.copy(timeslot = Seq.empty)).value.run

      result must beLike {
        case Right(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
          moment.timeslot shouldEqual Seq.empty
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addMoment(momentData).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "addMoments" should {

    "return a Seq to Moment for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns TaskService(Task(Either.right(seqRepoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.right(seqRepoWidget)))

      val result = persistenceServices.addMoments(seqMomentData).value.run
      result must beLike {
        case Right(seqMoment) => seqMoment.size shouldEqual seqRepoMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns TaskService(Task(Either.right(seqRepoMoment)))
      mockWidgetRepository.addWidgets(any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.addMoments(seqMomentData).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.addMoments(any) returns  TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addMoments(seqMomentData).value.run
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
      val result = persistenceServices.deleteMoment(moment).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.deleteMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteMoment(moment).value.run
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
      val result = persistenceServices.findMomentById( momentId).value.run

      result must beLike {
        case Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.id shouldEqual momentId
          }
      }
    }

    "return None when a non-existent id is given" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findMomentById(nonExistentMomentId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.findMomentById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findMomentById(momentId).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }

  "getMomentByType" should {

    "return a Moment by Type for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentTypeStr)) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.getMomentByType(momentType).value.run
      result must beLike {
        case Right(moment) => moment.momentType shouldEqual Some(momentType)
      }
    }

    "return a  PersistenceServiceException if the service return a Seq empty." in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentTypeStr)) returns TaskService(Task(Either.right(Seq.empty)))

      val result = persistenceServices.getMomentByType(momentType).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentTypeStr)) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.getMomentByType(momentType).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

  }

  "fetchMomentByType" should {

    "return a Moment by Type for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(any, any, any) returns TaskService(Task(Either.right(seqRepoMoment)))

      val result = persistenceServices.fetchMomentByType(momentType = momentTypeStr).value.run
      result must beLike {
        case Right(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.momentType shouldEqual Some(momentType)
          }
      }
    }

    "return a None if the service return a Seq empty" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentTypeStr)) returns TaskService(Task(Either.right(Seq.empty)))
      val result = persistenceServices.fetchMomentByType(momentType = momentTypeStr).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.fetchMoments(s"${MomentEntity.momentType} = ?", Seq(momentTypeStr)) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchMomentByType(momentType = momentTypeStr).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }

  }

  "updateMoment" should {

    "return the number of elements updated for a valid request" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.right(item)))
      val result = persistenceServices.updateMoment(moment).value.run
      result shouldEqual Right(item)
    }

    "return a PersistenceServiceException if the service throws a exception" in new MomentPersistenceServicesScope {

      mockMomentRepository.updateMoment(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateMoment(moment).value.run
      result must beAnInstanceOf[Left[RepositoryException,  _]]
    }
  }
}
