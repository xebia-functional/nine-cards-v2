package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.provider.CacheCategoryEntity._

case class CacheCategoryEntity(id: Int, data: CacheCategoryEntityData)

case class CacheCategoryEntityData(
    packageName: String,
    category: String,
    starRating: Double,
    numDownloads: String,
    ratingsCount: Int,
    commentCount: Int)

object CacheCategoryEntity {
  val Table = "cacheCategory"
  val PackageName = "packageName"
  val Category = "category"
  val StarRating = "starRating"
  val NumDownloads = "numDownloads"
  val RatingsCount = "ratingsCount"
  val CommentCount = "commentCount"

  val AllFields = Seq[String](
    NineCardsSqlHelper.Id,
    PackageName,
    Category,
    StarRating,
    NumDownloads,
    RatingsCount,
    CommentCount)

  def cacheCategoryEntityFromCursor(cursor: Cursor) =
    CacheCategoryEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = CacheCategoryEntityData(
        packageName = cursor.getString(cursor.getColumnIndex(PackageName)),
        category = cursor.getString(cursor.getColumnIndex(Category)),
        starRating = cursor.getDouble(cursor.getColumnIndex(StarRating)),
        numDownloads = cursor.getString(cursor.getColumnIndex(NumDownloads)),
        ratingsCount = cursor.getInt(cursor.getColumnIndex(RatingsCount)),
        commentCount = cursor.getInt(cursor.getColumnIndex(CommentCount))))
}

object CacheCategoryEntityData {

  def cacheCategoryEntityDataFromCursor(cursor: Cursor) =
    CacheCategoryEntityData(
      packageName = cursor.getString(cursor.getColumnIndex(PackageName)),
      category = cursor.getString(cursor.getColumnIndex(Category)),
      starRating = cursor.getDouble(cursor.getColumnIndex(StarRating)),
      numDownloads = cursor.getString(cursor.getColumnIndex(NumDownloads)),
      ratingsCount = cursor.getInt(cursor.getColumnIndex(RatingsCount)),
      commentCount = cursor.getInt(cursor.getColumnIndex(CommentCount)))
}