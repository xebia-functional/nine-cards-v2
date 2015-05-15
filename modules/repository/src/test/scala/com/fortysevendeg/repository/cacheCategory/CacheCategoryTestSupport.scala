package com.fortysevendeg.repository.cacheCategory

import com.fortysevendeg.ninecardslauncher.commons.CacheCategoryUri
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import com.fortysevendeg.ninecardslauncher.provider.{CacheCategoryEntity, CacheCategoryEntityData, NineCardsSqlHelper}
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory, CacheCategoryData}
import com.fortysevendeg.ninecardslauncher.repository.repositories.CacheCategoryRepositoryClient
import com.fortysevendeg.repository._
import org.mockito.Mockito._
import org.specs2.specification.Scope

import scala.util.Random

trait CacheCategoryTestSupport
    extends BaseTestSupport
    with CacheCategoryRepositoryClient
    with CacheCategoryTestData
    with MockContentResolverWrapper
    with Scope

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

trait CacheCategoryMockCursor extends MockCursor with CacheCategoryTestData with Scope {

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

trait EmptyCacheCategoryMockCursor extends MockCursor with CacheCategoryTestData with Scope {

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

trait AddCacheCategorySupport extends CacheCategoryTestSupport {

  def createAddCacheCategoryRequest = AddCacheCategoryRequest(CacheCategoryData(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount))

  when(contentResolverWrapper.insert(CacheCategoryUri, createCacheCategoryValues)).thenReturn(cacheCategoryId)
}

trait DeleteCacheCategorySupport extends CacheCategoryTestSupport {

  def createDeleteCacheCategoryRequest = DeleteCacheCategoryRequest(cacheCategory = cacheCategory)

  when(contentResolverWrapper.deleteById(CacheCategoryUri, cacheCategoryId)).thenReturn(1)
}

trait DeleteCacheCategoryByPackageSupport extends CacheCategoryTestSupport {

  def createDeleteCacheCategoryByPackageRequest = DeleteCacheCategoryByPackageRequest(`package` = packageName)

  when(contentResolverWrapper.delete(CacheCategoryUri, where = s"$PackageName = ?", whereParams = Seq(packageName))).thenReturn(1)
}

trait FetchCacheCategoriesSupport extends CacheCategoryTestSupport {

  def createFetchCacheCategoriesRequest = FetchCacheCategoriesRequest()

  when(contentResolverWrapper.fetchAll(
    nineCardsUri = CacheCategoryUri,
    projection = AllFields)(
        f = getListFromCursor(cacheCategoryEntityFromCursor))).thenReturn(cacheCategoryEntitySeq)
}

trait FindCacheCategoryByIdSupport extends CacheCategoryTestSupport {

  def createFindCacheCategoryByIdRequest(id: Int) = FindCacheCategoryByIdRequest(id = id)

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
}

trait FetchCacheCategoryByPackageSupport extends CacheCategoryTestSupport {

  def createFetchCacheCategoryByPackageRequest(packageName: String) = FetchCacheCategoryByPackageRequest(`package` = packageName)

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
}

trait UpdateCacheCategorySupport extends CacheCategoryTestSupport {

  def createUpdateCacheCategoryRequest = UpdateCacheCategoryRequest(cacheCategory = cacheCategory)

  when(contentResolverWrapper.updateById(CacheCategoryUri, cacheCategoryId, createCacheCategoryValues)).thenReturn(1)
}