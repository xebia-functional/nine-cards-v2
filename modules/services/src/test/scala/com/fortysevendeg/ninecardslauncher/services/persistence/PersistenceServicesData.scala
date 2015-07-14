package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.repository.{model => repositoryModel}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.{GeoInfo, CacheCategory}

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

  val geoInfoId = Random.nextInt(10)
  val nonExistentGeoInfoId = Random.nextInt(10) + 100
  val constrain = Random.nextString(5)
  val nonExistentConstrain = "nonExistentPackageName"
  val occurrence = Random.nextString(5)
  val wifi = Random.nextString(5)
  val longitude = Random.nextDouble()
  val latitude = Random.nextDouble()
  val system = Random.nextBoolean

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

  def createSeqGeoInfo(
    num: Int = 5,
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = (0 until 5) map (item => GeoInfo(
    id = id + item,
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system))

  def createSeqRepoGeoInfo(
    num: Int = 5,
    id: Int = geoInfoId,
    data: repositoryModel.GeoInfoData = createRepoGeoInfoData()
    ) = (0 until 5) map (item => repositoryModel.GeoInfo(id = id + item, data = data))

  def createRepoGeoInfoData(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = repositoryModel.GeoInfoData(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system)

  val seqCacheCategory = createSeqCacheCategory()
  val cacheCategory = seqCacheCategory.head
  val repoCacheCategoryData = createRepoCacheCategoryData()
  val seqRepoCacheCategory = createSeqRepoCacheCategory(data = repoCacheCategoryData)
  val repoCacheCategory = seqRepoCacheCategory.head

  val seqGeoInfo = createSeqGeoInfo()
  val geoInfo = seqGeoInfo.head
  val repoGeoInfoData = createRepoGeoInfoData()
  val seqRepoGeoInfo = createSeqRepoGeoInfo(data = repoGeoInfoData)
  val repoGeoInfo = seqRepoGeoInfo.head

  def createAddCacheCategoryRequest(
    packageName: String = packageName,
    category: String = category,
    starRating: Double = starRating,
    numDownloads: String = numDownloads,
    ratingsCount: Int = ratingsCount,
    commentCount: Int = commentCount) = AddCacheCategoryRequest(
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

  def createAddGeoInfoRequest(
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = AddGeoInfoRequest(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system)

  def createDeleteGeoInfoRequest(geoInfo: GeoInfo) = DeleteGeoInfoRequest(
    geoInfo = geoInfo)

  def createFetchGeoInfoByConstrainRequest(constrain: String) = FetchGeoInfoByConstrainRequest(
    constrain = constrain)

  def createFindGeoInfoByIdRequest(id: Int) = FindGeoInfoByIdRequest(
    id = id)

  def createUpdateGeoInfoRequest(
    id: Int = geoInfoId,
    constrain: String = constrain,
    occurrence: String = occurrence,
    wifi: String = wifi,
    longitude: Double = longitude,
    latitude: Double = latitude,
    system: Boolean = system) = UpdateGeoInfoRequest(
    id = id,
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    longitude = longitude,
    latitude = latitude,
    system = system)
}
