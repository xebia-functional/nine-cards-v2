package com.fortysevendeg.ninecardslauncher.repository.repositories

import com.fortysevendeg.ninecardslauncher.commons.{ContentResolverWrapper, GeoInfoUri}
import com.fortysevendeg.ninecardslauncher.provider.DBUtils
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

class GeoInfoRepository(contentResolverWrapper: ContentResolverWrapper) extends DBUtils {

  def addGeoInfo(request: AddGeoInfoRequest)(implicit executionContext: ExecutionContext): Future[AddGeoInfoResponse] =
      tryToFuture {
        Try {
          val values = Map[String, Any](
            constrain -> request.data.constrain,
            occurrence -> request.data.occurrence,
            wifi -> request.data.wifi,
            latitude -> request.data.latitude,
            longitude -> request.data.longitude,
            system -> request.data.system)

          val id = contentResolverWrapper.insert(
            nineCardsUri = GeoInfoUri,
            values = values)

          AddGeoInfoResponse(
            geoInfo = GeoInfo(
              id = id,
              data = request.data))

        } recover {
          case NonFatal(e) => throw RepositoryInsertException()
        }
      }

  def deleteGeoInfo(request: DeleteGeoInfoRequest)(implicit executionContext: ExecutionContext): Future[DeleteGeoInfoResponse] =
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

  def fetchGeoInfoItems(request: FetchGeoInfoItemsRequest)(implicit executionContext: ExecutionContext): Future[FetchGeoInfoItemsResponse] =
      tryToFuture {
        Try {
          val geoInfoItems = contentResolverWrapper.fetchAll(
            nineCardsUri = GeoInfoUri,
            projection = allFields)(getListFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FetchGeoInfoItemsResponse(geoInfoItems)
        } recover {
          case e: Exception =>
            FetchGeoInfoItemsResponse(geoInfoItems = Seq.empty)
        }
      }

  def findGeoInfoById(request: FindGeoInfoByIdRequest)(implicit executionContext: ExecutionContext): Future[FindGeoInfoByIdResponse] =
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.findById(
            nineCardsUri = GeoInfoUri,
            id = request.id,
            projection = allFields)(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FindGeoInfoByIdResponse(geoInfo)
        } recover {
          case e: Exception =>
            FindGeoInfoByIdResponse(geoInfo = None)
        }
      }


  def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest)(implicit executionContext: ExecutionContext): Future[FetchGeoInfoByConstrainResponse] =
      tryToFuture {
        Try {
          val geoInfo = contentResolverWrapper.fetch(
            nineCardsUri = GeoInfoUri,
            projection = allFields,
            where = s"$constrain = ?",
            whereParams = Array(request.constrain))(getEntityFromCursor(geoInfoEntityFromCursor)) map toGeoInfo

          FetchGeoInfoByConstrainResponse(geoInfo)
        } recover {
          case e: Exception =>
            FetchGeoInfoByConstrainResponse(geoInfo = None)
        }
      }

  def updateGeoInfo(request: UpdateGeoInfoRequest)(implicit executionContext: ExecutionContext): Future[UpdateGeoInfoResponse] =
      tryToFuture {
        Try {
          val values = Map[String, Any](
            constrain -> request.geoInfo.data.constrain,
            occurrence -> request.geoInfo.data.occurrence,
            wifi -> request.geoInfo.data.wifi,
            latitude -> request.geoInfo.data.latitude,
            longitude -> request.geoInfo.data.longitude,
            system -> request.geoInfo.data.system)

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
