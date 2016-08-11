package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence.data.DockAppPersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification
import rapture.core.{Errata, Answer, Result}

import scalaz.concurrent.Task

trait DockAppPersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with DockAppPersistenceServicesData {

    mockDockAppRepository.addDockApp(repoDockAppData) returns Service(Task(Result.answer(repoDockApp)))

    mockDockAppRepository.deleteDockApps() returns Service(Task(Result.answer(items)))

    mockDockAppRepository.deleteDockApp(repoDockApp) returns Service(Task(Result.answer(item)))

    mockDockAppRepository.fetchDockApps() returns Service(Task(Result.answer(seqRepoDockApp)))

    mockDockAppRepository.fetchIterableDockApps(any, any, any) returns Service(Task(Result.answer(iterableCursorDockApps)))

    mockDockAppRepository.findDockAppById(dockAppId) returns Service(Task(Result.answer(Option(repoDockApp))))

    mockDockAppRepository.findDockAppById(nonExistentDockAppId) returns Service(Task(Result.answer(None)))

    mockDockAppRepository.updateDockApp(repoDockApp) returns Service(Task(Result.answer(item)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with DockAppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockDockAppRepository.addDockApp(repoDockAppData) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.deleteDockApps() returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.deleteDockApp(repoDockApp) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.fetchDockApps() returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.fetchIterableDockApps(any, any, any) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.findDockAppById(dockAppId) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.findDockAppById(nonExistentDockAppId) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.updateDockApp(repoDockApp) returns Service(Task(Result.errata(exception)))

  }

}

class DockAppPersistenceServicesImplSpec extends DockAppPersistenceServicesSpecification {

  "createOrUpdateDockApp" should {

    "return a DockApp value for a valid request adding a dockApp" in new ValidRepositoryServicesResponses {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns Service(Task(Result.answer(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns Service(Task(Result.answer(Seq(repoDockApp))))

      mockDockAppRepository.updateDockApps(any) returns Service(Task(Result.answer(Seq.empty)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Answer(a) =>
          a shouldEqual Seq(dockApp)
      }
    }

    "return a DockApp value for a valid request updating a dockApp" in new ValidRepositoryServicesResponses {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns Service(Task(Result.answer(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns Service(Task(Result.answer(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns Service(Task(Result.answer(Seq(item))))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Answer(a) =>
          a shouldEqual Seq(dockApp)
      }
    }

    "return a PersistenceServiceException if the service throws a exception fetching the dockApps" in new ErrorRepositoryServicesResponses {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})")  returns Service(Task(Result.errata(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }

    "return a PersistenceServiceException if the service throws a exception adding the dockApps" in new ErrorRepositoryServicesResponses {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns Service(Task(Result.answer(Seq.empty)))

      mockDockAppRepository.addDockApps(any) returns Service(Task(Result.errata(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }

    "return a PersistenceServiceException if the service throws a exception updating the dockApps" in new ErrorRepositoryServicesResponses {

      mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns Service(Task(Result.answer(seqRepoDockApp)))

      mockDockAppRepository.addDockApps(any) returns Service(Task(Result.answer(Seq.empty)))

      mockDockAppRepository.updateDockApps(any) returns Service(Task(Result.errata(exception)))

      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllDockApps" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllDockApps().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllDockApps().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteDockApp" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteDockApp(createDeleteDockAppRequest(dockApp = dockApp)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteDockApp(createDeleteDockAppRequest(dockApp = dockApp)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchDockApps" should {

    "return a list of DockApp elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchDockApps.run.run

      result must beLike {
        case Answer(dockAppItems) =>
          dockAppItems.size shouldEqual seqDockApp.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchDockApps.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchIterableDockApps" should {

    "return a iterable of DockApp elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableDockApps.run.run

      result must beLike {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableDockApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableDockApps.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findDockAppById" should {

    "return a DockApp for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = dockAppId)).run.run

      result must beLike {
        case Answer(maybeDockApp) =>
          maybeDockApp must beSome[DockApp].which { dockApp =>
            dockApp.id shouldEqual dockAppId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = nonExistentDockAppId)).run.run

      result must beLike {
        case Answer(maybeDockApp) =>
          maybeDockApp must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findDockAppById(createFindDockAppByIdRequest(id = dockAppId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

}