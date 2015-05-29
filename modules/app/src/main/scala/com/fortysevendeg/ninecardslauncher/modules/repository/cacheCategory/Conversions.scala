package com.fortysevendeg.ninecardslauncher.modules.repository.cacheCategory

import com.fortysevendeg.ninecardslauncher.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory => RepositoryCacheCategory, CacheCategoryData => RepositoryCacheCategoryData}
import com.fortysevendeg.ninecardslauncher.{repository => repo}

trait Conversions {

  def toCacheCategorySeq(cache: Seq[RepositoryCacheCategory]) = cache map toCacheCategory

  def toCacheCategory(cacheCategory: RepositoryCacheCategory) =
    CacheCategory(
      id = cacheCategory.id,
      packageName = cacheCategory.data.packageName,
      category = cacheCategory.data.category,
      starRating = cacheCategory.data.starRating,
      numDownloads = cacheCategory.data.numDownloads,
      ratingsCount = cacheCategory.data.ratingsCount,
      commentCount = cacheCategory.data.commentCount)

  def toRepositoryCacheCategory(cacheCategory: CacheCategory) =
    RepositoryCacheCategory(
      id = cacheCategory.id,
      data = RepositoryCacheCategoryData(
        packageName = cacheCategory.packageName,
        category = cacheCategory.category,
        starRating = cacheCategory.starRating,
        numDownloads = cacheCategory.numDownloads,
        ratingsCount = cacheCategory.ratingsCount,
        commentCount = cacheCategory.commentCount
      )
    )

  def toRepositoryAddCacheCategoryRequest(request: AddCacheCategoryRequest) =
    repo.AddCacheCategoryRequest(
      data = RepositoryCacheCategoryData(
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
      `package` = request.`package`)

  def toRepositoryFetchCacheCategoryByPackageRequest(request: FetchCacheCategoryByPackageRequest) =
    repo.FetchCacheCategoryByPackageRequest(
      `package` = request.`package`)

  def toRepositoryFindCacheCategoryByIdRequest(request: FindCacheCategoryByIdRequest) =
    repo.FindCacheCategoryByIdRequest(
      id = request.id)

  def toRepositoryUpdateCacheCategoryRequest(request: UpdateCacheCategoryRequest) =
    repo.UpdateCacheCategoryRequest(
      cacheCategory = RepositoryCacheCategory(
        id = request.id,
        data = RepositoryCacheCategoryData(
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
