package com.fortysevendeg.ninecardslauncher.modules.repository.cacheCategory

import com.fortysevendeg.ninecardslauncher.models.CacheCategory

case class AddCacheCategoryRequest(
    packageName: String,
    category: String,
    starRating: Double,
    numDownloads: String,
    ratingsCount: Int,
    commentCount: Int)

case class AddCacheCategoryResponse(
    cacheCategory: CacheCategory)

case class DeleteCacheCategoryRequest(cacheCategory: CacheCategory)

case class DeleteCacheCategoryResponse(deleted: Int)

case class DeleteCacheCategoryByPackageRequest(`package`: String)

case class DeleteCacheCategoryByPackageResponse(deleted: Int)

case class FetchCacheCategoriesRequest()

case class FetchCacheCategoriesResponse(cacheCategories: Seq[CacheCategory])

case class FindCacheCategoryByIdRequest(id: Int)

case class FindCacheCategoryByIdResponse(category: Option[CacheCategory])

case class FetchCacheCategoryByPackageRequest(`package`: String)

case class FetchCacheCategoryByPackageResponse(category: Option[CacheCategory])

case class UpdateCacheCategoryRequest(
    id: Int, packageName: String,
    category: String,
    starRating: Double,
    numDownloads: String,
    ratingsCount: Int,
    commentCount: Int)

case class UpdateCacheCategoryResponse(updated: Int)
