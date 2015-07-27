package com.fortysevendeg.repository.cachecategory

import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategoryData, CacheCategory}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CacheCategoryEntityData, CacheCategoryEntity}

import scala.util.Random

trait CacheCategoryRepositoryTestData {

  val testCacheCategoryId = Random.nextInt(10)
  val testNonExistingCacheCategoryId = 15
  val testPackageName = Random.nextString(5)
  val testNonExistingPackageName = Random.nextString(5)
  val testCategory = Random.nextString(5)
  val testStarRating = Random.nextDouble()
  val testNumDownloads = Random.nextString(5)
  val testRatingsCount = Random.nextInt(1)
  val testCommentCount = Random.nextInt(1)

  val cacheCategoryEntitySeq = createCacheCategoryEntitySeq(5)
  val cacheCategoryEntity = cacheCategoryEntitySeq.head
  val cacheCategorySeq = createCacheCategorySeq(5)
  val cacheCategory = cacheCategorySeq.head

  def createCacheCategoryEntitySeq(num: Int) = List.tabulate(num)(
    i => CacheCategoryEntity(
      id = testCacheCategoryId + i,
      data = CacheCategoryEntityData(
        packageName = testPackageName,
        category = testCategory,
        starRating = testStarRating,
        numDownloads = testNumDownloads,
        ratingsCount = testRatingsCount,
        commentCount = testCommentCount)))

  def createCacheCategorySeq(num: Int) = List.tabulate(num)(
    i => CacheCategory(
      id = testCacheCategoryId + i,
      data = CacheCategoryData(
        packageName = testPackageName,
        category = testCategory,
        starRating = testStarRating,
        numDownloads = testNumDownloads,
        ratingsCount = testRatingsCount,
        commentCount = testCommentCount)))

  def createCacheCategoryValues = Map[String, Any](
    CacheCategoryEntity.packageName -> testPackageName,
    CacheCategoryEntity.category -> testCategory,
    CacheCategoryEntity.starRating -> testStarRating,
    CacheCategoryEntity.numDownloads -> testNumDownloads,
    CacheCategoryEntity.ratingsCount -> testRatingsCount,
    CacheCategoryEntity.commentCount -> testCommentCount)

  def createCacheCategoryData = CacheCategoryData(
    packageName = testPackageName,
    category = testCategory,
    starRating = testStarRating,
    numDownloads = testNumDownloads,
    ratingsCount = testRatingsCount,
    commentCount = testCommentCount)
}
