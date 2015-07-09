package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository.commons.{ContentResolverWrapper, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.repository.model.{GeoInfo, GeoInfoData}
import com.fortysevendeg.ninecardslauncher.repository.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.repository.provider.{DBUtils, GeoInfoEntity}

import scalaz.\/
import scalaz.concurrent.Task

class GeoInfoRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addGeoInfo(data: GeoInfoData): Task[NineCardsException \/ GeoInfo] =
    Task {
      \/.fromTryCatchThrowable[GeoInfo, NineCardsException] {
        val values = Map[String, Any](
          constrain -> data.constrain,
          occurrence -> data.occurrence,
          wifi -> data.wifi,
          latitude -> data.latitude,
          longitude -> data.longitude,
          system -> data.system)

        val id = contentResolverWrapper.insert(
          nineCardsUri = GeoInfoUri,
          values = values)

        GeoInfo(id = id, data = data)
      }
    }

  def deleteGeoInfo(geoInfo: GeoInfo): Task[NineCardsException \/ Int] =
    Task {
      \/.fromTryCatchThrowable[Int, NineCardsException] {
        contentResolverWrapper.deleteById(
          nineCardsUri = GeoInfoUri,
          id = geoInfo.id)
      }
    }

  def fetchGeoInfoItems: Task[NineCardsException \/ Seq[GeoInfo]] =
    Task {
      \/.fromTryCatchThrowable[Seq[GeoInfo], NineCardsException] {
        contentResolverWrapper.fetchAll(
          nineCardsUri = GeoInfoUri,
          projection = allFields)(getListFromCursor(geoInfoEntityFromCursor)) map toGeoInfo
      }
    }

  def findGeoInfoById(id: Int): Task[NineCardsException \/ Option[GeoInfo]] =
    Task {
      \/.fromTryCatchThrowable[Option[GeoInfo], NineCardsException] {
        contentResolverWrapper.findById(
          nineCardsUri = GeoInfoUri,
          id = id,
          projection = allFields)(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo
      }
    }


  def fetchGeoInfoByConstrain(constrain: String): Task[NineCardsException \/ Option[GeoInfo]] =
    Task {
      \/.fromTryCatchThrowable[Option[GeoInfo], NineCardsException] {
        contentResolverWrapper.fetch(
          nineCardsUri = GeoInfoUri,
          projection = allFields,
          where = s"${GeoInfoEntity.constrain} = ?",
          whereParams = Array(constrain))(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo
      }
    }

  def updateGeoInfo(geoInfo: GeoInfo): Task[NineCardsException \/ Int] =
    Task {
      \/.fromTryCatchThrowable[Int, NineCardsException] {
        val values = Map[String, Any](
          constrain -> geoInfo.data.constrain,
          occurrence -> geoInfo.data.occurrence,
          wifi -> geoInfo.data.wifi,
          latitude -> geoInfo.data.latitude,
          longitude -> geoInfo.data.longitude,
          system -> geoInfo.data.system)

        contentResolverWrapper.updateById(
          nineCardsUri = GeoInfoUri,
          id = geoInfo.id,
          values = values)
      }
    }
}
