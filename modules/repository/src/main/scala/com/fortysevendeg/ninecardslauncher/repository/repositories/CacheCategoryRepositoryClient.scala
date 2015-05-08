package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.provider.NineCardsContentProvider._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCacheCategory
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.CacheCategory
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CacheCategoryRepositoryClient extends DBUtils {

  self: ContentResolverWrapperComponent =>

  implicit val executionContext: ExecutionContext

  def addCacheCategory: Service[AddCacheCategoryRequest, AddCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            PackageName -> request.data.packageName,
            Category -> request.data.category,
            StarRating -> request.data.starRating,
            NumDownloads -> request.data.numDownloads,
            RatingsCount -> request.data.ratingsCount,
            CommentCount -> request.data.commentCount)

          val uri = contentResolverWrapper.insert(
            uri = ContentUriCacheCategory,
            values = values)

          AddCacheCategoryResponse(
            cacheCategory = Some(CacheCategory(
              id = Integer.parseInt(uri.getPathSegments.get(1)),
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCacheCategoryResponse(cacheCategory = None)
        }
      }

  def deleteCacheCategory: Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolverWrapper.delete(
            uri = withAppendedPath(ContentUriCacheCategory, request.cacheCategory.id.toString))

          DeleteCacheCategoryResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCacheCategoryResponse(success = false)
        }
      }

  def deleteCacheByPackageCategory: Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolverWrapper.delete(
            uri = ContentUriCacheCategory,
            where = s"$PackageName = ?",
            whereParams = Array(request.`package`))

          DeleteCacheCategoryByPackageResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCacheCategoryByPackageResponse(success = false)
        }
      }

  def getAllCacheCategories: Service[GetAllCacheCategoriesRequest, GetAllCacheCategoriesResponse] =
    request =>
      tryToFuture {
        Try {
          val maybeCursor: Option[Cursor] = Option(contentResolverWrapper.query(
            uri = ContentUriCacheCategory,
            projection = AllFields))

          maybeCursor match {
            case Some(cursor) =>
              GetAllCacheCategoriesResponse(
                cacheCategories = getListFromCursor(cursor, cacheCategoryEntityFromCursor) map toCacheCategory)
            case _ => GetAllCacheCategoriesResponse(cacheCategories = Seq.empty)
          }

        } recover {
          case e: Exception =>
            GetAllCacheCategoriesResponse(cacheCategories = Seq.empty)
        }
      }

  def getCacheCategoryById: Service[GetCacheCategoryByIdRequest, GetCacheCategoryByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val maybeCursor: Option[Cursor] = Option(contentResolverWrapper.query(
            uri = withAppendedPath(ContentUriCacheCategory, request.id.toString),
            projection = AllFields))

          maybeCursor match {
            case Some(cursor) =>
              GetCacheCategoryByIdResponse(
                result = getEntityFromCursor(cursor, cacheCategoryEntityFromCursor) map toCacheCategory)
            case _ => GetCacheCategoryByIdResponse(result = None)
          }

        } recover {
          case e: Exception =>
            GetCacheCategoryByIdResponse(result = None)
        }
      }

  def getCacheCategoryByPackage: Service[GetCacheCategoryByPackageRequest, GetCacheCategoryByPackageResponse] =
    request =>
      tryToFuture {
        Try {
          val maybeCursor: Option[Cursor] = Option(contentResolverWrapper.query(
            uri = ContentUriCacheCategory,
            projection = AllFields,
            where = s"$PackageName = ?",
            whereParams = Seq(request.`package`)))

          maybeCursor match {
            case Some(cursor) =>
              GetCacheCategoryByPackageResponse(
                result = getEntityFromCursor(cursor, cacheCategoryEntityFromCursor) map toCacheCategory)
            case _ => GetCacheCategoryByPackageResponse(result = None)
          }
        } recover {
          case e: Exception =>
            GetCacheCategoryByPackageResponse(result = None)
        }
      }

  def updateCacheCategory: Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            PackageName -> request.cacheCategory.data.packageName,
            Category -> request.cacheCategory.data.category,
            StarRating -> request.cacheCategory.data.starRating,
            NumDownloads -> request.cacheCategory.data.numDownloads,
            RatingsCount -> request.cacheCategory.data.ratingsCount,
            CommentCount -> request.cacheCategory.data.commentCount)

          contentResolverWrapper.update(
            uri = withAppendedPath(ContentUriCacheCategory, request.cacheCategory.id.toString),
            values = values
          )

          UpdateCacheCategoryResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCacheCategoryResponse(success = false)
        }
      }
}
