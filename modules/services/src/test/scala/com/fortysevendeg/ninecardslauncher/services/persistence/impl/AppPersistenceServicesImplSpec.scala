package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
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
    with DisjunctionMatchers {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with AppPersistenceServicesData {

    mockAppRepository.fetchApps(any) returns CatsService(Task(Xor.right(seqRepoApp)))

    mockAppRepository.fetchIterableApps(any, any, any) returns CatsService(Task(Xor.right(iterableCursorApp)))

    mockAppRepository.fetchAlphabeticalAppsCounter returns CatsService(Task(Xor.right(dataCounters)))

    mockAppRepository.fetchCategorizedAppsCounter returns CatsService(Task(Xor.right(dataCounters)))

    mockAppRepository.fetchInstallationDateAppsCounter returns CatsService(Task(Xor.right(dataCounters)))

    mockAppRepository.fetchAppsByCategory(any, any) returns CatsService(Task(Xor.right(seqRepoApp)))

    mockAppRepository.fetchIterableAppsByCategory(any, any) returns CatsService(Task(Xor.right(iterableCursorApp)))

    mockAppRepository.fetchAppByPackage(packageName) returns CatsService(Task(Xor.right(Option(repoApp))))

    mockAppRepository.fetchAppByPackage(nonExistentPackageName) returns CatsService(Task(Xor.right(None)))

    mockAppRepository.addApp(repoAppData) returns CatsService(Task(Xor.right(repoApp)))

    mockAppRepository.addApps(Seq(repoAppData)) returns CatsService(Task(Xor.right(())))

    mockAppRepository.deleteApps() returns CatsService(Task(Xor.right(items)))

    mockAppRepository.deleteAppByPackage(packageName) returns CatsService(Task(Xor.right(item)))

    mockAppRepository.updateApp(repoApp) returns CatsService(Task(Xor.right(item)))

  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with AppPersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockAppRepository.fetchApps(any) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchIterableApps(any, any, any) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchAlphabeticalAppsCounter returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchCategorizedAppsCounter returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchInstallationDateAppsCounter returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchAppsByCategory(any, any) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchIterableAppsByCategory(any, any) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.fetchAppByPackage(packageName) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.addApp(repoAppData) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.addApps(Seq(repoAppData)) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.deleteApps() returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.deleteAppByPackage(packageName) returns CatsService(Task(Xor.left(exception)))

    mockAppRepository.updateApp(repoApp) returns CatsService(Task(Xor.left(exception)))

  }

}

class AppPersistenceServicesImplSpec extends AppPersistenceServicesSpecSpecification {

  "fetchApps" should {

    "return a sequence of the apps when pass OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.name))
    }

    "return a sequence of the apps when pass OrderByName and descending order" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName, ascending = false).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.name).and(contain("DESC")))
    }

    "return a sequence of the apps when pass OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.dateInstalled))
    }

    "return a sequence of the apps when pass OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByCategory, ascending = true).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.category))
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findAppByPackage" should {

    "return an App when a valid packageName is provided" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(packageName).value.run

      result must beLike {
        case Xor.Right(maybeApp) =>
          maybeApp must beSome[App].which { app =>
            app.id shouldEqual appId
            app.packageName shouldEqual packageName
          }
      }
    }

    "return None when an invalid packageName is provided" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(nonExistentPackageName).value.run

      result must beLike {
        case Xor.Right(maybeApp) =>
          maybeApp must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(packageName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "addApp" should {

    "return a App value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addApp(createAddAppRequest()).value.run

      result must beLike {
        case Xor.Right(app) =>
          app.id shouldEqual appId
          app.packageName shouldEqual packageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addApp(createAddAppRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "addApps" should {

    "return Unit for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).value.run

      result must beLike {
        case Xor.Right(a) =>
          a shouldEqual ((): Unit)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllApps" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllApps().value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllApps().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAppByPackage" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAppByPackage(packageName).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAppByPackage(packageName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateApp" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateApp(createUpdateAppRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateApp(createUpdateAppRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }


  "fetchIterableApps" should {

    "return a iterable of apps when pass OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByCategory, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchIterableAppsByKeyword" should {

    "return a iterable of apps when pass a keyword and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchAppsByCategory" should {

    "return a sequence of apps when pass a category and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.name))
    }

    "return a sequence of apps when pass a category and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.dateInstalled))
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchIterableAppsByCategory" should {

    "return a iterable of apps when pass a category and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a category and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByInstallDate, ascending = true).value.run

      result must beLike {
        case Xor.Right(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchAlphabeticalAppsCounter" should {

    "return a sequence of DataCounter sort alphabetically" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run

      result must beLike {
        case Xor.Right(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchAlphabeticalAppsCounter.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCategorizedAppsCounter" should {

    "return a sequence of DataCounter by category sort alphabetically" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run

      result must beLike {
        case Xor.Right(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCategorizedAppsCounter.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchInstallationDateAppsCounter" should {

    "return a sequence of DataCounter by installation date" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run

      result must beLike {
        case Xor.Right(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchInstallationDateAppsCounter.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

}