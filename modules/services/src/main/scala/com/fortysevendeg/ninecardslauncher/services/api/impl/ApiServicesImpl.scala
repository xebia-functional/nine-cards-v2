package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiUserConfigService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._

import scala.concurrent.ExecutionContext
import scalaz.concurrent.Task
import scalaz._
import Scalaz._
import EitherT._
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

case class ApiServicesConfig(appId: String, appKey: String, localization: String)

class ApiServicesImpl(
  apiServicesConfig: ApiServicesConfig,
  apiUserService: ApiUserService,
  googlePlayService: ApiGooglePlayService,
  userConfigService: ApiUserConfigService)
  extends ApiServices
  with Conversions {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val headerAppId = "X-Appsly-Application-Id"

  val headerAppKey = "X-Appsly-REST-API-Key"

  val headerDevice = "X-Android-ID"

  val headerToken = "X-Appsly-Session-Token"

  val headerLocalization = "X-Android-Market-Localization"

  val baseHeader: Seq[(String, String)] = Seq(
    (headerAppId, apiServicesConfig.appId),
    (headerAppKey, apiServicesConfig.appKey),
    (headerLocalization, apiServicesConfig.localization))

  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._

  override def login: Service[LoginRequest, LoginResponse] =
    request =>
      for {
        response <- apiUserService.login(fromLoginRequest(request), baseHeader)
      } yield LoginResponse(response.statusCode, response.data map toUser)

  override def linkGoogleAccount: Service[LinkGoogleAccountRequest, LoginResponse] =
    request =>
      for {
        response <- apiUserService.linkAuthData(fromLinkGoogleAccountRequest(request), createHeader(request.deviceId, request.token))
      } yield LoginResponse(response.statusCode, response.data map toUser)

  override def createInstallation: Service[InstallationRequest, InstallationResponse] =
    request =>
      for {
        response <- apiUserService.createInstallation(fromInstallationRequest(request), baseHeader)
      } yield InstallationResponse(response.statusCode, response.data map toInstallation)

  override def updateInstallation: Service[InstallationRequest, UpdateInstallationResponse] =
    request =>
      for {
        response <- apiUserService.updateInstallation(fromInstallationRequest(request), baseHeader)
      } yield UpdateInstallationResponse(response.statusCode)

  override def googlePlayPackage: Service[GooglePlayPackageRequest, GooglePlayPackageResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlayPackage(request.packageName, createHeader(request.deviceId, request.token))
      } yield GooglePlayPackageResponse(response.statusCode, response.data map (playApp => toGooglePlayApp(playApp.docV2)))

  override def googlePlayPackages: Service[GooglePlayPackagesRequest, GooglePlayPackagesResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlayPackages(PackagesRequest(request.packageNames), createHeader(request.deviceId, request.token))
      } yield GooglePlayPackagesResponse(response.statusCode, response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)

  override def googlePlaySimplePackages(request: GooglePlaySimplePackagesRequest): Task[NineCardsException \/ GooglePlaySimplePackagesResponse] =
      for {
        response <- googlePlayService.getGooglePlaySimplePackages(PackagesRequest(request.items), createHeader(request.deviceId, request.token)) ▹ eitherT
      } yield GooglePlaySimplePackagesResponse(
        response.statusCode,
        response.data.map(playApp => toGooglePlaySimplePackages(playApp)).getOrElse(GooglePlaySimplePackages(Seq.empty, Seq.empty)))

  override def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse] =
    request =>
      for {
        response <- userConfigService.getUserConfig(createHeader(request.deviceId, request.token))
      } yield GetUserConfigResponse(response.statusCode, response.data map toUserConfig)

  override def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse] =
    request =>
      for {
        response <- userConfigService.saveDevice(
          fromUserConfigDevice(request.userConfigDevice),
          createHeader(request.deviceId, request.token))
      } yield SaveDeviceResponse(response.statusCode, response.data map toUserConfig)

  override def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse] =
    request =>
      for {
        response <- userConfigService.saveGeoInfo(
          fromUserConfigGeoInfo(request.userConfigGeoInfo),
          createHeader(request.deviceId, request.token))
      } yield SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse] =
    request =>
      for {
        response <- userConfigService.checkpointPurchaseProduct(
          request.productId,
          createHeader(request.deviceId, request.token))
      } yield CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse] =
    request =>
      for {
        response <- userConfigService.checkpointCustomCollection(createHeader(request.deviceId, request.token))
      } yield CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse] =
    request =>
      for {
        response <- userConfigService.checkpointJoinedBy(
          request.otherConfigId,
          createHeader(request.deviceId, request.token))
      } yield CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)

  override def tester: Service[TesterRequest, TesterResponse] =
    request =>
      for {
        response <- userConfigService.tester(
          request.replace,
          createHeader(request.deviceId, request.token))
      } yield TesterResponse(response.statusCode, response.data map toUserConfig)

  private def createHeader(device: String, token: String) =
    baseHeader :+ (headerDevice, device) :+ (headerToken, token)

}