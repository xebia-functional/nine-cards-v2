package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory => RepoCacheCategory, CacheCategoryData => RepoCacheCategoryData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory

trait CacheCategoryConversions {

  def toCacheCategorySeq(cache: Seq[RepoCacheCategory]): Seq[CacheCategory] = cache map toCacheCategory

  def toCacheCategory(cacheCategory: RepoCacheCategory): CacheCategory =
    CacheCategory(
      id = cacheCategory.id,
      packageName = cacheCategory.data.packageName,
      category = cacheCategory.data.category,
      starRating = cacheCategory.data.starRating,
      numDownloads = cacheCategory.data.numDownloads,
      ratingsCount = cacheCategory.data.ratingsCount,
      commentCount = cacheCategory.data.commentCount)

  def toRepositoryCacheCategory(cacheCategory: CacheCategory): RepoCacheCategory =
    RepoCacheCategory(
      id = cacheCategory.id,
      data = RepoCacheCategoryData(
        packageName = cacheCategory.packageName,
        category = cacheCategory.category,
        starRating = cacheCategory.starRating,
        numDownloads = cacheCategory.numDownloads,
        ratingsCount = cacheCategory.ratingsCount,
        commentCount = cacheCategory.commentCount
      )
    )

  def toRepositoryCacheCategory(request: UpdateCacheCategoryRequest): RepoCacheCategory =
    RepoCacheCategory(
      id = request.id,
      data = RepoCacheCategoryData(
        packageName = request.packageName,
        category = request.category,
        starRating = request.starRating,
        numDownloads = request.numDownloads,
        ratingsCount = request.ratingsCount,
        commentCount = request.commentCount
      )
    )

  def toRepositoryCacheCategoryData(request: AddCacheCategoryRequest): RepoCacheCategoryData =
    RepoCacheCategoryData(
      packageName = request.packageName,
      category = request.category,
      starRating = request.starRating,
      numDownloads = request.numDownloads,
      ratingsCount = request.ratingsCount,
      commentCount = request.commentCount
    )
}
