package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.{OrderByCategory, OrderByInstallDate, OrderByName}
import com.fortysevendeg.ninecardslauncher.services.persistence.data.AppPersistenceServicesData
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mutable.Specification

import scalaz.concurrent.Task

trait AppPersistenceServicesSpecSpecification
  extends Specification
    with DisjunctionMatchers
    with AppPersistenceServicesData
    with RepositoryServicesScope {

  trait AppPersistenceServicesScope
    extends RepositoryServicesScope
      with AppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

  }

}

class AppPersistenceServicesImplSpec extends AppPersistenceServicesSpecSpecification {

  "fetchApps" should {

    "return a sequence of the apps when pass OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByName, ascending = true).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.name))
    }

    "return a sequence of the apps when pass OrderByName and descending order" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByName, ascending = false).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.name).and(contain("DESC")))
    }

    "return a sequence of the apps when pass OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByInstallDate, ascending = true).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.dateInstalled))
    }

    "return a sequence of the apps when pass OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchApps(OrderByCategory, ascending = true).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchApps(contain(AppEntity.category))
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchApps(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchApps(OrderByName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "findAppByPackage" should {

    "return an App when a valid packageName is provided" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(packageName) returns TaskService(Task(Xor.right(Option(repoApp))))
      val result = persistenceServices.findAppByPackage(packageName).value.run

      result must beLike {
        case Xor.Right(maybeApp) =>
          maybeApp must beSome[App].which { app =>
            app.id shouldEqual appId
            app.packageName shouldEqual packageName
          }
      }
    }

    "return None when an invalid packageName is provided" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(any) returns TaskService(Task(Xor.right(None)))
      val result = persistenceServices.findAppByPackage(nonExistentPackageName).value.run
      result shouldEqual Xor.Right(None)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppByPackage(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.findAppByPackage(packageName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "addApp" should {

    "return a App value for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.addApp(repoAppData) returns TaskService(Task(Xor.right(repoApp)))
      val result = persistenceServices.addApp(createAddAppRequest()).value.run

      result must beLike {
        case Xor.Right(app) =>
          app.id shouldEqual appId
          app.packageName shouldEqual packageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.addApp(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addApp(createAddAppRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "addApps" should {

    "return Unit for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.addApps(Seq(repoAppData)) returns TaskService(Task(Xor.right(())))
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).value.run
      result shouldEqual Xor.Right((): Unit)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.addApps(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAllApps" should {

    "return the number of elements deleted for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps() returns TaskService(Task(Xor.right(items)))
      val result = persistenceServices.deleteAllApps().value.run
      result shouldEqual Xor.Right(items)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.deleteApps() returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAllApps().value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "deleteAppByPackage" should {

    "return the number of elements deleted for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.deleteAppByPackage(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.deleteAppByPackage(packageName).value.run
      result shouldEqual Xor.Right(1)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.deleteAppByPackage(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.deleteAppByPackage(packageName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "updateApp" should {

    "return the number of elements updated for a valid request" in new AppPersistenceServicesScope {

      mockAppRepository.updateApp(any) returns TaskService(Task(Xor.right(item)))
      val result = persistenceServices.updateApp(createUpdateAppRequest()).value.run
      result shouldEqual Xor.Right(1)
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.updateApp(any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.updateApp(createUpdateAppRequest()).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }


  "fetchIterableApps" should {

    "return a iterable of apps when pass OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableApps(OrderByCategory, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchIterableApps(OrderByName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchIterableAppsByKeyword" should {

    "return a iterable of apps when pass a keyword and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = true).value.run
      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByCategory" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableApps(any, any, any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchAppsByCategory" should {

    "return a sequence of apps when pass a category and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName, ascending = true).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.name))
    }

    "return a sequence of apps when pass a category and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Xor.right(seqRepoApp)))
      val result = persistenceServices.fetchAppsByCategory(category, OrderByInstallDate, ascending = true).value.run
      result shouldEqual Xor.Right(seqApp)
      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.dateInstalled))
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAppsByCategory(any, any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchIterableAppsByCategory" should {

    "return a iterable of apps when pass a category and OrderByName" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a category and OrderByInstallDate" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Xor.right(iterableCursorApp)))
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchIterableAppsByCategory(any, any) returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName).value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchAlphabeticalAppsCounter" should {

    "return a sequence of DataCounter sort alphabetically" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.right(dataCounters)))
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run

      result must beLike {
        case Xor.Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchCategorizedAppsCounter" should {

    "return a sequence of DataCounter by category sort alphabetically" in new AppPersistenceServicesScope {

      mockAppRepository.fetchCategorizedAppsCounter returns TaskService(Task(Xor.right(dataCounters)))
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run
      result must beLike {
        case Xor.Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchCategorizedAppsCounter returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }

  "fetchInstallationDateAppsCounter" should {

    "return a sequence of DataCounter by installation date" in new AppPersistenceServicesScope {

      mockAppRepository.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.right(dataCounters)))
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run
      result must beLike {
        case Xor.Right(counters) => counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new AppPersistenceServicesScope {

      mockAppRepository.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.left(exception)))
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run
      result must beAnInstanceOf[Xor.Left[RepositoryException]]
    }
  }
}