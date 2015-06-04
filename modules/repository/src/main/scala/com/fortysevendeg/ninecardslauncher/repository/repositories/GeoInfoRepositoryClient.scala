package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapper, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try
import scala.util.control.NonFatal

class GeoInfoRepositoryClient(contentResolverWrapper: ContentResolverWrapper)
  extends DBUtils {

  def addGeoInfo(request: AddGeoInfoRequest)(implicit ec: ExecutionContext): Future[AddGeoInfoResponse] =
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

  def deleteGeoInfo(request: DeleteGeoInfoRequest)(implicit ec: ExecutionContext): Future[DeleteGeoInfoResponse] =
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

  def getAllGeoInfoItems(request: GetAllGeoInfoItemsRequest)(implicit ec: ExecutionContext): Future[GetAllGeoInfoItemsResponse] =
      tryToFuture {
        Try {
          val geoInfoItems = contentResolverWrapper.fetchAll(
            nineCardsUri = GeoInfoUri,
            projection = AllFields)(getListFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          GetAllGeoInfoItemsResponse(geoInfoItems)
        } recover {
          case e: Exception =>
            GetAllGeoInfoItemsResponse(geoInfoItems = Seq.empty)
        }
      }

  def getGeoInfoById(request: GetGeoInfoByIdRequest)(implicit ec: ExecutionContext): Future[GetGeoInfoByIdResponse] =
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.findById(
            nineCardsUri = GeoInfoUri,
            id = request.id,
            projection = AllFields)(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          GetGeoInfoByIdResponse(geoInfo)
        } recover {
          case e: Exception =>
            GetGeoInfoByIdResponse(result = None)
        }
      }


  def getGeoInfoByConstrain(request: GetGeoInfoByConstrainRequest)(implicit ec: ExecutionContext): Future[GetGeoInfoByConstrainResponse] =
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.fetch(
            nineCardsUri = GeoInfoUri,
            projection = AllFields,
            where = s"$Constrain = ?",
            whereParams = Array(request.constrain))(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          GetGeoInfoByConstrainResponse(geoInfo)
        } recover {
          case e: Exception =>
            GetGeoInfoByConstrainResponse(result = None)
        }
      }

  def updateGeoInfo(request: UpdateGeoInfoRequest)(implicit ec: ExecutionContext): Future[UpdateGeoInfoResponse] =
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
