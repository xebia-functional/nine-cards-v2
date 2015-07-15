package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models._
import org.mockito.Mockito._
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait PersistenceServicesSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait RepositoryServicesScope
    extends Scope {

    val mockCardRepository = mock[CardRepository]

    val mockCacheCategoryRepository = mock[CacheCategoryRepository]

    val mockCollectionRepository = mock[CollectionRepository]

    val mockGeoInfoRepository = mock[GeoInfoRepository]

    val persistenceServices = new PersistenceServicesImpl(
      cacheCategoryRepository = mockCacheCategoryRepository,
      cardRepository = mockCardRepository,
      collectionRepository = mockCollectionRepository,
      geoInfoRepository = mockGeoInfoRepository)
  }

  trait ValidRepositoryServicesResponses extends Mockito with RepositoryServicesScope with PersistenceServicesData {

    mockCacheCategoryRepository.addCacheCategory(repoCacheCategoryData) returns Task(\/-(repoCacheCategory))

    mockCacheCategoryRepository.deleteCacheCategory(repoCacheCategory) returns Task(\/-(1))

    mockCacheCategoryRepository.deleteCacheCategoryByPackage(packageName) returns Task(\/-(1))

    mockCacheCategoryRepository.fetchCacheCategories returns Task(\/-(seqRepoCacheCategory))

    mockCacheCategoryRepository.fetchCacheCategoryByPackage(packageName) returns Task(\/-(Option(repoCacheCategory)))

    mockCacheCategoryRepository.fetchCacheCategoryByPackage(nonExistentPackageName) returns Task(\/-(None))

    mockCacheCategoryRepository.findCacheCategoryById(cacheCategoryId) returns Task(\/-(Option(repoCacheCategory)))

    mockCacheCategoryRepository.findCacheCategoryById(nonExistentCacheCategoryId) returns Task(\/-(None))

    mockCacheCategoryRepository.updateCacheCategory(repoCacheCategory) returns Task(\/-(1))

    mockGeoInfoRepository.addGeoInfo(repoGeoInfoData) returns Task(\/-(repoGeoInfo))

    mockGeoInfoRepository.deleteGeoInfo(repoGeoInfo) returns Task(\/-(1))

    mockGeoInfoRepository.fetchGeoInfoItems returns Task(\/-(seqRepoGeoInfo))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(constrain) returns Task(\/-(Option(repoGeoInfo)))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(nonExistentConstrain) returns Task(\/-(None))

    mockGeoInfoRepository.findGeoInfoById(geoInfoId) returns Task(\/-(Option(repoGeoInfo)))

    mockGeoInfoRepository.findGeoInfoById(nonExistentGeoInfoId) returns Task(\/-(None))

    mockGeoInfoRepository.updateGeoInfo(repoGeoInfo) returns Task(\/-(1))

    mockCardRepository.addCard(collectionId, repoCardData) returns Task(\/-(repoCard))

    mockCardRepository.deleteCard(repoCard) returns Task(\/-(1))

    mockCardRepository.fetchCardsByCollection(collectionId) returns Task(\/-(seqRepoCard))

    mockCardRepository.findCardById(cardId) returns Task(\/-(Option(repoCard)))

    mockCardRepository.findCardById(nonExistentCardId) returns Task(\/-(None))

    mockCardRepository.updateCard(repoCard) returns Task(\/-(1))
  }

  trait ErrorRepositoryServicesResponses extends Mockito with RepositoryServicesScope with PersistenceServicesData {

    val exception = NineCardsException("Irrelevant message")

    mockCacheCategoryRepository.addCacheCategory(repoCacheCategoryData) returns Task(-\/(exception))

    mockCacheCategoryRepository.deleteCacheCategory(repoCacheCategory) returns Task(-\/(exception))

    mockCacheCategoryRepository.deleteCacheCategoryByPackage(packageName) returns Task(-\/(exception))

    mockCacheCategoryRepository.fetchCacheCategories returns Task(-\/(exception))

    mockCacheCategoryRepository.fetchCacheCategoryByPackage(packageName) returns Task(-\/(exception))

    mockCacheCategoryRepository.findCacheCategoryById(cacheCategoryId) returns Task(-\/(exception))

    mockCacheCategoryRepository.updateCacheCategory(repoCacheCategory) returns Task(-\/(exception))

    mockGeoInfoRepository.addGeoInfo(repoGeoInfoData) returns Task(-\/(exception))

    mockGeoInfoRepository.deleteGeoInfo(repoGeoInfo) returns Task(-\/(exception))

    mockGeoInfoRepository.fetchGeoInfoItems returns Task(-\/(exception))

    mockGeoInfoRepository.fetchGeoInfoByConstrain(constrain) returns Task(-\/(exception))

    mockGeoInfoRepository.findGeoInfoById(geoInfoId) returns Task(-\/(exception))

    mockGeoInfoRepository.updateGeoInfo(repoGeoInfo) returns Task(-\/(exception))

    mockCardRepository.addCard(collectionId, repoCardData) returns Task(-\/(exception))

    mockCardRepository.deleteCard(repoCard) returns Task(-\/(exception))

    mockCardRepository.fetchCardsByCollection(collectionId) returns Task(-\/(exception))

    mockCardRepository.findCardById(cardId) returns Task(-\/(exception))

    mockCardRepository.updateCard(repoCard) returns Task(-\/(exception))
  }
}

class PersistenceServicesSpec
  extends PersistenceServicesSpecification {

  "PersistenceServices" should {
    "addCacheCategory should return a CacheCategory value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCacheCategory(createAddCacheCategoryRequest())

      result.run must be_\/-[CacheCategory].which { cacheCategory =>
        cacheCategory.id shouldEqual cacheCategoryId
        cacheCategory.packageName shouldEqual packageName
      }
    }

    "addCacheCategory should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCacheCategory(createAddCacheCategoryRequest())

      result.run must be_-\/[NineCardsException]
    }

    "deleteCacheCategory should return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCacheCategory(createDeleteCacheCategoryRequest(cacheCategory = cacheCategory))

      result.run must be_\/-[Int].which { deleted =>
        deleted shouldEqual 1
      }
    }

    "deleteCacheCategory should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCacheCategory(createDeleteCacheCategoryRequest(cacheCategory = cacheCategory))

      result.run must be_-\/[NineCardsException]
    }

    "deleteCacheCategoryByPackage should return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCacheCategoryByPackage(createDeleteCacheCategoryByPackageRequest(packageName = packageName))

      result.run must be_\/-[Int].which { deleted =>
        deleted shouldEqual 1
      }
    }

    "deleteCacheCategoryByPackage should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCacheCategoryByPackage(createDeleteCacheCategoryByPackageRequest(packageName = packageName))

      result.run must be_-\/[NineCardsException]
    }

    "fetchCacheCategories should return a list of CacheCategory elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCacheCategories

      result.run must be_\/-[Seq[CacheCategory]].which { cacheCategories =>
        cacheCategories.size shouldEqual seqCacheCategory.size
      }
    }

    "fetchCacheCategories should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCacheCategories

      result.run must be_-\/[NineCardsException]
    }

    "fetchCacheCategoryByPackage should return a CacheCategory for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCacheCategoryByPackage(createFetchCacheCategoryByPackageRequest(packageName = packageName))

      result.run must be_\/-[Option[CacheCategory]].which { maybeCacheCategory =>
        maybeCacheCategory must beSome[CacheCategory].which { cacheCategory =>
          cacheCategory.packageName shouldEqual packageName
        }
      }
    }

    "fetchCacheCategoryByPackage should return None when a non-existent packageName is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCacheCategoryByPackage(createFetchCacheCategoryByPackageRequest(packageName = nonExistentPackageName))

      result.run must be_\/-[Option[CacheCategory]].which { maybeCacheCategory =>
        maybeCacheCategory must beNone
      }
    }

    "fetchCacheCategoryByPackage should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCacheCategoryByPackage(createFetchCacheCategoryByPackageRequest(packageName = packageName))

      result.run must be_-\/[NineCardsException]
    }

    "findCacheCategoryById should return a CacheCategory for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCacheCategoryById(createFindCacheCategoryByIdRequest(id = cacheCategoryId))

      result.run must be_\/-[Option[CacheCategory]].which { maybeCacheCategory =>
        maybeCacheCategory must beSome[CacheCategory].which { cacheCategory =>
          cacheCategory.packageName shouldEqual packageName
        }
      }
    }

    "findCacheCategoryById should return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCacheCategoryById(createFindCacheCategoryByIdRequest(id = nonExistentCacheCategoryId))

      result.run must be_\/-[Option[CacheCategory]].which { maybeCacheCategory =>
        maybeCacheCategory must beNone
      }
    }

    "findCacheCategoryById should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCacheCategoryById(createFindCacheCategoryByIdRequest(id = cacheCategoryId))

      result.run must be_-\/[NineCardsException]
    }

    "updateCacheCategory should return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCacheCategory(createUpdateCacheCategoryRequest())

      result.run must be_\/-[Int].which { updated =>
        updated shouldEqual 1
      }
    }

    "updateCacheCategory should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCacheCategory(createUpdateCacheCategoryRequest())

      result.run must be_-\/[NineCardsException]
    }

    "addGeoInfo should return a GeoInfo value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addGeoInfo(createAddGeoInfoRequest())

      result.run must be_\/-[GeoInfo].which { geoInfo =>
        geoInfo.id shouldEqual geoInfoId
        geoInfo.constrain shouldEqual constrain
      }
    }

    "addGeoInfo should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addGeoInfo(createAddGeoInfoRequest())

      result.run must be_-\/[NineCardsException]
    }

    "deleteGeoInfo should return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteGeoInfo(createDeleteGeoInfoRequest(geoInfo = geoInfo))

      result.run must be_\/-[Int].which { deleted =>
        deleted shouldEqual 1
      }
    }

    "deleteGeoInfo should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteGeoInfo(createDeleteGeoInfoRequest(geoInfo = geoInfo))

      result.run must be_-\/[NineCardsException]
    }

    "fetchGeoInfoItems should return a list of GeoInfo elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoItems

      result.run must be_\/-[Seq[GeoInfo]].which { geoInfoItems =>
        geoInfoItems.size shouldEqual seqGeoInfo.size
      }
    }

    "fetchGeoInfoItems should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoItems

      result.run must be_-\/[NineCardsException]
    }

    "fetchGeoInfoByConstrain should return a GeoInfo for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = constrain))

      result.run must be_\/-[Option[GeoInfo]].which { maybeGeoInfo =>
        maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
          geoInfo.constrain shouldEqual constrain
        }
      }
    }

    "fetchGeoInfoByConstrain should return None when a non-existent packageName is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = nonExistentConstrain))

      result.run must be_\/-[Option[GeoInfo]].which { maybeGeoInfo =>
        maybeGeoInfo must beNone
      }
    }

    "fetchGeoInfoByConstrain should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchGeoInfoByConstrain(createFetchGeoInfoByConstrainRequest(constrain = constrain))

      result.run must be_-\/[NineCardsException]
    }

    "findGeoInfoById should return a GeoInfo for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = geoInfoId))

      result.run must be_\/-[Option[GeoInfo]].which { maybeGeoInfo =>
        maybeGeoInfo must beSome[GeoInfo].which { geoInfo =>
          geoInfo.constrain shouldEqual constrain
        }
      }
    }

    "findGeoInfoById should return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = nonExistentGeoInfoId))

      result.run must be_\/-[Option[GeoInfo]].which { maybeGeoInfo =>
        maybeGeoInfo must beNone
      }
    }

    "findGeoInfoById should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findGeoInfoById(createFindGeoInfoByIdRequest(id = geoInfoId))

      result.run must be_-\/[NineCardsException]
    }

    "updateGeoInfo should return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateGeoInfo(createUpdateGeoInfoRequest())

      result.run must be_\/-[Int].which { updated =>
        updated shouldEqual 1
      }
    }

    "updateGeoInfo should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateGeoInfo(createUpdateGeoInfoRequest())

      result.run must be_-\/[NineCardsException]
    }

    "addCard should return a Card value for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest())

      result.run must be_\/-[Card].which { card =>
        card.id shouldEqual cardId
        card.cardType shouldEqual cardType
      }
    }

    "addCard should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.addCard(createAddCardRequest())

      result.run must be_-\/[NineCardsException]
    }

    "deleteCard should return the number of elements deleted for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card))

      result.run must be_\/-[Int].which { deleted =>
        deleted shouldEqual 1
      }
    }

    "deleteCard should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.deleteCard(createDeleteCardRequest(card = card))

      result.run must be_-\/[NineCardsException]
    }

    "fetchCardsByCollection should return a list of Card elements for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId))

      result.run must be_\/-[Seq[Card]].which { cards =>
        cards.size shouldEqual seqCard.size
      }
    }

    "fetchCardItems should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.fetchCardsByCollection(createFetchCardsByCollectionRequest(collectionId))

      result.run must be_-\/[NineCardsException]
    }

    "findCardById should return a Card for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId))

      result.run must be_\/-[Option[Card]].which { maybeCard =>
        maybeCard must beSome[Card].which { card =>
          card.cardType shouldEqual cardType
        }
      }
    }

    "findCardById should return None when a non-existent id is given" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = nonExistentCardId))

      result.run must be_\/-[Option[Card]].which { maybeCard =>
        maybeCard must beNone
      }
    }

    "findCardById should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.findCardById(createFindCardByIdRequest(id = cardId))

      result.run must be_-\/[NineCardsException]
    }

    "updateCard should return the number of elements updated for a valid request" in new ValidRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest())

      result.run must be_\/-[Int].which { updated =>
        updated shouldEqual 1
      }
    }

    "updateCard should return a NineCardException if the service throws a exception" in new ErrorRepositoryServicesResponses {
      val result = persistenceServices.updateCard(createUpdateCardRequest())

      result.run must be_-\/[NineCardsException]
    }
  }
}
