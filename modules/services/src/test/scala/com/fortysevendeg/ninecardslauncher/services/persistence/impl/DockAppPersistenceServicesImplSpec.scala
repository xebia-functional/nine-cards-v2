package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.DockAppPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

import scalaz.concurrent.Task

trait DockAppPersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers {

  trait DockAppPersistenceServices
    extends RepositoryServicesScope
      with DockAppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class DockAppPersistenceServicesImplSpec extends DockAppPersistenceServicesSpecification {

  "createOrUpdateDockApp" should {

    "return a DockApp value for a valid request adding a dockApp" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns TaskService(Task(Xor.right(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Xor.right(Seq(repoDockApp))))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Xor.right(Seq.empty)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).value.run
      result shouldEqual Xor.Right(Seq(dockApp))
    }

    "return a DockApp value for a valid request updating a dockApp" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns TaskService(Task(Xor.right(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Xor.right(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Xor.right(Seq(item))))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).value.run
      result shouldEqual Xor.Right(Seq(dockApp))

    }

    "return a PersistenceServiceException if the service throws a exception fetching the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns TaskService(Task(Xor.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }

    "return a PersistenceServiceException if the service throws a exception adding the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns TaskService(Task(Xor.right(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Xor.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }

    "return a PersistenceServiceException if the service throws a exception updating the dockApps" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns TaskService(Task(Xor.right(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns TaskService(Task(Xor.right(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns TaskService(Task(Xor.left(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAllDockApps" should {

    "return the number of elements deleted for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApps() returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteAllDockApps().value.run

      result must beLike {
        case Xor.Right(deleted) => deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApps() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAllDockApps().value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteDockApp" should {

    "return the number of elements deleted for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApp(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.deleteDockApp(createDeleteDockAppRequest(dockApp = dockApp)).value.run
      result shouldEqual Xor.Right(item)

    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.deleteDockApp(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteDockApp(createDeleteDockAppRequest(dockApp = dockApp)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchDockApps" should {

    "return a list of DockApp elements for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps() returns TaskService(Task(Xor.right(seqRepoDockApp)))
      val result = persistenceServices.fetchDockApps.value.run

      result must beLike {
        case Xor.Right(dockAppItems) =>
          dockAppItems.size shouldEqual seqDockApp.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchDockApps() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchDockApps.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchIterableDockApps" should {

    "return a iterable of DockApp elements for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchIterableDockApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorDockApps)))
      val result = persistenceServices.fetchIterableDockApps.value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableDockApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.fetchIterableDockApps(any, any, any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchIterableDockApps.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "findDockAppById" should {

    "return a DockApp for a valid request" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Xor.right(Option(repoDockApp))))
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = dockAppId)).value.run

      result must beLike {
        case Xor.Right(maybeDockApp) =>
          maybeDockApp must beSome[DockApp].which { dockApp =>
            dockApp.id shouldEqual dockAppId
          }
      }
    }

    "return None when a non-existent id is given" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Xor.right(None)))
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = nonExistentDockAppId)).value.run
      result shouldEqual Xor.Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new DockAppPersistenceServices {

      mockDockAppRepository.findDockAppById(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = dockAppId)).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

}