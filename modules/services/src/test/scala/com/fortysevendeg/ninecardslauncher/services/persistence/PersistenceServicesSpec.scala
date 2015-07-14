package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence.impl.PersistenceServicesImpl
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
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

    val seqCacheCategory = createSeqCacheCategory()
    val cacheCategory = seqCacheCategory.head
    val repoCacheCategoryData = createRepoCacheCategoryData()
    val seqRepoCacheCategory = createSeqRepoCacheCategory(data = repoCacheCategoryData)
    val repoCacheCategory = seqRepoCacheCategory.head

    when(mockCacheCategoryRepository.addCacheCategory(repoCacheCategoryData)).thenReturn(Task(\/-(repoCacheCategory)))

    when(mockCacheCategoryRepository.deleteCacheCategory(repoCacheCategory)).thenReturn(Task(\/-(1)))

    when(mockCacheCategoryRepository.deleteCacheCategoryByPackage(packageName)).thenReturn(Task(\/-(1)))

    when(mockCacheCategoryRepository.fetchCacheCategories).thenReturn(Task(\/-(seqRepoCacheCategory)))

    when(mockCacheCategoryRepository.fetchCacheCategoryByPackage(packageName)).thenReturn(Task(\/-(Option(repoCacheCategory))))

    when(mockCacheCategoryRepository.fetchCacheCategoryByPackage(nonExistentPackageName)).thenReturn(Task(\/-(None)))

    when(mockCacheCategoryRepository.findCacheCategoryById(cacheCategoryId)).thenReturn(Task(\/-(Option(repoCacheCategory))))

    when(mockCacheCategoryRepository.findCacheCategoryById(nonExistentCacheCategoryId)).thenReturn(Task(\/-(None)))

    when(mockCacheCategoryRepository.updateCacheCategory(repoCacheCategory)).thenReturn(Task(\/-(1)))
  }

  trait ErrorRepositoryServicesResponses extends Mockito with RepositoryServicesScope with PersistenceServicesData {

    val exception = NineCardsException("Irrelevant message")

    val seqCacheCategory = createSeqCacheCategory()
    val cacheCategory = seqCacheCategory.head
    val repoCacheCategoryData = createRepoCacheCategoryData()
    val seqRepoCacheCategory = createSeqRepoCacheCategory(data = repoCacheCategoryData)
    val repoCacheCategory = seqRepoCacheCategory.head

    when(mockCacheCategoryRepository.addCacheCategory(repoCacheCategoryData)).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.deleteCacheCategory(repoCacheCategory)).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.deleteCacheCategoryByPackage(packageName)).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.fetchCacheCategories).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.fetchCacheCategoryByPackage(packageName)).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.findCacheCategoryById(cacheCategoryId)).thenReturn(Task(-\/(exception)))

    when(mockCacheCategoryRepository.updateCacheCategory(repoCacheCategory)).thenReturn(Task(-\/(exception)))
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
  }
}
