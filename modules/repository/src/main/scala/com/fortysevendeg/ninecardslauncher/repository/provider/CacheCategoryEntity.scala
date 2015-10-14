package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.provider.CacheCategoryEntity._

case class CacheCategoryEntity(id: Int, data: CacheCategoryEntityData)

case class CacheCategoryEntityData(
  packageName: String,
  category: String,
  starRating: Double,
  numDownloads: String,
  ratingsCount: Int,
  commentCount: Int)

object CacheCategoryEntity {
  val table = "cacheCategory"
  val packageName = "packageName"
  val category = "category"
  val starRating = "starRating"
  val numDownloads = "numDownloads"
  val ratingsCount = "ratingsCount"
  val commentCount = "commentCount"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    packageName,
    category,
    starRating,
    numDownloads,
    ratingsCount,
    commentCount)

  def cacheCategoryEntityFromCursor(cursor: Cursor) =
    CacheCategoryEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = CacheCategoryEntityData(
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        category = cursor.getString(cursor.getColumnIndex(category)),
        starRating = cursor.getDouble(cursor.getColumnIndex(starRating)),
        numDownloads = cursor.getString(cursor.getColumnIndex(numDownloads)),
        ratingsCount = cursor.getInt(cursor.getColumnIndex(ratingsCount)),
        commentCount = cursor.getInt(cursor.getColumnIndex(commentCount))))

  def createTableSQL =
    s"""CREATE TABLE ${CacheCategoryEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${CacheCategoryEntity.packageName} TEXT not null,
       |${CacheCategoryEntity.category} TEXT not null,
       |${CacheCategoryEntity.starRating} DOUBLE,
       |${CacheCategoryEntity.numDownloads} TEXT,
       |${CacheCategoryEntity.ratingsCount} INTEGER,
       |${CacheCategoryEntity.commentCount} INTEGER )""".stripMargin
}

object CacheCategoryEntityData {

  def cacheCategoryEntityDataFromCursor(cursor: Cursor) =
    CacheCategoryEntityData(
      packageName = cursor.getString(cursor.getColumnIndex(packageName)),
      category = cursor.getString(cursor.getColumnIndex(category)),
      starRating = cursor.getDouble(cursor.getColumnIndex(starRating)),
      numDownloads = cursor.getString(cursor.getColumnIndex(numDownloads)),
      ratingsCount = cursor.getInt(cursor.getColumnIndex(ratingsCount)),
      commentCount = cursor.getInt(cursor.getColumnIndex(commentCount)))
}