package cards.nine.services.persistence.impl

import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.ApplicationTestData
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.models.Application
import cards.nine.models.types.{OrderByCategory, OrderByInstallDate, OrderByName}
import cards.nine.repository.RepositoryException
import cards.nine.repository.provider.AppEntity
import cards.nine.services.persistence.data.AppPersistenceServicesData
import cats.syntax.either._
import monix.eval.Task
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

trait AppPersistenceServicesSpecSpecification
  extends Specification
  with DisjunctionMatchers
  with AppPersistenceServicesData
  with RepositoryServicesScope {

  trait AppPersistenceServicesScope
    extends RepositoryServicesScope
    with ApplicationTestData
    with AppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class AppPersistenceServicesImplSpec extends AppPersistenceServicesSpecSpecification {

  "fetchApps" should {

    "return a sequence of the apps when pass OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByName, ascending = true).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.name))
    }

    "return a sequence of the apps when pass OrderByName and descending order" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByName, ascending = false).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.name).and(contain("DESC")))
    }

    "return a sequence of the apps when pass OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByInstallDate, ascending = true).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.dateInstalled))
    }

    "return a sequence of the apps when pass OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByCategory, ascending = true).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.category))
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchApps(OrderByName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "findAppByPackage" should {

    "return an App when a valid packageName is provided" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(any) returns TaskService(Task(Either.right(Option(repoApp))))
      val result = persistenceServices.findAppByPackage(appPackageName).value.run

      result must beLike {
        case Right(maybeApp) =>
          maybeApp must beSome[Application].which { app =>
            app.id shouldEqual applicationId
            app.packageName shouldEqual appPackageName
          }
      }
    }

    "return None when an invalid packageName is provided" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(any) returns TaskService(Task(Either.right(None)))
      val result = persistenceServices.findAppByPackage(nonExistentApplicationPackageName).value.run
      result shouldEqual Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.findAppByPackage(appPackageName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchAppByPackages" should {

    "return a Seq App when a valid packageName is provided" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackages(any) returns TaskService(Task(Either.right(Seq(repoApp))))
      val result = persistenceServices.fetchAppByPackages(Seq(appPackageName)).value.run

      result must beLike {
        case Right(applicationSeq) =>
            applicationSeq shouldEqual Seq(application)
          }
      }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackages(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchAppByPackages(Seq(appPackageName)).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addApp" should {

    "return a App value for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.addApp(any) returns TaskService(Task(Either.right(repoApp)))
      val result = persistenceServices.addApp(applicationData).value.run

      result must beLike {
        case Right(app) =>
          app.id shouldEqual applicationId
          app.packageName shouldEqual appPackageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.addApp(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addApp(applicationData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "addApps" should {

    "return Unit for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.addApps(any) returns TaskService(Task(Either.right(())))
      val result = persistenceServices.addApps(seqApplicationData).value.run
      result shouldEqual Right((): Unit)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.addApps(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.addApps(seqApplicationData).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAllApps" should {

    "return the number of elements deleted for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps() returns TaskService(Task(Either.right(deletedApplications)))
      val result = persistenceServices.deleteAllApps().value.run
      result shouldEqual Right(deletedApplications)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps() returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAllApps().value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAppsById" should {

    "return the number of elements deleted for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps(any) returns TaskService(Task(Either.right(deletedApplication)))
      val result = persistenceServices.deleteAppsByIds(Seq.empty).value.run
      result shouldEqual Right(deletedApplication)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAppsByIds(Seq.empty).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "deleteAppByPackage" should {

    "return the number of elements deleted for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.deleteAppByPackage(any) returns TaskService(Task(Either.right(deletedApplication)))
      val result = persistenceServices.deleteAppByPackage(applicationPackageName).value.run
      result shouldEqual Right(1)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.deleteAppByPackage(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.deleteAppByPackage(applicationPackageName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "updateApp" should {

    "return the number of elements updated for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.updateApp(any) returns TaskService(Task(Either.right(updatedApplication)))
      val result = persistenceServices.updateApp(application).value.run
      result shouldEqual Right(1)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.updateApp(any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.updateApp(application).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchIterableApps" should {

    "return a iterable of apps when pass OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByName, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByCategory, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchIterableApps(OrderByName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchIterableAppsByKeyword" should {

    "return a iterable of apps when pass a keyword and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = true).value.run
      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchAppsByCategory" should {

    "return a sequence of apps when pass a category and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchAppsByCategory(applicationCategoryStr, OrderByName, ascending = true).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchAppsByCategory(contain(applicationCategoryStr), contain(AppEntity.name))
    }

    "return a sequence of apps when pass a category and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Either.right(seqRepoApp)))
      val result = persistenceServices.fetchAppsByCategory(applicationCategoryStr, OrderByInstallDate, ascending = true).value.run
      result shouldEqual Right(seqApplication)
      there was one(mockAppRepository).fetchAppsByCategory(contain(applicationCategoryStr), contain(AppEntity.dateInstalled))
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchAppsByCategory(applicationCategoryStr, OrderByName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchIterableAppsByCategory" should {

    "return a iterable of apps when pass a category and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByCategory(applicationCategoryStr, OrderByName, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a category and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Either.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByCategory(applicationCategoryStr, OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchIterableAppsByCategory(applicationCategoryStr, OrderByName).value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchAlphabeticalAppsCounter" should {

    "return a sequence of DataCounter sort alphabetically" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.right(dataCounters)))
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run

      result must beLike {
        case Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchCategorizedAppsCounter" should {

    "return a sequence of DataCounter by category sort alphabetically" in new AppPersistenceServicesScope {

      mockAppRepository.fetchCategorizedAppsCounter returns TaskService(Task(Either.right(dataCounters)))
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run
      result must beLike {
        case Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchCategorizedAppsCounter returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }

  "fetchInstallationDateAppsCounter" should {

    "return a sequence of DataCounter by installation date" in new AppPersistenceServicesScope {

      mockAppRepository.fetchInstallationDateAppsCounter returns TaskService(Task(Either.right(dataCounters)))
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run
      result must beLike {
        case Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchInstallationDateAppsCounter returns TaskService(Task(Either.left(exception)))
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run
      result must beAnInstanceOf[Left[RepositoryException, _]]
    }
  }
}