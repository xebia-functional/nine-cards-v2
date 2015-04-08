package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.provider.{DBUtils, NineCardsContentProvider}
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

trait GeoInfoRepositoryClient extends DBUtils {

  self: AppContextProvider =>

  def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {

          val contentValues = new ContentValues()
          contentValues.put(Constrain, request.data.constrain)
          contentValues.put(Occurrence, request.data.occurrence)
          contentValues.put(Wifi, request.data.wifi)
          contentValues.put(Latitude, request.data.latitude)
          contentValues.put(Longitude, request.data.longitude)
          contentValues.put(System, request.data.system)

          val uri = appContextProvider.get.getContentResolver.insert(
            NineCardsContentProvider.ContentUriGeoInfo,
            contentValues)

          AddGeoInfoResponse(geoInfo = Some(request.data.copy(id = Integer.parseInt(uri.getPathSegments.get(1)))))

        } recover {
          case e: Exception =>
            AddGeoInfoResponse(geoInfo = None)
        }
      }

  def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          appContextProvider.get.getContentResolver.delete(
            withAppendedPath(NineCardsContentProvider.ContentUriGeoInfo, request.geoInfo.id.toString),
            "",
            Array.empty)

          DeleteGeoInfoResponse(success = true)

        } recover {
          case e: Exception =>
            DeleteGeoInfoResponse(success = false)
        }
      }

  def getGeoInfoById: Service[GetGeoInfoByIdRequest, GetGeoInfoByIdResponse] =
    request =>
      tryToFuture {
        Try {
          val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
            withAppendedPath(NineCardsContentProvider.ContentUriGeoInfo, request.id.toString),
            Array.empty,
            "",
            Array.empty,
            ""))

          GetGeoInfoByIdResponse(result = getEntityFromCursor(cursor, geoInfoEntityFromCursor) map toGeoInfo)

        } recover {
          case e: Exception =>
            GetGeoInfoByIdResponse(result = None)
        }
      }


  def getGeoInfoByConstrain: Service[GetGeoInfoByConstrainRequest, GetGeoInfoByConstrainResponse] =
    request =>
      tryToFuture {
        Try {
          val cursor: Option[Cursor] = Option(appContextProvider.get.getContentResolver.query(
            NineCardsContentProvider.ContentUriGeoInfo,
            AllFields,
            s"$Constrain = ?",
            Array(request.constrain),
            ""))

          GetGeoInfoByConstrainResponse(result = getEntityFromCursor(cursor, geoInfoEntityFromCursor) map toGeoInfo)

        } recover {
          case e: Exception =>
            GetGeoInfoByConstrainResponse(result = None)
        }
      }

  def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(Constrain, request.geoInfo.constrain)
          contentValues.put(Occurrence, request.geoInfo.occurrence)
          contentValues.put(Wifi, request.geoInfo.wifi)
          contentValues.put(Latitude, request.geoInfo.latitude)
          contentValues.put(Longitude, request.geoInfo.longitude)
          contentValues.put(System, request.geoInfo.system)

          appContextProvider.get.getContentResolver.update(
            withAppendedPath(NineCardsContentProvider.ContentUriGeoInfo, request.geoInfo.id.toString),
            contentValues,
            "",
            Array.empty)

          UpdateGeoInfoResponse(success = true)

        } recover {
          case e: Exception =>
            UpdateGeoInfoResponse(success = false)
        }
      }
}
