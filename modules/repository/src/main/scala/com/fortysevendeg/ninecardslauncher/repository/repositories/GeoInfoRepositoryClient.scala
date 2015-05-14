package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapperComponent, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try
import scala.util.control.NonFatal

trait GeoInfoRepositoryClient extends DBUtils {

  self: ContentResolverWrapperComponent =>

  implicit val executionContext: ExecutionContext

  def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            Constrain -> request.data.constrain,
            Occurrence -> request.data.occurrence,
            Wifi -> request.data.wifi,
            Latitude -> request.data.latitude,
            Longitude -> request.data.longitude,
            System -> request.data.system)

          val id = contentResolverWrapper.insert(
            nineCardsUri = GeoInfoUri,
            values = values)

          AddGeoInfoResponse(
            geoInfo = Some(GeoInfo(
              id = id,
              data = request.data)))

        } recover {
          case NonFatal(e) => throw RepositoryInsertException()
        }
      }

  def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          val deleted = contentResolverWrapper.deleteById(
            nineCardsUri = GeoInfoUri,
            id = request.geoInfo.id)

          DeleteGeoInfoResponse(deleted = deleted)

        } recover {
          case NonFatal(e) => throw RepositoryDeleteException()
        }
      }

  def fetchGeoInfoItems: Service[FetchGeoInfoItemsRequest, FetchGeoInfoItemsResponse] =
    request =>
      tryToFuture {
        Try {
          val geoInfoItems = contentResolverWrapper.fetchAll(
            nineCardsUri = GeoInfoUri,
            projection = AllFields)(getListFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FetchGeoInfoItemsResponse(geoInfoItems)
        } recover {
          case e: Exception =>
            FetchGeoInfoItemsResponse(geoInfoItems = Seq.empty)
        }
      }

  def getGeoInfoById: Service[FindGeoInfoByIdRequest, FindGeoInfoByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.findById(
            nineCardsUri = GeoInfoUri,
            id = request.id,
            projection = AllFields)(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FindGeoInfoByIdResponse(geoInfo)
        } recover {
          case e: Exception =>
            FindGeoInfoByIdResponse(geoInfo = None)
        }
      }


  def getGeoInfoByConstrain: Service[FetchGeoInfoByConstrainRequest, FetchGeoInfoByConstrainResponse] =
    request =>
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.fetch(
            nineCardsUri = GeoInfoUri,
            projection = AllFields,
            where = s"$Constrain = ?",
            whereParams = Array(request.constrain))(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FetchGeoInfoByConstrainResponse(geoInfo)
        } recover {
          case e: Exception =>
            FetchGeoInfoByConstrainResponse(geoInfo = None)
        }
      }

  def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          val values = Map[String, Any](
            Constrain -> request.geoInfo.data.constrain,
            Occurrence -> request.geoInfo.data.occurrence,
            Wifi -> request.geoInfo.data.wifi,
            Latitude -> request.geoInfo.data.latitude,
            Longitude -> request.geoInfo.data.longitude,
            System -> request.geoInfo.data.system)

          val updated = contentResolverWrapper.updateById(
            nineCardsUri = GeoInfoUri,
            id = request.geoInfo.id,
            values = values)

          UpdateGeoInfoResponse(updated = updated)

        } recover {
          case NonFatal(e) => throw RepositoryUpdateException()
        }
      }
}
