package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.provider.AppEntity._

case class AppEntity(id: Int, data: AppEntityData)

case class AppEntityData(
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  colorPrimary: Int,
  dateInstalled: Int,
  dateUpdate: Int,
  version: String,
  installedFromGooglePlay: Boolean)

object AppEntity {
  val table = "app"
  val name = "name"
  val packageName = "packageName"
  val className = "className"
  val category = "category"
  val imagePath = "imagePath"
  val colorPrimary = "colorPrimary"
  val dateInstalled = "dateInstalled"
  val dateUpdate = "dateUpdate"
  val version = "version"
  val installedFromGooglePlay = "installedFromGooglePlay"


  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    name,
    packageName,
    className,
    category,
    imagePath,
    colorPrimary,
    dateInstalled,
    dateUpdate,
    version,
    installedFromGooglePlay)

  def AppEntityFromCursor(cursor: Cursor) =
    AppEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = AppEntityData(
        name = cursor.getString(cursor.getColumnIndex(name)),
        packageName = cursor.getString(cursor.getColumnIndex(packageName)),
        className = cursor.getString(cursor.getColumnIndex(className)),
        category = cursor.getString(cursor.getColumnIndex(category)),
        imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
        colorPrimary = cursor.getInt(cursor.getColumnIndex(colorPrimary)),
        dateInstalled = cursor.getInt(cursor.getColumnIndex(dateInstalled)),
        dateUpdate = cursor.getInt(cursor.getColumnIndex(dateUpdate)),
        version = cursor.getString(cursor.getColumnIndex(version)),
        installedFromGooglePlay = cursor.getInt(cursor.getColumnIndex(installedFromGooglePlay)) > 0))
}

object AppEntityData {

  def AppEntityDataFromCursor(cursor: Cursor) =
    AppEntityData(
      name = cursor.getString(cursor.getColumnIndex(name)),
      packageName = cursor.getString(cursor.getColumnIndex(packageName)),
      className = cursor.getString(cursor.getColumnIndex(className)),
      category = cursor.getString(cursor.getColumnIndex(category)),
      imagePath = cursor.getString(cursor.getColumnIndex(imagePath)),
      colorPrimary = cursor.getInt(cursor.getColumnIndex(colorPrimary)),
      dateInstalled = cursor.getInt(cursor.getColumnIndex(dateInstalled)),
      dateUpdate = cursor.getInt(cursor.getColumnIndex(dateUpdate)),
      version = cursor.getString(cursor.getColumnIndex(version)),
      installedFromGooglePlay = cursor.getInt(cursor.getColumnIndex(installedFromGooglePlay)) > 0)
}