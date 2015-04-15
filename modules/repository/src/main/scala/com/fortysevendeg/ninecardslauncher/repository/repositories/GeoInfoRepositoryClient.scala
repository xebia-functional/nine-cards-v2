package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.provider.GeoInfoEntity._
import com.fortysevendeg.ninecardslauncher.provider.{DBUtils, NineCardsContentProvider}
import com.fortysevendeg.ninecardslauncher.repository.Conversions.toGeoInfo
import com.fortysevendeg.ninecardslauncher.repository._
import com.fortysevendeg.ninecardslauncher.repository.model.GeoInfo
import com.fortysevendeg.ninecardslauncher.utils._

import scala.concurrent.ExecutionContext
import scala.util.Try

trait GeoInfoRepositoryClient extends DBUtils {

  self: ContentResolverProvider =>

  implicit val executionContext: ExecutionContext

  def addGeoInfo(): Service[AddGeoInfoRequest, AddGeoInfoResponse] =
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

          val uri = contentResolver.insert(
            NineCardsContentProvider.ContentUriGeoInfo,
            contentValues)

          AddGeoInfoResponse(
            geoInfo = Some(GeoInfo(
              id = Integer.parseInt(uri.getPathSegments.get(1)),
              data = request.data)))

        } recover {
          case e: Exception =>
            AddGeoInfoResponse(geoInfo = None)
        }
      }

  def deleteGeoInfo(): Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          contentResolver.delete(
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
          val maybeCursor: Option[Cursor] = Option(contentResolver.query(
            withAppendedPath(NineCardsContentProvider.ContentUriGeoInfo, request.id.toString),
            Array.empty,
            "",
            Array.empty,
            ""))

          maybeCursor match {
            case Some(cursor) =>
              GetGeoInfoByIdResponse(
                result = getEntityFromCursor(cursor, geoInfoEntityFromCursor) map toGeoInfo)
            case _ => GetGeoInfoByIdResponse(result = None)
          }

        } recover {
          case e: Exception =>
            GetGeoInfoByIdResponse(result = None)
        }
      }


  def getGeoInfoByConstrain: Service[GetGeoInfoByConstrainRequest, GetGeoInfoByConstrainResponse] =
    request =>
      tryToFuture {
        Try {
          val maybeCursor: Option[Cursor] = Option(contentResolver.query(
            NineCardsContentProvider.ContentUriGeoInfo,
            AllFields,
            s"$Constrain = ?",
            Array(request.constrain),
            ""))

          maybeCursor match {
            case Some(cursor) =>
              GetGeoInfoByConstrainResponse(
                result = getEntityFromCursor(cursor, geoInfoEntityFromCursor) map toGeoInfo)
            case _ => GetGeoInfoByConstrainResponse(result = None)
          }

        } recover {
          case e: Exception =>
            GetGeoInfoByConstrainResponse(result = None)
        }
      }

  def updateGeoInfo(): Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse] =
    request =>
      tryToFuture {
        Try {
          val contentValues = new ContentValues()
          contentValues.put(Constrain, request.geoInfo.data.constrain)
          contentValues.put(Occurrence, request.geoInfo.data.occurrence)
          contentValues.put(Wifi, request.geoInfo.data.wifi)
          contentValues.put(Latitude, request.geoInfo.data.latitude)
          contentValues.put(Longitude, request.geoInfo.data.longitude)
          contentValues.put(System, request.geoInfo.data.system)

          contentResolver.update(
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
