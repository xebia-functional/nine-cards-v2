package com.fortysevendeg.ninecardslauncher.services.persistence.conversions

import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory => RepoCacheCategory, CacheCategoryData => RepoCacheCategoryData}
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.{repository => repo}

trait CacheCategoryConversions {

  def toCacheCategorySeq(cache: Seq[RepoCacheCategory]) = cache map toCacheCategory

  def toCacheCategory(cacheCategory: RepoCacheCategory) =
    CacheCategory(
      id = cacheCategory.id,
      packageName = cacheCategory.data.packageName,
      category = cacheCategory.data.category,
      starRating = cacheCategory.data.starRating,
      numDownloads = cacheCategory.data.numDownloads,
      ratingsCount = cacheCategory.data.ratingsCount,
      commentCount = cacheCategory.data.commentCount)

  def toRepositoryCacheCategory(cacheCategory: CacheCategory) =
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

  def toAddCacheCategoryRequest(request: AddCacheCategoryRequest) =
    repo.AddCacheCategoryRequest(
      data = RepoCacheCategoryData(
        packageName = request.packageName,
        category = request.category,
        starRating = request.starRating,
        numDownloads = request.numDownloads,
        ratingsCount = request.ratingsCount,
        commentCount = request.commentCount
      )
    )

  def toRepositoryDeleteCacheCategoryRequest(request: DeleteCacheCategoryRequest) =
    repo.DeleteCacheCategoryRequest(
      cacheCategory = toRepositoryCacheCategory(request.cacheCategory)
    )

  def toRepositoryDeleteCacheCategoryByPackageRequest(request: DeleteCacheCategoryByPackageRequest) =
    repo.DeleteCacheCategoryByPackageRequest(
      packageName = request.`package`)

  def toRepositoryFetchCacheCategoryByPackageRequest(request: FetchCacheCategoryByPackageRequest) =
    repo.FetchCacheCategoryByPackageRequest(
      `package` = request.`package`)

  def toRepositoryFindCacheCategoryByIdRequest(request: FindCacheCategoryByIdRequest) =
    repo.FindCacheCategoryByIdRequest(
      id = request.id)

  def toRepositoryUpdateCacheCategoryRequest(request: UpdateCacheCategoryRequest) =
    repo.UpdateCacheCategoryRequest(
      cacheCategory = RepoCacheCategory(
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
    )
}
