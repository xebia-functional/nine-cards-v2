package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.repository.{model => repositoryModel}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory

import scala.util.Random

trait PersistenceServicesData {

  val cacheCategoryId = Random.nextInt(10)
  val nonExistentCacheCategoryId = Random.nextInt(10) + 100
  val packageName = Random.nextString(5)
  val nonExistentPackageName = "nonExistentPackageName"
  val category = Random.nextString(5)
  val starRating = Random.nextDouble()
  val numDownloads = Random.nextString(5)
  val ratingsCount = Random.nextInt(10)
  val commentCount = Random.nextInt(10)

  def createSeqCacheCategory(
    num: Int = 5,
    id: Int = cacheCategoryId,
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = (0 until 5) map (item => CacheCategory(
    id = id + item,
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount))

  def createSeqRepoCacheCategory(
    num: Int = 5,
    id: Int = cacheCategoryId,
    data: repositoryModel.CacheCategoryData = createRepoCacheCategoryData()
    ) = (0 until 5) map (item => repositoryModel.CacheCategory(id = id + item, data = data))

  def createRepoCacheCategoryData(
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = repositoryModel.CacheCategoryData(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)

  def createAddCacheCategoryRequest(
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount
    ) = AddCacheCategoryRequest(
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)

  def createDeleteCacheCategoryRequest(cacheCategory: CacheCategory) = DeleteCacheCategoryRequest(
    cacheCategory = cacheCategory)

  def createDeleteCacheCategoryByPackageRequest(packageName: String) = DeleteCacheCategoryByPackageRequest(
    packageName = packageName)

  def createFetchCacheCategoryByPackageRequest(packageName: String) = FetchCacheCategoryByPackageRequest(
    packageName = packageName)

  def createFindCacheCategoryByIdRequest(id: Int) = FindCacheCategoryByIdRequest(
    id = id)

  def createUpdateCacheCategoryRequest(
    id: Int = cacheCategoryId,
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = UpdateCacheCategoryRequest(
    id = id,
    packageName = packageName,
    category = category,
    starRating = starRating,
    numDownloads = numDownloads,
    ratingsCount = ratingsCount,
    commentCount = commentCount)
}
