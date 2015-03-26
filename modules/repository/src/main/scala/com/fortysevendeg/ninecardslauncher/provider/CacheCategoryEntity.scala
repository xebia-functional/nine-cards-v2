package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor

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

  def cacheCategoryEntityFromCursor(cursor: Cursor) = {
    CacheCategoryEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = CacheCategoryEntityData(
        packageName = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.PackageName)),
        category = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.Category)),
        starRating = cursor.getDouble(cursor.getColumnIndex(CacheCategoryEntity.StarRating)),
        numDownloads = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.NumDownloads)),
        ratingsCount = cursor.getInt(cursor.getColumnIndex(CacheCategoryEntity.RatingsCount)),
        commentCount = cursor.getInt(cursor.getColumnIndex(CacheCategoryEntity.CommentCount))))
  }
}

object CacheCategoryEntityData {

  def cacheCategoryEntityDataFromCursor(cursor: Cursor) = {
    CacheCategoryEntityData(
      packageName = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.PackageName)),
      category = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.Category)),
      starRating = cursor.getDouble(cursor.getColumnIndex(CacheCategoryEntity.StarRating)),
      numDownloads = cursor.getString(cursor.getColumnIndex(CacheCategoryEntity.NumDownloads)),
      ratingsCount = cursor.getInt(cursor.getColumnIndex(CacheCategoryEntity.RatingsCount)),
      commentCount = cursor.getInt(cursor.getColumnIndex(CacheCategoryEntity.CommentCount)))
  }
}