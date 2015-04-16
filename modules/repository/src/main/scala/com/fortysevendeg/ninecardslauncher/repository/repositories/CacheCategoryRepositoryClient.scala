package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.commons.RichContentValues._
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._
import com.fortysevendeg.ninecardslauncher.provider.{DBUtils, NineCardsContentProvider}
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toCacheCategory
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.CacheCategory
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait CacheCategoryRepositoryClient extends DBUtils {

  self: ContentResolverProvider =>

  implicit val executionContext: ExecutionContext

  def addCacheCategory(): Service[AddCacheCategoryRequest, AddCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(PackageName, request.data.packageName)
          contentValues.put(Category, request.data.category)
          contentValues.put(StarRating, request.data.starRating)
          contentValues.put(NumDownloads, request.data.numDownloads)
          contentValues.put(RatingsCount, request.data.ratingsCount)
          contentValues.put(CommentCount, request.data.commentCount)

          val uri = contentResolver.insert(
            NineCardsContentProvider.ContentUriCacheCategory,
            contentValues)

          AddCacheCategoryResponse(
            cacheCategory = Some(CacheCategory(
              id = Integer.parseInt(uri.getPathSegments.get(1)),
              data = request.data)))

        } recover {
          case e: Exception =>
            AddCacheCategoryResponse(cacheCategory = None)
        }
      }

  def deleteCacheCategory(): Service[DeleteCacheCategoryRequest, DeleteCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolver.delete(
            withAppendedPath(NineCardsContentProvider.ContentUriCacheCategory, request.cacheCategory.id.toString),
            "",
            Array.empty)

          DeleteCacheCategoryResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCacheCategoryResponse(success = false)
        }
      }


  def deleteCacheByPackageCategory(): Service[DeleteCacheCategoryByPackageRequest, DeleteCacheCategoryByPackageResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolver.delete(
            NineCardsContentProvider.ContentUriCacheCategory,
            s"$PackageName = ?",
            Array(request.`package`))

          DeleteCacheCategoryByPackageResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteCacheCategoryByPackageResponse(success = false)
        }
      }

  def getCacheCategoryById: Service[GetCacheCategoryByIdRequest, GetCacheCategoryByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val maybeCursor: Option[Cursor] = Option(contentResolver.query(
            withAppendedPath(NineCardsContentProvider.ContentUriCacheCategory, request.id.toString),
            Array.empty,
            "",
            Array.empty,
            ""))

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
          val maybeCursor: Option[Cursor] = Option(contentResolver.query(
            NineCardsContentProvider.ContentUriCacheCategory,
            AllFields,
            s"$PackageName = ?",
            Array(request.`package`),
            ""))

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

  def updateCacheCategory(): Service[UpdateCacheCategoryRequest, UpdateCacheCategoryResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(PackageName, request.cacheCategory.data.packageName)
          contentValues.put(Category, request.cacheCategory.data.category)
          contentValues.put(StarRating, request.cacheCategory.data.starRating)
          contentValues.put(NumDownloads, request.cacheCategory.data.numDownloads)
          contentValues.put(RatingsCount, request.cacheCategory.data.ratingsCount)
          contentValues.put(CommentCount, request.cacheCategory.data.commentCount)

          contentResolver.update(
            withAppendedPath(NineCardsContentProvider.ContentUriCacheCategory, request.cacheCategory.id.toString),
            contentValues,
            "",
            Array.empty)

          UpdateCacheCategoryResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateCacheCategoryResponse(success = false)
        }
      }
}
