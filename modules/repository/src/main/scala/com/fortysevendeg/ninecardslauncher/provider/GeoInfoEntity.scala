package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._

case class GeoInfoEntity(id: Int, data: GeoInfoEntityData)

case class GeoInfoEntityData(
    constrain: String,
    occurrence: String,
    wifi: String,
    latitude: Double,
    longitude: Double,
    system: Boolean)

object GeoInfoEntity {
  val Table = "GeoInfo"
  val Constrain = "constrain"
  val Occurrence = "occurrence"
  val Wifi = "wifi"
  val Latitude = "latitude"
  val Longitude = "longitude"
  val System = "system"

  val AllFields = Array[String](
    NineCardsSqlHelper.Id,
    Constrain,
    Occurrence,
    Wifi,
    Latitude,
    Longitude,
    System)

  def geoInfoEntityFromCursor(cursor: Cursor) =
    GeoInfoEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.Id)),
      data = GeoInfoEntityData(
        constrain = cursor.getString(cursor.getColumnIndex(Constrain)),
        occurrence = cursor.getString(cursor.getColumnIndex(Occurrence)),
        wifi = cursor.getString(cursor.getColumnIndex(Wifi)),
        latitude = cursor.getDouble(cursor.getColumnIndex(Latitude)),
        longitude = cursor.getDouble(cursor.getColumnIndex(Longitude)),
        system = cursor.getInt(cursor.getColumnIndex(System)) > 0))
}

object GeoInfoEntityData {

  def geoInfoEntityDataFromCursor(cursor: Cursor) =
    GeoInfoEntityData(
      constrain = cursor.getString(cursor.getColumnIndex(Constrain)),
      occurrence = cursor.getString(cursor.getColumnIndex(Occurrence)),
      wifi = cursor.getString(cursor.getColumnIndex(Wifi)),
      latitude = cursor.getDouble(cursor.getColumnIndex(Latitude)),
      longitude = cursor.getDouble(cursor.getColumnIndex(Longitude)),
      system = cursor.getInt(cursor.getColumnIndex(System)) > 0)
}