package com.fortysevendeg.repository.cachecategory

import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategoryData, CacheCategory}
import com.fortysevendeg.ninecardslauncher.repository.provider.{CacheCategoryEntityData, CacheCategoryEntity}

import scala.util.Random

trait CacheCategoryRepositoryTestData {

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
    CacheCategoryEntity.packageName -> packageName,
    CacheCategoryEntity.category -> category,
    CacheCategoryEntity.starRating -> starRating,
    CacheCategoryEntity.numDownloads -> numDownloads,
    CacheCategoryEntity.ratingsCount -> ratingsCount,
    CacheCategoryEntity.commentCount -> commentCount)

  def createCacheCategoryData = CacheCategoryData(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)
}
