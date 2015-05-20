package com.fortysevendeg.repository.cacheCategory

import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class CacheCategoryRepositoryClientSpec
    extends Specification
    with Mockito {

  "CacheCategoryRepositoryClient component" should {

    "addCacheCategory should return a valid CacheCategory object" in new AddCacheCategorySupport {

      val result = await(repoAddCacheCategory(createAddCacheCategoryRequest))

      result.cacheCategory.id shouldEqual cacheCategoryId
      result.cacheCategory.data.packageName shouldEqual packageName
    }

    "deleteCacheCategory should return a successful response when a valid cache category id is given" in
        new DeleteCacheCategorySupport {
          val response = await(repoDeleteCacheCategory(createDeleteCacheCategoryRequest))

          response.deleted shouldEqual 1
        }

    "deleteCacheCategoryByPackage should return a successful response when a valid package name is given" in
        new DeleteCacheCategoryByPackageSupport {
          val response = await(repoDeleteCacheCategoryByPackage(createDeleteCacheCategoryByPackageRequest))

          response.deleted shouldEqual 1
        }

    "fetchCacheCategories should return all the cache categories stored in the database" in
        new FetchCacheCategoriesSupport {
          val response = await(repoFetchCacheCategories(createFetchCacheCategoriesRequest))

          response.cacheCategories shouldEqual cacheCategorySeq
        }

    "findCacheCategoryById should return a CacheCategory object when a existing id is given" in
        new FindCacheCategoryByIdSupport {
          val response = await(repoFindCacheCategoryById(createFindCacheCategoryByIdRequest(id = cacheCategoryId)))

          response.category.get.id shouldEqual cacheCategoryId
          response.category.get.data.packageName shouldEqual packageName
        }

    "findCacheCategoryById should return None when a non-existing id is given" in
        new FindCacheCategoryByIdSupport {
          val response = await(repoFindCacheCategoryById(createFindCacheCategoryByIdRequest(id = nonExistingCacheCategoryId)))

          response.category shouldEqual None
        }

    "fetchCacheCategoryByPackage should return a CacheCategory object when a existing package name is given" in
        new FetchCacheCategoryByPackageSupport {
          val response = await(repoFetchCacheCategoryByPackage(createFetchCacheCategoryByPackageRequest(packageName = packageName)))

          response.category.get.id shouldEqual cacheCategoryId
          response.category.get.data.packageName shouldEqual packageName
        }

    "fetchCacheCategoryByPackage should return None when a non-existing package name is given" in
        new FetchCacheCategoryByPackageSupport {
          val response = await(repoFetchCacheCategoryByPackage(createFetchCacheCategoryByPackageRequest(packageName = nonExistingPackageName)))

          response.category shouldEqual None
        }

    "updateCacheCategory should return a successful response when the cache category is updated" in
        new UpdateCacheCategorySupport {
          val response = await(repoUpdateCacheCategory(createUpdateCacheCategoryRequest))

          response.updated shouldEqual 1
        }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCacheCategoryMockCursor {
          val result = getEntityFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a CacheCategory object when a cursor with data is given" in
        new CacheCategoryMockCursor {
          val result = getEntityFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual Some(cacheCategoryEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyCacheCategoryMockCursor {
          val result = getListFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getEntityFromCursor should return a CacheCategory sequence when a cursor with data is given" in
        new CacheCategoryMockCursor {
          val result = getListFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual cacheCategoryEntitySeq
        }
  }
}
