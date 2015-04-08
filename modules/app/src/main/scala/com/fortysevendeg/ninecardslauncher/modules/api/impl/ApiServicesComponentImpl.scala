package com.fortysevendeg.ninecardslauncher.modules.api.impl

import akka.actor.ActorSystem
import com.fortysevendeg.macroid.extras.AppContextProvider
import com.fortysevendeg.ninecardslauncher.api.NineCardsServiceClient
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher2.R
import com.fortysevendeg.rest.client.HttpClient

import scala.concurrent.{ExecutionContext}

trait ApiServicesComponentImpl
  extends ApiServicesComponent {

  self: AppContextProvider =>

  lazy val apiServices = new ApiServicesImpl

  class ApiServicesImpl
    extends ApiServices
    with NineCardsServiceClient
    with HttpClient
    with Conversions {

    implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

    implicit val actorSystem: ActorSystem = ActorSystem(getString(R.string.api_actor))

    override val httpClient = this

    override val baseUrl = getString(R.string.api_base_url)

    val HeaderAppId = "X-Appsly-Application-Id"

    val HeaderAppKey = "X-Appsly-REST-API-Key"

    val HeaderDevice = "X-Android-ID"

    val HeaderToken = "X-Appsly-Session-Token"

    val HeaderLocalization = "X-Android-Market-Localization"

    val baseHeader: Seq[(String, String)] = Seq(
      (HeaderAppId, getString(R.string.api_app_id)),
      (HeaderAppKey, getString(R.string.api_app_key)),
      (HeaderLocalization, getString(R.string.api_localization)))

    override def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse] =
      request =>
          for {
            response <- getUserConfig(createHeader(request.deviceId, request.token))
          } yield GetUserConfigResponse(response.statusCode, response.data map toUserConfig)

    override def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse] =
      request =>
          for {
            response <- saveDevice(
              fromUserConfigDevice(request.userConfigDevice),
              createHeader(request.deviceId, request.token))
          } yield SaveDeviceResponse(response.statusCode, response.data map toUserConfig)

    override def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse] =
      request =>
          for {
            response <- saveGeoInfo(
              fromUserConfigGeoInfo(request.userConfigGeoInfo),
              createHeader(request.deviceId, request.token))
          } yield SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)

    override def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse] =
      request =>
          for {
            response <- checkpointPurchaseProduct(
              request.productId,
              createHeader(request.deviceId, request.token))
          } yield CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)

    override def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse] =
      request =>
          for {
            response <- checkpointCustomCollection(createHeader(request.deviceId, request.token))
          } yield CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)

    override def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse] =
      request =>
          for {
            response <- checkpointJoinedBy(
              request.otherConfigId,
              createHeader(request.deviceId, request.token))
          } yield CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)

    override def tester: Service[TesterRequest, TesterResponse] =
      request =>
          for {
            response <- tester(
              request.replace,
              createHeader(request.deviceId, request.token))
          } yield TesterResponse(response.statusCode, response.data map toUserConfig)

    private def getString(string: Int) = appContextProvider.get.getString(string)

    private def createHeader(device: String, token: String) =
      baseHeader :+ (HeaderDevice, device) :+ (HeaderToken, token)

  }

}
