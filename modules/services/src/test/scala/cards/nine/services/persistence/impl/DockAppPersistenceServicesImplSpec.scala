package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.DockAppTestData
import cards.nine.commons.test.data.DockAppValues._
import cards.nine.models.DockApp
import cards.nine.repository.RepositoryException
import cards.nine.services.persistence.data.DockAppPersistenceServicesData
import cats.syntax.either._
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

trait DockAppPersistenceServicesSpecification
  extends Specification
  with DisjunctionMatchers {

  trait DockAppPersistenceServices
    extends RepositoryServicesScope
    with DockAppTestData
    with DockAppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class DockAppPersistenceServicesImplSpec extends DockAppPersistenceServicesSpecification {

  "createOrUpdateDockApp" should {

    "return a DockApp value for a valid request adding a dockApp" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(any, any, any) returns TaskService(Task(Either.right(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Either.right(Seq(repoDockApp))))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Either.right(Seq.empty)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(dockAppData)).value.run
      result shouldEqual Right(Seq(dockApp))
    }

    "return a DockApp value for a valid request updating a dockApp" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(any, any, any) returns TaskService(Task(Either.right(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Either.right(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Either.right(Seq(deletedDockApp))))

      val result = persistenceServices.createOrUpdateDockApp(Seq(dockAppData)).value.run
      result shouldEqual Right(Seq(dockApp))

    }

    "return a PersistenceServiceException if the service throws a exception fetching the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(any, any, any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(seqDockAppData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }

    "return a PersistenceServiceException if the service throws a exception adding the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(any, any, any) returns TaskService(Task(Either.right(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(seqDockAppData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }

    "return a PersistenceServiceException if the service throws a exception updating the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(any, any, any) returns TaskService(Task(Either.right(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Either.right(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Either.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(seqDockAppData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllDockApps" should {

    "return the number of elements deleted for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApps() returns TaskService(Task(Either.right(deletedDockApps)))
      val result = persistenceServices.deleteAllDockApps().value.run

      result must beLike {
        case Right(deleted) => deleted shouldEqual deletedDockApps
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApps() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllDockApps().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteDockApp" should {

    "return the number of elements deleted for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApp(any) returns TaskService(Task(Either.right(deletedDockApp)))
      val result = persistenceServices.deleteDockApp(dockApp).value.run
      result shouldEqual Right(deletedDockApp)

    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApp(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteDockApp(dockApp).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchDockApps" should {

    "return a list of DockApp elements for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps() returns TaskService(Task(Either.right(seqRepoDockApp)))
      val result = persistenceServices.fetchDockApps.value.run

      result must beLike {
        case Right(dockAppItems) =>
          dockAppItems.size shouldEqual seqDockApp.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchDockApps.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findDockAppById" should {

    "return a DockApp for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Either.right(Option(repoDockApp))))
      val result = persistenceServices.findDockAppById(dockAppId).value.run

      result must beLike {
        case Right(maybeDockApp) =>
          maybeDockApp must beSome[DockApp].which { dockApp =>
            dockApp.id shouldEqual dockAppId
          }
      }
    }

    "return None when a non-existent id is given" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findDockAppById(nonExistentDockAppId).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findDockAppById(dockAppId).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

}