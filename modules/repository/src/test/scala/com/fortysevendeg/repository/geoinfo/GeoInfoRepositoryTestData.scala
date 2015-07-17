package com.fortysevendeg.repository.geoinfo

import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfoData, GeoInfo}
import com.fortysevendeg.ninecardslauncher.repository.provider.{GeoInfoEntityData, GeoInfoEntity}

import scala.util.Random

trait GeoInfoRepositoryTestData {

  val geoInfoId = Random.nextInt(10)
  val nonExistingGeoInfoId = 15
  val constrain = Random.nextString(5)
  val nonExistingConstrain = Random.nextString(5)
  val occurrence = Random.nextString(5)
  val wifi = Random.nextString(5)
  val latitude = Random.nextDouble()
  val longitude = Random.nextDouble()
  val system = Random.nextInt(10) < 5

  val geoInfoEntitySeq = createGeoInfoEntitySeq(5)
  val geoInfoEntity = geoInfoEntitySeq.head
  val geoInfoSeq = createGeoInfoSeq(5)
  val geoInfo = geoInfoSeq.head

  def createGeoInfoEntitySeq(num: Int) = (0 until num) map (i => GeoInfoEntity(
    id = geoInfoId + i,
    data = GeoInfoEntityData(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      latitude = latitude,
      longitude = longitude,
      system = system)))

  def createGeoInfoSeq(num: Int) = (0 until num) map (i => GeoInfo(
    id = geoInfoId + i,
    data = GeoInfoData(
      constrain = constrain,
      occurrence = occurrence,
      wifi = wifi,
      latitude = latitude,
      longitude = longitude,
      system = system)))

  def createGeoInfoValues = Map[String, Any](
    GeoInfoEntity.constrain -> constrain,
    GeoInfoEntity.occurrence -> occurrence,
    GeoInfoEntity.wifi -> wifi,
    GeoInfoEntity.latitude -> latitude,
    GeoInfoEntity.longitude -> longitude,
    GeoInfoEntity.system -> system)

  def createGeoInfoData = GeoInfoData(
    constrain = constrain,
    occurrence = occurrence,
    wifi = wifi,
    latitude = latitude,
    longitude = longitude,
    system = system)
}
