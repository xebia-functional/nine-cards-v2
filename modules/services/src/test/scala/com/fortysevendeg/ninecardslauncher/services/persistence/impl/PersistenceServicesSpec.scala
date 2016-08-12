package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.{AppEntity, CardEntity, MomentEntity}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence.data.PersistenceServicesData
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import com.fortysevendeg.ninecardslauncher.services.persistence.{OrderByCategory, OrderByInstallDate, OrderByName}
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scalaz.concurrent.Task

trait PersistenceServicesSpecification
  extends Specification
    with DisjunctionMatchers
    with Mockito {

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

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

    mockCardRepository.addCard(collectionId, repoCardData) returns CatsService(Task(Xor.right(repoCard)))

    mockCardRepository.addCards(any) returns CatsService(Task(Xor.right(Seq(repoCard))))

    mockCardRepository.deleteCards() returns CatsService(Task(Xor.right(items)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.right(items)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns CatsService(Task(Xor.right(item)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns CatsService(Task(Xor.right(seqRepoCard)))
    }

    mockCardRepository.fetchCards returns CatsService(Task(Xor.right(seqRepoCard)))

    mockCardRepository.findCardById(cardId) returns CatsService(Task(Xor.right(Option(repoCard))))

    mockCardRepository.findCardById(nonExistentCardId) returns CatsService(Task(Xor.right(None)))

    mockCardRepository.updateCard(repoCard) returns CatsService(Task(Xor.right(item)))

    mockCardRepository.updateCards(seqRepoCard) returns CatsService(Task(Xor.right(item to items)))

    mockCollectionRepository.addCollection(repoCollectionData) returns CatsService(Task(Xor.right(repoCollection)))

    mockCollectionRepository.deleteCollections() returns CatsService(Task(Xor.right(items)))

    mockCollectionRepository.deleteCollection(repoCollection) returns CatsService(Task(Xor.right(item)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns CatsService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionByPosition(nonExistentPosition) returns CatsService(Task(Xor.right(None)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns CatsService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(nonExistentSharedCollectionId) returns CatsService(Task(Xor.right(None)))

    mockCollectionRepository.fetchSortedCollections returns CatsService(Task(Xor.right(seqRepoCollection)))

    mockCollectionRepository.findCollectionById(collectionId) returns CatsService(Task(Xor.right(Option(repoCollection))))

    mockCollectionRepository.findCollectionById(nonExistentCollectionId) returns CatsService(Task(Xor.right(None)))

    mockCollectionRepository.updateCollection(repoCollection) returns CatsService(Task(Xor.right(item)))

    mockUserRepository.addUser(repoUserData) returns CatsService(Task(Xor.right(repoUser)))

    mockUserRepository.deleteUsers() returns CatsService(Task(Xor.right(items)))

    mockUserRepository.deleteUser(repoUser) returns CatsService(Task(Xor.right(item)))

    mockUserRepository.fetchUsers returns CatsService(Task(Xor.right(seqRepoUser)))

    mockUserRepository.findUserById(uId) returns CatsService(Task(Xor.right(Option(repoUser))))

    mockUserRepository.findUserById(nonExistentUserId) returns CatsService(Task(Xor.right(None)))

    mockUserRepository.updateUser(repoUser) returns CatsService(Task(Xor.right(item)))

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

    mockCardRepository.addCard(collectionId, repoCardData) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards() returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns CatsService(Task(Xor.left(exception)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns CatsService(Task(Xor.left(exception)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns CatsService(Task(Xor.left(exception)))
    }

    mockCardRepository.fetchCards returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.findCardById(cardId) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.updateCard(repoCard) returns CatsService(Task(Xor.left(exception)))

    mockCardRepository.updateCards(seqRepoCard) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.addCollection(repoCollectionData) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.deleteCollections() returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.deleteCollection(repoCollection) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.fetchSortedCollections returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.findCollectionById(collectionId) returns CatsService(Task(Xor.left(exception)))

    mockCollectionRepository.updateCollection(repoCollection) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.addUser(repoUserData) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.deleteUsers() returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.deleteUser(repoUser) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.fetchUsers returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.findUserById(uId) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.findUserById(nonExistentUserId) returns CatsService(Task(Xor.left(exception)))

    mockUserRepository.updateUser(repoUser) returns CatsService(Task(Xor.left(exception)))

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

class PersistenceServicesSpec
  extends PersistenceServicesSpecification {

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

  "addCard" should {

    "return a Card value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Xor.Right(card) =>
          card.id shouldEqual cardId
          card.cardType shouldEqual cardType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Xor.Right(cards) =>
          cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCards" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Right(cards) =>
          cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCardById" should {

    "return a Card for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Right(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId)).value.run

      result must beLike {
        case Xor.Right(maybeCard) =>
          maybeCard must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual (item to items)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "addCollection" should {

    "return a Collection value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).value.run

      result must beLike {
        case Xor.Right(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllCollections" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollectionByPosition" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.position shouldEqual position
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(nonExistentPosition)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollectionBySharedCollection" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(sharedCollectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(nonExistentSharedCollectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(sharedCollectionId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchCollections" should {

    "return a list of Collection elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.value.run

      result must beLike {
        case Xor.Right(collections) =>
          collections.size shouldEqual seqCollection.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findCollectionById" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = nonExistentCollectionId)).value.run

      result must beLike {
        case Xor.Right(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateCollection" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "addUser" should {

    "return a User value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).value.run

      result must beLike {
        case Xor.Right(user) =>
          user.id shouldEqual uId
          user.userId shouldEqual Some(userId)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteAllUsers" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "deleteUser" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run

      result must beLike {
        case Xor.Right(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "fetchUsers" should {

    "return a list of User elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.value.run

      result must beLike {
        case Xor.Right(userItems) =>
          userItems.size shouldEqual seqUser.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "findUserById" should {

    "return a User for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run

      result must beLike {
        case Xor.Right(maybeUser) =>
          maybeUser must beSome[User].which { user =>
            user.id shouldEqual uId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = nonExistentUserId)).value.run

      result must beLike {
        case Xor.Right(maybeUser) =>
          maybeUser must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

  "updateUser" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run

      result must beLike {
        case Xor.Right(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).value.run

      result must beLike {
        case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
      }
    }
  }

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
