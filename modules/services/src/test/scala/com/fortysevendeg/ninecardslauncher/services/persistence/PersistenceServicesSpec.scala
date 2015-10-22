package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.repository.RepositoryException
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity
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

    val mockGeoInfoRepository = mock[GeoInfoRepository]

    val persistenceServices = new PersistenceServicesImpl(
      appRepository = mockAppRepository,
      cardRepository = mockCardRepository,
      collectionRepository = mockCollectionRepository,
      geoInfoRepository = mockGeoInfoRepository)
  }

  trait ValidRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    mockAppRepository.fetchApps(any) returns Service(Task(Result.answer(seqRepoApp)))

    mockAppRepository.fetchAppByPackage(packageName) returns Service(Task(Result.answer(Option(repoApp))))

    mockAppRepository.fetchAppByPackage(nonExistentPackageName) returns Service(Task(Result.answer(None)))

    mockAppRepository.addApp(repoAppData) returns Service(Task(Result.answer(repoApp)))

    mockAppRepository.deleteAppByPackage(packageName) returns Service(Task(Result.answer(1)))

    mockAppRepository.updateApp(repoApp) returns Service(Task(Result.answer(1)))

    mockGeoInfoRepository.addGeoInfo(repoGeoInfoData) returns Service(Task(Result.answer(repoGeoInfo)))

    mockGeoInfoRepository.deleteGeoInfo(repoGeoInfo) returns Service(Task(Result.answer(1)))

    mockGeoInfoRepository.fetchGeoInfoItems returns Service(Task(Result.answer(seqRepoGeoInfo)))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(constrain) returns Service(Task(Result.answer(Option(repoGeoInfo))))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(nonExistentConstrain) returns Service(Task(Result.answer(None)))

    mockGeoInfoRepository.findGeoInfoById(geoInfoId) returns Service(Task(Result.answer(Option(repoGeoInfo))))

    mockGeoInfoRepository.findGeoInfoById(nonExistentGeoInfoId) returns Service(Task(Result.answer(None)))

    mockGeoInfoRepository.updateGeoInfo(repoGeoInfo) returns Service(Task(Result.answer(1)))

    mockCardRepository.addCard(collectionId, repoCardData) returns Service(Task(Result.answer(repoCard)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns Service(Task(Result.answer(1)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns Service(Task(Result.answer(seqRepoCard)))
    }

    mockCardRepository.findCardById(cardId) returns Service(Task(Result.answer(Option(repoCard))))

    mockCardRepository.findCardById(nonExistentCardId) returns Service(Task(Result.answer(None)))

    mockCardRepository.updateCard(repoCard) returns Service(Task(Result.answer(1)))

    mockCollectionRepository.addCollection(repoCollectionData) returns Service(Task(Result.answer(repoCollection)))

    mockCollectionRepository.deleteCollection(repoCollection) returns Service(Task(Result.answer(1)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionByPosition(nonExistentPosition) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(nonExistentSharedCollectionId) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.fetchSortedCollections returns Service(Task(Result.answer(seqRepoCollection)))

    mockCollectionRepository.findCollectionById(collectionId) returns Service(Task(Result.answer(Option(repoCollection))))

    mockCollectionRepository.findCollectionById(nonExistentCollectionId) returns Service(Task(Result.answer(None)))

    mockCollectionRepository.updateCollection(repoCollection) returns Service(Task(Result.answer(1)))
  }

  trait ErrorRepositoryServicesResponses extends RepositoryServicesScope with PersistenceServicesData {

    val exception = RepositoryException("Irrelevant message")

    mockAppRepository.fetchApps(any) returns Service(Task(Result.errata(exception)))

    mockAppRepository.fetchAppByPackage(packageName) returns Service(Task(Result.errata(exception)))

    mockAppRepository.addApp(repoAppData) returns Service(Task(Result.errata(exception)))

    mockAppRepository.deleteAppByPackage(packageName) returns Service(Task(Result.errata(exception)))

    mockAppRepository.updateApp(repoApp) returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.addGeoInfo(repoGeoInfoData) returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.deleteGeoInfo(repoGeoInfo) returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.fetchGeoInfoItems returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(constrain) returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.findGeoInfoById(geoInfoId) returns Service(Task(Result.errata(exception)))

    mockGeoInfoRepository.updateGeoInfo(repoGeoInfo) returns Service(Task(Result.errata(exception)))

    mockCardRepository.addCard(collectionId, repoCardData) returns Service(Task(Result.errata(exception)))

    seqRepoCard foreach { repoCard =>
      mockCardRepository.deleteCard(repoCard) returns Service(Task(Result.errata(exception)))
    }

    List.tabulate(5) { index =>
      mockCardRepository.fetchCardsByCollection(collectionId + index) returns Service(Task(Result.errata(exception)))
    }

    mockCardRepository.findCardById(cardId) returns Service(Task(Result.errata(exception)))

    mockCardRepository.updateCard(repoCard) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.addCollection(repoCollectionData) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.deleteCollection(repoCollection) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchCollectionByPosition(position) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchCollectionBySharedCollectionId(sharedCollectionId) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.fetchSortedCollections returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.findCollectionById(collectionId) returns Service(Task(Result.errata(exception)))

    mockCollectionRepository.updateCollection(repoCollection) returns Service(Task(Result.errata(exception)))
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

      there was one(mockAppRepository).fetchApps(s"${AppEntity.name} ASC")
    }

    "return a sequence of the apps when pass OrderByName and descending order" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByName, ascending = false).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(s"${AppEntity.name} DESC")
    }

    "return a sequence of the apps when pass OrderByUpdate" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByUpdate, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(s"${AppEntity.dateUpdate} ASC")
    }

    "return a sequence of the apps when pass OrderByCategory" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchApps(OrderByCategory, ascending = true).run.run

      result must beLike[Result[Seq[App], PersistenceServiceException]] {
        case Answer(apps) =>
          apps shouldEqual seqApp
      }

      there was one(mockAppRepository).fetchApps(s"${AppEntity.category} ASC")
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

  "addGeoInfo" should {

    "return a GeoInfo value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addGeoInfo(createAddGeoInfoRequest()).run.run

      result must beLike {
        case Answer(geoInfo) =>
          geoInfo.id shouldEqual geoInfoId
          geoInfo.constrain shouldEqual constrain
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addGeoInfo(createAddGeoInfoRequest()).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "deleteGeoInfo" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteGeoInfo(createDeleteGeoInfoRequest(geoInfo = geoInfo)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteGeoInfo(createDeleteGeoInfoRequest(geoInfo = geoInfo)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchGeoInfoItems" should {

    "return a list of GeoInfo elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoItems.run.run

      result must beLike {
        case Answer(geoInfoItems) =>
          geoInfoItems.size shouldEqual seqGeoInfo.size
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoItems.run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "fetchGeoInfoByConstrain" should {

    "return a GeoInfo for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = constrain)).run.run

      result must beLike {
        case Answer(maybeGeoInfo) =>
          maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
            geoInfo.constrain shouldEqual constrain
          }
      }
    }

    "return None when a non-existent packageName is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = nonExistentConstrain)).run.run

      result must beLike {
        case Answer(maybeGeoInfo) =>
          maybeGeoInfo must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = constrain)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "findGeoInfoById" should {

    "return a GeoInfo for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = geoInfoId)).run.run

      result must beLike {
        case Answer(maybeGeoInfo) =>
          maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
            geoInfo.id shouldEqual geoInfoId
          }
      }
    }

    "return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = nonExistentGeoInfoId)).run.run

      result must beLike {
        case Answer(maybeGeoInfo) =>
          maybeGeoInfo must beNone
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = geoInfoId)).run.run

      result must beLike {
        case Errata(e) => e.headOption must beSome.which {
          case (_, (_, persistenceServiceException)) => persistenceServiceException must beLike {
            case e: PersistenceServiceException => e.cause must beSome.which(_ shouldEqual exception)
          }
        }
      }
    }
  }

  "updateGeoInfo" should {

    "return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateGeoInfo(createUpdateGeoInfoRequest()).run.run

      result must beLike {
        case Answer(updated) =>
          updated shouldEqual 1
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateGeoInfo(createUpdateGeoInfoRequest()).run.run

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

  "deleteCard" should {

    "return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card)).run.run

      result must beLike {
        case Answer(deleted) =>
          deleted shouldEqual 1
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
          updated shouldEqual 1
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

  "addCollection" should {

    "return a Collection value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCollection(createAddCollectionRequest()).run.run

      result must beLike {
        case Answer(collection) =>
          collection.id shouldEqual collectionId
          collection.collectionType shouldEqual collectionType
      }
    }

    "return a PersistenceServiceException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCollection(createAddCollectionRequest()).run.run

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
          deleted shouldEqual 1
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
          updated shouldEqual 1
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
}
