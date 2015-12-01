package com.fortysevendeg.ninecardslauncher.repository.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.repository.Conversions._

case class GeoInfoEntity(id: Int, data: GeoInfoEntityData)

case class GeoInfoEntityData(
  constrain: String,
  occurrence: String,
  wifi: String,
  latitude: Double,
  longitude: Double,
  system: Boolean)

object GeoInfoEntity {
  val table = "GeoInfo"
  val constrain = "constrain"
  val occurrence = "occurrence"
  val wifi = "wifi"
  val latitude = "latitude"
  val longitude = "longitude"
  val system = "system"

  val allFields = Seq[String](
    NineCardsSqlHelper.id,
    constrain,
    occurrence,
    wifi,
    latitude,
    longitude,
    system)

  def geoInfoEntityFromCursor(cursor: Cursor) =
    GeoInfoEntity(
      id = cursor.getInt(cursor.getColumnIndex(NineCardsSqlHelper.id)),
      data = GeoInfoEntityData(
        constrain = cursor.getString(cursor.getColumnIndex(constrain)),
        occurrence = cursor.getString(cursor.getColumnIndex(occurrence)),
        wifi = cursor.getString(cursor.getColumnIndex(wifi)),
        latitude = cursor.getDouble(cursor.getColumnIndex(latitude)),
        longitude = cursor.getDouble(cursor.getColumnIndex(longitude)),
        system = cursor.getInt(cursor.getColumnIndex(system)) > 0))

  def geoInfoFromCursor(cursor: Cursor) = toGeoInfo(geoInfoEntityFromCursor(cursor))

  def createTableSQL =
    s"""CREATE TABLE ${GeoInfoEntity.table}
       |(${NineCardsSqlHelper.id} INTEGER PRIMARY KEY AUTOINCREMENT,
       |${GeoInfoEntity.constrain} TEXT not null,
       |${GeoInfoEntity.occurrence} TEXT not null,
       |${GeoInfoEntity.wifi} TEXT,
       |${GeoInfoEntity.latitude} DOUBLE,
       |${GeoInfoEntity.longitude} DOUBLE,
       |${GeoInfoEntity.system} INTEGER )""".stripMargin
}
