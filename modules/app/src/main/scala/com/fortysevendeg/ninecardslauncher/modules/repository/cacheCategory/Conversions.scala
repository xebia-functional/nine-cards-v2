package com.fortysevendeg.ninecardslauncher.modules.repository.cacheCategory

import com.fortysevendeg.ninecardslauncher.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.repository.{AddCacheCategoryRequest => RepositoryAddCacheCategoryRequest}
import com.fortysevendeg.ninecardslauncher.repository.{DeleteCacheCategoryRequest => RepositoryDeleteCacheCategoryRequest}
import com.fortysevendeg.ninecardslauncher.repository.{DeleteCacheCategoryByPackageRequest => RepositoryDeleteCacheCategoryByPackageRequest}
import com.fortysevendeg.ninecardslauncher.repository.{FetchCacheCategoryByPackageRequest => RepositoryFetchCacheCategoryByPackageRequest}
import com.fortysevendeg.ninecardslauncher.repository.{FindCacheCategoryByIdRequest => RepositoryFindCacheCategoryByIdRequest}
import com.fortysevendeg.ninecardslauncher.repository.{UpdateCacheCategoryRequest => RepositoryUpdateCacheCategoryRequest}
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategory => RepositoryCacheCategory}
import com.fortysevendeg.ninecardslauncher.repository.model.{CacheCategoryData => RepositoryCacheCategoryData}

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
    RepositoryAddCacheCategoryRequest(
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
    RepositoryDeleteCacheCategoryRequest(
      cacheCategory = toRepositoryCacheCategory(request.cacheCategory)
    )

  def toRepositoryDeleteCacheCategoryByPackageRequest(request: DeleteCacheCategoryByPackageRequest) =
    RepositoryDeleteCacheCategoryByPackageRequest(
      `package` = request.`package`)

  def toRepositoryFetchCacheCategoryByPackageRequest(request: FetchCacheCategoryByPackageRequest) =
    RepositoryFetchCacheCategoryByPackageRequest(
      `package` = request.`package`)

  def toRepositoryFindCacheCategoryByIdRequest(request: FindCacheCategoryByIdRequest) =
    RepositoryFindCacheCategoryByIdRequest(
      id = request.id)

  def toRepositoryUpdateCacheCategoryRequest(request: UpdateCacheCategoryRequest) =
    RepositoryUpdateCacheCategoryRequest(
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
