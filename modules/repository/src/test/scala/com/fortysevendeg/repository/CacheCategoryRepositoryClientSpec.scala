package com.fortysevendeg.repository

import com.fortysevendeg.ninecardslauncher.commons.CacheCategoryUri
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider._
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, CacheCategoryData}
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import org.mockito.Mockito._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

trait CacheCategoryMockCursor extends MockCursor with CacheCategoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, cacheCategorySeq map (_.id), IntDataType),
    (PackageName, 1, cacheCategorySeq map (_.data.packageName), StringDataType),
    (Category, 2, cacheCategorySeq map (_.data.category), StringDataType),
    (StarRating, 3, cacheCategorySeq map (_.data.starRating), DoubleDataType),
    (NumDownloads, 4, cacheCategorySeq map (_.data.numDownloads), StringDataType),
    (RatingsCount, 5, cacheCategorySeq map (_.data.ratingsCount), IntDataType),
    (CommentCount, 6, cacheCategorySeq map (_.data.commentCount), IntDataType)
  )

  prepareCursor[CacheCategory](cacheCategorySeq.size, cursorData)
}

trait EmptyCacheCategoryMockCursor extends MockCursor with CacheCategoryTestData {

  val cursorData = Seq(
    (NineCardsSqlHelper.Id, 0, Seq.empty, IntDataType),
    (PackageName, 1, Seq.empty, StringDataType),
    (Category, 2, Seq.empty, StringDataType),
    (StarRating, 3, Seq.empty, DoubleDataType),
    (NumDownloads, 4, Seq.empty, StringDataType),
    (RatingsCount, 5, Seq.empty, IntDataType),
    (CommentCount, 6, Seq.empty, IntDataType)
  )

  prepareCursor[CacheCategory](0, cursorData)
}

trait CacheCategoryTestData {
  val cacheCategoryId = Random.nextInt(10)
  val nonExistingCacheCategoryId = 15
  val packageName = Random.nextString(5)
  val nonExistingPackageName = Random.nextString(5)
  val category = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val ratingsCount = Random.nextInt(1)
  val commentCount = Random.nextInt(1)

  val cacheCategoryEntitySeq = createCacheCategoryEntitySeq(5)
  val cacheCategoryEntity = cacheCategoryEntitySeq.head
  val cacheCategorySeq = createCacheCategorySeq(5)
  val cacheCategory = cacheCategorySeq.head

  def createCacheCategoryEntitySeq(num: Int) = (0 until num) map (i => CacheCategoryEntity(
    id = cacheCategoryId + i,
    data = CacheCategoryEntityData(
      packageName = packageName,
      category = category,
      starRating = starRating,
      numDownloads = numDownloads,
      ratingsCount = ratingsCount,
      commentCount = commentCount)))

  def createCacheCategorySeq(num: Int) = (0 until num) map (i => CacheCategory(
    id = cacheCategoryId + i,
    data = CacheCategoryData(
      packageName = packageName,
      category = category,
      starRating = starRating,
      numDownloads = numDownloads,
      ratingsCount = ratingsCount,
      commentCount = commentCount)))

  def createCacheCategoryValues = Map[String, Any](
    PackageName -> packageName,
    Category -> category,
    StarRating -> starRating,
    NumDownloads -> numDownloads,
    RatingsCount -> ratingsCount,
    CommentCount -> commentCount)
}

trait CacheCategoryTestSupport
    extends BaseTestSupport
    with MockContentResolverWrapper
    with CacheCategoryTestData
    with DBUtils {

  def createAddCacheCategoryRequest = AddCacheCategoryRequest(CacheCategoryData(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount))

  def createDeleteCacheCategoryRequest = DeleteCacheCategoryRequest(cacheCategory = cacheCategory)

  def createDeleteCacheCategoryByPackageRequest = DeleteCacheCategoryByPackageRequest(`package` = packageName)

  def createGetAllCacheCategoriesRequest = FetchCacheCategoriesRequest()

  def createGetCacheCategoryByIdRequest(id: Int) = FindCacheCategoryByIdRequest(id = id)

  def createGetCacheCategoriesByPackage(packageName: String) = FetchCacheCategoryByPackageRequest(`package` = packageName)

  def createUpdateCacheCategoryRequest = UpdateCacheCategoryRequest(cacheCategory = cacheCategory)

  when(contentResolverWrapper.insert(CacheCategoryUri, createCacheCategoryValues)).thenReturn(cacheCategoryId)

  when(contentResolverWrapper.deleteById(CacheCategoryUri, cacheCategoryId)).thenReturn(1)

  when(contentResolverWrapper.delete(CacheCategoryUri, where = s"$PackageName = ?", whereParams = Seq(packageName))).thenReturn(1)

  when(contentResolverWrapper.findById(
    nineCardsUri = CacheCategoryUri,
    id = cacheCategoryId,
    projection = AllFields)(
        f = getEntityFromCursor(cacheCategoryEntityFromCursor))).thenReturn(Some(cacheCategoryEntity))

  when(contentResolverWrapper.findById(
    nineCardsUri = CacheCategoryUri,
    id = nonExistingCacheCategoryId,
    projection = AllFields)(
        f = getEntityFromCursor(cacheCategoryEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CacheCategoryUri,
    projection = AllFields)(
        f = getListFromCursor(cacheCategoryEntityFromCursor))).thenReturn(cacheCategoryEntitySeq)

  when(contentResolverWrapper.fetch(
    nineCardsUri = CacheCategoryUri,
    projection = AllFields,
    where = s"$PackageName = ?",
    whereParams = Seq(packageName))(
        f = getEntityFromCursor(cacheCategoryEntityFromCursor))).thenReturn(Some(cacheCategoryEntity))

  when(contentResolverWrapper.fetch(
    nineCardsUri = CacheCategoryUri,
    projection = AllFields,
    where = s"$PackageName = ?",
    whereParams = Seq(nonExistingPackageName))(
        f = getEntityFromCursor(cacheCategoryEntityFromCursor))).thenReturn(None)

  when(contentResolverWrapper.updateById(CacheCategoryUri, cacheCategoryId, createCacheCategoryValues)).thenReturn(1)
}

class CacheCategoryRepositoryClientSpec
    extends Specification
    with Mockito
    with CacheCategoryTestSupport
    with CacheCategoryRepositoryClient {

  "CacheCategoryRepositoryClient component" should {

    "addCacheCategory should return a valid CacheCategory object" in {

      val result = await(addCacheCategory(createAddCacheCategoryRequest))

      result.cacheCategory.get.id shouldEqual cacheCategoryId
      result.cacheCategory.get.data.packageName shouldEqual packageName
    }

    "deleteCacheCategory should return a successful response when a valid cache category id is given" in {
      val response = await(deleteCacheCategory(createDeleteCacheCategoryRequest))

      response.deleted shouldEqual 1
    }

    "deleteCacheCategoryByPackage should return a successful response when a valid package name is given" in {
      val response = await(deleteCacheByPackageCategory(createDeleteCacheCategoryByPackageRequest))

      response.deleted shouldEqual 1
    }

    "getAllCacheCategories should return all the cache categories stored in the database" in {
      val response = await(fetchCacheCategories(createGetAllCacheCategoriesRequest))

      response.cacheCategories shouldEqual cacheCategorySeq
    }

    "getCacheCategoryById should return a CacheCategory object when a existing id is given" in {
      val response = await(getCacheCategoryById(createGetCacheCategoryByIdRequest(id = cacheCategoryId)))

      response.category.get.id shouldEqual cacheCategoryId
      response.category.get.data.packageName shouldEqual packageName
    }

    "getCacheCategoryById should return None when a non-existing id is given" in {
      val response = await(getCacheCategoryById(createGetCacheCategoryByIdRequest(id = nonExistingCacheCategoryId)))

      response.category shouldEqual None
    }

    "getCacheCategoryByPackage should return a CacheCategory object when a existing package name is given" in {
      val response = await(getCacheCategoryByPackage(createGetCacheCategoriesByPackage(packageName = packageName)))

      response.category.get.id shouldEqual cacheCategoryId
      response.category.get.data.packageName shouldEqual packageName
    }

    "getCacheCategoryByPackage should return None when a non-existing package name is given" in {
      val response = await(getCacheCategoryByPackage(createGetCacheCategoriesByPackage(packageName = nonExistingPackageName)))

      response.category shouldEqual None
    }

    "updateCacheCategory should return a successful response when the cache category is updated" in {
      val response = await(updateCacheCategory(createUpdateCacheCategoryRequest))

      response.updated shouldEqual 1
    }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCacheCategoryMockCursor
            with Scope {
          val result = getEntityFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a CacheCategory object when a cursor with data is given" in
        new CacheCategoryMockCursor
            with Scope {
          val result = getEntityFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual Some(cacheCategoryEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyCacheCategoryMockCursor
            with Scope {
          val result = getListFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getEntityFromCursor should return a CacheCategory sequence when a cursor with data is given" in
        new CacheCategoryMockCursor
            with Scope {
          val result = getListFromCursor(cacheCategoryEntityFromCursor)(mockCursor)

          result shouldEqual cacheCategoryEntitySeq
        }
  }
}
