package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.{AppEntity, CardEntity, MomentEntity}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait PersistenceServicesSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait RepositoryServicesScope
    extends Scope {

    val mockAppRepository = mock[AppRepository]

    val mockCardRepository = mock[CardRepository]

    val mockCollectionRepository = mock[CollectionRepository]

    val mockDockAppRepository = mock[DockAppRepository]

    val mockMomentRepository = mock[MomentRepository]

    val mockUserRepository = mock[UserRepository]

    val persistenceServices = new PersistenceServicesImpl(
      appRepository = mockAppRepository,
      cardRepository = mockCardRepository,
      collectionRepository = mockCollectionRepository,
      dockAppRepository = mockDockAppRepository,
      momentRepository = mockMomentRepository,
      userRepository = mockUserRepository)
  }

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    mockAppRepository.fetchApps(any) returns Service(Task(Result.answer(seqRepoApp)))

    mockAppRepository.fetchIterableApps(any, any, any) returns Service(Task(Result.answer(iterableCursorApp)))

    mockAppRepository.fetchAlphabeticalAppsCounter returns Service(Task(Result.answer(dataCounters)))

    mockAppRepository.fetchCategorizedAppsCounter returns Service(Task(Result.answer(dataCounters)))

    mockAppRepository.fetchInstallationDateAppsCounter returns Service(Task(Result.answer(dataCounters)))

    mockAppRepository.fetchAppsByCategory(any, any) returns Service(Task(Result.answer(seqRepoApp)))

    mockAppRepository.fetchIterableAppsByCategory(any, any) returns Service(Task(Result.answer(iterableCursorApp)))

    mockAppRepository.fetchAppByPackage(packageName) returns Service(Task(Result.answer(Option(repoApp))))

    mockAppRepository.fetchAppByPackage(nonExistentPackageName) returns Service(Task(Result.answer(None)))

    mockAppRepository.addApp(repoAppData) returns Service(Task(Result.answer(repoApp)))

    mockAppRepository.addApps(Seq(repoAppData)) returns Service(Task(Result.answer(())))

    mockAppRepository.deleteApps() returns Service(Task(Result.answer(items)))

    mockAppRepository.deleteAppByPackage(packageName) returns Service(Task(Result.answer(item)))

    mockAppRepository.updateApp(repoApp) returns Service(Task(Result.answer(item)))

    mockCardRepository.addCard(collectionId, repoCardData) returns Service(Task(Result.answer(repoCard)))

    mockCardRepository.addCards(any) returns Service(Task(Result.answer(Seq(repoCard))))

    mockCardRepository.deleteCards() returns Service(Task(Result.answer(items)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns Service(Task(Result.answer(items)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns Service(Task(Result.answer(item)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns Service(Task(Result.answer(seqRepoCard)))
    }

    mockCardRepository.fetchCards returns Service(Task(Result.answer(seqRepoCard)))

    mockCardRepository.findCardById(cardId) returns Service(Task(Result.answer(Option(repoCard))))

    mockCardRepository.findCardById(nonExistentCardId) returns Service(Task(Result.answer(None)))

    mockCardRepository.updateCard(repoCard) returns Service(Task(Result.answer(item)))

    mockCardRepository.updateCards(seqRepoCard) returns Service(Task(Result.answer(item to items)))

    mockCollectionRepository.addCollection(repoCollectionData) returns Service(Task(Result.answer(repoCollection)))

    mockCollectionRepository.deleteCollections() returns Service(Task(Result.answer(items)))

    mockCollectionRepository.deleteCollection(repoCollection) returns Service(Task(Result.answer(item)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionByPosition(nonExistentPosition) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(nonExistentSharedCollectionId) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.fetchSortedCollections returns Service(Task(Result.answer(seqRepoCollection)))

    mockCollectionRepository.findCollectionById(collectionId) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.findCollectionById(nonExistentCollectionId) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.updateCollection(repoCollection) returns Service(Task(Result.answer(item)))

    mockUserRepository.addUser(repoUserData) returns Service(Task(Result.answer(repoUser)))

    mockUserRepository.deleteUsers() returns Service(Task(Result.answer(items)))

    mockUserRepository.deleteUser(repoUser) returns Service(Task(Result.answer(item)))

    mockUserRepository.fetchUsers returns Service(Task(Result.answer(seqRepoUser)))

    mockUserRepository.findUserById(uId) returns Service(Task(Result.answer(Option(repoUser))))

    mockUserRepository.findUserById(nonExistentUserId) returns Service(Task(Result.answer(None)))

    mockUserRepository.updateUser(repoUser) returns Service(Task(Result.answer(item)))

    mockDockAppRepository.addDockApp(repoDockAppData) returns Service(Task(Result.answer(repoDockApp)))

    mockDockAppRepository.addDockApps(any) returns Service(Task(Result.answer(Seq(repoDockApp))))

    mockDockAppRepository.deleteDockApps() returns Service(Task(Result.answer(items)))

    mockDockAppRepository.deleteDockApp(repoDockApp) returns Service(Task(Result.answer(item)))

    mockDockAppRepository.fetchDockApps() returns Service(Task(Result.answer(seqRepoDockApp)))

    mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})") returns Service(Task(Result.answer(seqRepoDockApp)))

    mockDockAppRepository.fetchIterableDockApps(any, any, any) returns Service(Task(Result.answer(iterableCursorDockApps)))

    mockDockAppRepository.findDockAppById(dockAppId) returns Service(Task(Result.answer(Option(repoDockApp))))

    mockDockAppRepository.findDockAppById(nonExistentDockAppId) returns Service(Task(Result.answer(None)))

    mockDockAppRepository.updateDockApp(repoDockApp) returns Service(Task(Result.answer(item)))

    mockDockAppRepository.updateDockApps(any) returns Service(Task(Result.answer(Seq(item))))

    mockMomentRepository.addMoment(repoMomentData) returns Service(Task(Result.answer(repoMoment)))

    mockMomentRepository.addMoment(createRepoMomentData(wifiString = "")) returns Service(Task(Result.answer(createSeqRepoMoment(data = createRepoMomentData(wifiString = ""))(0))))

    mockMomentRepository.addMoment(createRepoMomentData(timeslot = "[]")) returns Service(Task(Result.answer(createSeqRepoMoment(data = createRepoMomentData(timeslot = "[]"))(0))))

    mockMomentRepository.addMoments(any) returns Service(Task(Result.answer(Seq(repoMoment))))

    mockMomentRepository.deleteMoments() returns Service(Task(Result.answer(items)))

    mockMomentRepository.deleteMoment(repoMoment) returns Service(Task(Result.answer(item)))

    mockMomentRepository.fetchMoments() returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 1}") returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 2}") returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 3}") returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${collectionId + 4}") returns Service(Task(Result.answer(seqRepoMoment)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns Service(Task(Result.answer(Seq.empty)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns Service(Task(Result.answer(Seq.empty)))

    mockMomentRepository.findMomentById(momentId) returns Service(Task(Result.answer(Option(repoMoment))))

    mockMomentRepository.findMomentById(nonExistentMomentId) returns Service(Task(Result.answer(None)))

    mockMomentRepository.updateMoment(repoMoment) returns Service(Task(Result.answer(item)))
  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockAppRepository.fetchApps(any) returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchIterableApps(any, any, any) returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchAlphabeticalAppsCounter returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchCategorizedAppsCounter returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchInstallationDateAppsCounter returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchAppsByCategory(any, any) returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchIterableAppsByCategory(any, any) returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchAppByPackage(packageName) returns Service(Task(Result.errata(exception)))

    mockAppRepository.addApp(repoAppData) returns Service(Task(Result.errata(exception)))

    mockAppRepository.addApps(Seq(repoAppData)) returns Service(Task(Result.errata(exception)))

    mockAppRepository.deleteApps() returns Service(Task(Result.errata(exception)))

    mockAppRepository.deleteAppByPackage(packageName) returns Service(Task(Result.errata(exception)))

    mockAppRepository.updateApp(repoApp) returns Service(Task(Result.errata(exception)))

    mockCardRepository.addCard(collectionId, repoCardData) returns Service(Task(Result.errata(exception)))

    mockCardRepository.deleteCards() returns Service(Task(Result.errata(exception)))

    mockCardRepository.deleteCards(where = s"${CardEntity.collectionId} = $collectionId") returns Service(Task(Result.errata(exception)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns Service(Task(Result.errata(exception)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns Service(Task(Result.errata(exception)))
    }

    mockCardRepository.fetchCards returns Service(Task(Result.errata(exception)))

    mockCardRepository.findCardById(cardId) returns Service(Task(Result.errata(exception)))

    mockCardRepository.updateCard(repoCard) returns Service(Task(Result.errata(exception)))

    mockCardRepository.updateCards(seqRepoCard) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.addCollection(repoCollectionData) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.deleteCollections() returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.deleteCollection(repoCollection) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchSortedCollections returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.findCollectionById(collectionId) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.updateCollection(repoCollection) returns Service(Task(Result.errata(exception)))

    mockUserRepository.addUser(repoUserData) returns Service(Task(Result.errata(exception)))

    mockUserRepository.deleteUsers() returns Service(Task(Result.errata(exception)))

    mockUserRepository.deleteUser(repoUser) returns Service(Task(Result.errata(exception)))

    mockUserRepository.fetchUsers returns Service(Task(Result.errata(exception)))

    mockUserRepository.findUserById(uId) returns Service(Task(Result.errata(exception)))

    mockUserRepository.findUserById(nonExistentUserId) returns Service(Task(Result.errata(exception)))

    mockUserRepository.updateUser(repoUser) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.addDockApp(repoDockAppData) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.addDockApps(any) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.deleteDockApps() returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.deleteDockApp(repoDockApp) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.fetchDockApps() returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.fetchDockApps(where = s"position IN (${Seq(position).mkString("\"", ",", "\"")})")  returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.fetchIterableDockApps(any, any, any) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.findDockAppById(dockAppId) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.findDockAppById(nonExistentDockAppId) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.updateDockApp(repoDockApp) returns Service(Task(Result.errata(exception)))

    mockDockAppRepository.updateDockApps(any) returns Service(Task(Result.errata(exception)))

    mockMomentRepository.addMoment(repoMomentData) returns Service(Task(Result.errata(exception)))

    mockMomentRepository.deleteMoments() returns Service(Task(Result.errata(exception)))

    mockMomentRepository.deleteMoment(repoMoment) returns Service(Task(Result.errata(exception)))

    mockMomentRepository.fetchMoments() returns Service(Task(Result.errata(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $collectionId") returns Service(Task(Result.errata(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = $nonExistentCollectionId") returns Service(Task(Result.errata(exception)))

    mockMomentRepository.fetchMoments(where = s"${MomentEntity.collectionId} = ${None.orNull}") returns Service(Task(Result.errata(exception)))

    mockMomentRepository.findMomentById(momentId) returns Service(Task(Result.errata(exception)))

    mockMomentRepository.findMomentById(nonExistentMomentId) returns Service(Task(Result.errata(exception)))

    mockMomentRepository.updateMoment(repoMoment) returns Service(Task(Result.errata(exception)))
  }

}

class PersistenceServicesSpec
  extends PersistenceServicesSpecification {

  "fetchApps" should {

    "return a sequence of the apps when pass OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.name))
    }

    "return a sequence of the apps when pass OrderByName and descending order" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName, ascending = false).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.name).and(contain("DESC")))
    }

    "return a sequence of the apps when pass OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByInstallDate, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.dateInstalled))
    }

    "return a sequence of the apps when pass OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByCategory, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(contain(AppEntity.category))
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchIterableApps" should {

    "return a iterable of apps when pass OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByName, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByInstallDate, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByCategory, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableApps(OrderByName).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchIterableAppsByKeyword" should {

    "return a iterable of apps when pass a keyword and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a keyword and OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByKeyword(keyword, OrderByName).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchAppsByCategory" should {

    "return a sequence of apps when pass a category and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.name))
    }

    "return a sequence of apps when pass a category and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByInstallDate, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchAppsByCategory(contain(category), contain(AppEntity.dateInstalled))
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchAppsByCategory(category, OrderByName).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchIterableAppsByCategory" should {

    "return a iterable of apps when pass a category and OrderByName" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a iterable of apps when pass a category and OrderByInstallDate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByInstallDate, ascending = true).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Answer(iter) =>
          iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchIterableAppsByCategory(category, OrderByName).run.run

      result must beLike[Result[IterableApps, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchAlphabeticalAppsCounter" should {

    "return a sequence of DataCounter sort alphabetically" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchAlphabeticalAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Answer(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchAlphabeticalAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCategorizedAppsCounter" should {

    "return a sequence of DataCounter by category sort alphabetically" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCategorizedAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Answer(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCategorizedAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchInstallationDateAppsCounter" should {

    "return a sequence of DataCounter by installation date" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchInstallationDateAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Answer(counters) =>
          counters map (_.term) shouldEqual (dataCounters map (_.term))
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchInstallationDateAppsCounter.run.run

      result must beLike[Result[Seq[DataCounter], PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findAppByPackage" should {

    "return an App when a valid packageName is provided" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(packageName).run.run

      result must beLike {
        case Answer(maybeApp) =>
          maybeApp must beSome[App].which { app =>
            app.id shouldEqual appId
            app.packageName shouldEqual packageName
          }
      }
    }

    "return None when an invalid packageName is provided" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(nonExistentPackageName).run.run

      result must beLike {
        case Answer(maybeApp) =>
          maybeApp must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findAppByPackage(packageName).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addApp" should {

    "return a App value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addApp(createAddAppRequest()).run.run

      result must beLike[Result[App, PersistenceServiceException]] {
        case Answer(app) =>
          app.id shouldEqual appId
          app.packageName shouldEqual packageName
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addApp(createAddAppRequest()).run.run

      result must beLike[Result[App, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addApps" should {

    "return Unit for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).run.run

      result must beLike[Result[Unit, PersistenceServiceException]] {
        case Answer(a) =>
          a shouldEqual ((): Unit)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addApps(Seq(createAddAppRequest())).run.run

      result must beLike[Result[Unit, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllApps" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllApps().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllApps().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAppByPackage" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAppByPackage(packageName).run.run

      result must beLike[Result[Int, PersistenceServiceException]] {
        case Answer(deleted) =>
          deleted shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAppByPackage(packageName).run.run

      result must beLike[Result[Int, PersistenceServiceException]] {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateApp" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateApp(createUpdateAppRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateApp(createUpdateAppRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addCard" should {

    "return a Card value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).run.run

      result must beLike {
        case Answer(card) =>
          card.id shouldEqual cardId
          card.cardType shouldEqual cardType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllCards" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCards().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteCardsByCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCardsByCollection(collectionId).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCardsByCollection" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).run.run

      result must beLike {
        case Answer(cards) =>
          cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCards" should {

    "return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.run.run

      result must beLike {
        case Answer(cards) =>
          cards.size shouldEqual seqCard.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCards.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findCardById" should {

    "return a Card for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).run.run

      result must beLike {
        case Answer(maybeCard) =>
          maybeCard must beSome[Card].which { card =>
            card.cardType shouldEqual cardType
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId)).run.run

      result must beLike {
        case Answer(maybeCard) =>
          maybeCard must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateCard" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateCards" should {

    "return the sequence with the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual (item to items)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCards(createUpdateCardsRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addCollection" should {

    "return a Collection value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).run.run

      result must beLike {
        case Answer(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCollection(addCollectionRequest).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllCollections" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllCollections().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteCollection" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCollection(createDeleteCollectionRequest(collection = collection)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCollectionByPosition" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.position shouldEqual position
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(nonExistentPosition)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionByPosition(createFetchCollectionByPositionRequest(position)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCollectionBySharedCollection" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(sharedCollectionId)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(nonExistentSharedCollectionId)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollectionBySharedCollection(createFetchCollectionBySharedCollection(sharedCollectionId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchCollections" should {

    "return a list of Collection elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.run.run

      result must beLike {
        case Answer(collections) =>
          collections.size shouldEqual seqCollection.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCollections.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findCollectionById" should {

    "return a Collection for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beSome[Collection].which { collection =>
            collection.id shouldEqual collectionId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = nonExistentCollectionId)).run.run

      result must beLike {
        case Answer(maybeCollection) =>
          maybeCollection must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCollectionById(createFindCollectionByIdRequest(id = collectionId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateCollection" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCollection(createUpdateCollectionRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "addUser" should {

    "return a User value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).run.run

      result must beLike {
        case Answer(user) =>
          user.id shouldEqual uId
          user.userId shouldEqual Some(userId)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addUser(createAddUserRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllUsers" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllUsers().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteUser" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteUser(createDeleteUserRequest(user = user)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchUsers" should {

    "return a list of User elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.run.run

      result must beLike {
        case Answer(userItems) =>
          userItems.size shouldEqual seqUser.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchUsers.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findUserById" should {

    "return a User for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).run.run

      result must beLike {
        case Answer(maybeUser) =>
          maybeUser must beSome[User].which { user =>
            user.id shouldEqual uId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = nonExistentUserId)).run.run

      result must beLike {
        case Answer(maybeUser) =>
          maybeUser must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findUserById(createFindUserByIdRequest(id = uId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateUser" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateUser(createUpdateUserRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "createOrUpdateDockApp" should {

    "return a DockApp value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.createOrUpdateDockApp(Seq(createCreateOrUpdateDockAppRequest())).run.run

      result must beLike {
        case Answer(a) =>
          a shouldEqual ((): Unit)
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
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

  "addMoment" should {

    "return a Moment for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest()).run.run

      result must beLike {
        case Answer(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
      }
    }

    "return a Moment with a empty wifi sequence for a valid request with a empty wifi sequence" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest(wifi = Seq.empty)).run.run

      result must beLike {
        case Answer(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual Seq.empty
      }
    }

    "return a Moment with an empty timeslot sequence for a valid request with an empty timeslot" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest(timeslot = Seq.empty)).run.run

      result must beLike {
        case Answer(moment) =>
          moment.id shouldEqual momentId
          moment.wifi shouldEqual wifiSeq
          moment.timeslot shouldEqual Seq.empty
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addMoment(createAddMomentRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteAllMoments" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteAllMoments().run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual items
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteAllMoments().run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteMoment" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteMoment(createDeleteMomentRequest(moment = servicesMoment)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchMoments" should {

    "return a list of Moment elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchMoments.run.run

      result must beLike {
        case Answer(momentItems) =>
          momentItems.size shouldEqual seqMoment.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchMoments.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findMomentById" should {

    "return a Moment for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).run.run

      result must beLike {
        case Answer(maybeMoment) =>
          maybeMoment must beSome[Moment].which { moment =>
            moment.id shouldEqual momentId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = nonExistentMomentId)).run.run

      result must beLike {
        case Answer(maybeMoment) =>
          maybeMoment must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findMomentById(createFindMomentByIdRequest(id = momentId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateMoment" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual item
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateMoment(createUpdateMomentRequest()).run.run

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
