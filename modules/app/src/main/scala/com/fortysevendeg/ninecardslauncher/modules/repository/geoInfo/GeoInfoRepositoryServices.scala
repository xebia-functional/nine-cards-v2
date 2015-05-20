package com.fortysevendeg.ninecardslauncher.modules.repository.geoInfo

import android.content.ContentResolver
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.repository.{FetchGeoInfoItemsRequest => RepositoryFetchGeoInfoItemsRequest, NineCardRepositoryClient}

import scala.concurrent.ExecutionContext

trait GeoInfoRepositoryServices {
  def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse]
  def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse]
  def fetchGeoInfoByConstrain: Service[FetchGeoInfoByConstrainRequest, FetchGeoInfoByConstrainResponse]
  def fetchGeoInfoItems: Service[FetchGeoInfoItemsRequest, FetchGeoInfoItemsResponse]
  def findGeoInfoById: Service[FindGeoInfoByIdRequest, FindGeoInfoByIdResponse]
  def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse]
}

trait GeoInfoRepositoryServicesComponent {
  val geoInfoRepositoryServices: GeoInfoRepositoryServices
}

trait GeoInfoRepositoryServicesComponentImpl
    extends GeoInfoRepositoryServicesComponent {

  self: ContextWrapperProvider =>

  lazy val geoInfoRepositoryServices = new GeoInfoRepositoryServicesImpl

  class GeoInfoRepositoryServicesImpl
      extends GeoInfoRepositoryServices
      with Conversions
      with NineCardRepositoryClient {

    override implicit val contentResolver: ContentResolver = contextProvider.application.getContentResolver
    override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    override def addGeoInfo: Service[AddGeoInfoRequest, AddGeoInfoResponse] =
      request =>
        repoAddGeoInfo(toRepositoryAddGeoInfoRequest(request)) map {
          response =>
            AddGeoInfoResponse(geoInfo = toGeoInfo(response.geoInfo))
        }

    override def deleteGeoInfo: Service[DeleteGeoInfoRequest, DeleteGeoInfoResponse] =
      request =>
        repoDeleteGeoInfo(toRepositoryDeleteGeoInfoRequest(request)) map {
          response =>
            DeleteGeoInfoResponse(deleted = response.deleted)
        }

    override def fetchGeoInfoByConstrain: Service[FetchGeoInfoByConstrainRequest, FetchGeoInfoByConstrainResponse] =
      request =>
        repoFetchGeoInfoByConstrain(toRepositoryFetchGeoInfoByConstrainRequest(request)) map {
          response =>
            FetchGeoInfoByConstrainResponse(geoInfo = response.geoInfo map toGeoInfo)
        }

    override def fetchGeoInfoItems: Service[FetchGeoInfoItemsRequest, FetchGeoInfoItemsResponse] =
      request =>
        repoFetchGeoInfoItems(RepositoryFetchGeoInfoItemsRequest()) map {
          response =>
            FetchGeoInfoItemsResponse(geoInfoItems = toGeoInfoSeq(response.geoInfoItems))
        }

    override def findGeoInfoById: Service[FindGeoInfoByIdRequest, FindGeoInfoByIdResponse] =
      request =>
        repoFindGeoInfoById(toRepositoryFindGeoInfoByIdRequest(request)) map {
          response =>
            FindGeoInfoByIdResponse(geoInfo = response.geoInfo map toGeoInfo)
        }

    override def updateGeoInfo: Service[UpdateGeoInfoRequest, UpdateGeoInfoResponse] =
      request =>
        repoUpdateGeoInfo(toRepositoryUpdateGeoInfoRequest(request)) map {
          response =>
            UpdateGeoInfoResponse(updated = response.updated)
        }
  }

}