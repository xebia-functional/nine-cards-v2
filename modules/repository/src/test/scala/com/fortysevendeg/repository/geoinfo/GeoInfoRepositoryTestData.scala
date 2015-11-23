package com.fortysevendeg.repository.geoinfo

import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfoData, GeoInfo}
import com.fortysevendeg.ninecardslauncher.repository.provider.{GeoInfoEntityData, GeoInfoEntity}

import scala.util.Random

trait GeoInfoRepositoryTestData {

  val testGeoInfoId = Random.nextInt(10)
  val testNonExistingGeoInfoId = 15
  val testConstrain = Random.nextString(5)
  val testNonExistingConstrain = Random.nextString(5)
  val testOccurrence = Random.nextString(5)
  val testWifi = Random.nextString(5)
  val testLatitude = Random.nextDouble()
  val testLongitude = Random.nextDouble()
  val testSystem = Random.nextInt(10) < 5

  val geoInfoEntitySeq = createGeoInfoEntitySeq(5)
  val geoInfoEntity = geoInfoEntitySeq(0)
  val geoInfoSeq = createGeoInfoSeq(5)
  val geoInfo = geoInfoSeq(0)

  def createGeoInfoEntitySeq(num: Int) = List.tabulate(num)(
    i => GeoInfoEntity(
      id = testGeoInfoId + i,
      data = GeoInfoEntityData(
        constrain = testConstrain,
        occurrence = testOccurrence,
        wifi = testWifi,
        latitude = testLatitude,
        longitude = testLongitude,
        system = testSystem)))

  def createGeoInfoSeq(num: Int) = List.tabulate(num)(
    i => GeoInfo(
      id = testGeoInfoId + i,
      data = GeoInfoData(
        constrain = testConstrain,
        occurrence = testOccurrence,
        wifi = testWifi,
        latitude = testLatitude,
        longitude = testLongitude,
        system = testSystem)))

  def createGeoInfoValues = Map[String, Any](
    GeoInfoEntity.constrain -> testConstrain,
    GeoInfoEntity.occurrence -> testOccurrence,
    GeoInfoEntity.wifi -> testWifi,
    GeoInfoEntity.latitude -> testLatitude,
    GeoInfoEntity.longitude -> testLongitude,
    GeoInfoEntity.system -> testSystem)

  def createGeoInfoData = GeoInfoData(
    constrain = testConstrain,
    occurrence = testOccurrence,
    wifi = testWifi,
    latitude = testLatitude,
    longitude = testLongitude,
    system = testSystem)
}
