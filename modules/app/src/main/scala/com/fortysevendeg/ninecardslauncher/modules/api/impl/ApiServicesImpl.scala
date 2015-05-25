package com.fortysevendeg.ninecardslauncher.modules.api.impl

import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.api.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiUserConfigService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.di.Module
import com.fortysevendeg.ninecardslauncher.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.user.{UserNotFoundException, UserService}
import com.fortysevendeg.ninecardslauncher2.R

import scala.concurrent.ExecutionContext

class ApiServicesImpl(
    resources: Resources,
    repositoryServices: RepositoryServices,
    apiUserService: ApiUserService,
    googlePlayService: ApiGooglePlayService,
    userConfigService: ApiUserConfigService)
    extends ApiServices
    with Module
    with Conversions {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val HeaderAppId = "X-Appsly-Application-Id"

  val HeaderAppKey = "X-Appsly-REST-API-Key"

  val HeaderDevice = "X-Android-ID"

  val HeaderToken = "X-Appsly-Session-Token"

  val HeaderLocalization = "X-Android-Market-Localization"

  val baseHeader: Seq[(String, String)] = Seq(
    (HeaderAppId, resources.getString(R.string.api_app_id)),
    (HeaderAppKey, resources.getString(R.string.api_app_key)),
    (HeaderLocalization, resources.getString(R.string.api_localization)))

  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  
  override def login: Service[LoginRequest, LoginResponse] =
    request =>
      for {
        response <- apiUserService.login(fromLoginRequest(request), baseHeader)
      } yield LoginResponse(response.statusCode, response.data map toUser)

  override def linkGoogleAccount: Service[LinkGoogleAccountRequest, LoginResponse] =
    request =>
      for {
        response <- apiUserService.linkAuthData(fromLinkGoogleAccountRequest(request), createAuthHeader)
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
        response <- googlePlayService.getGooglePlayPackage(request.packageName, createAuthHeader)
      } yield GooglePlayPackageResponse(response.statusCode, response.data map (playApp => toGooglePlayApp(playApp.docV2)))

  override def googlePlayPackages: Service[GooglePlayPackagesRequest, GooglePlayPackagesResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlayPackages(PackagesRequest(request.packageNames), createAuthHeader)
      } yield GooglePlayPackagesResponse(response.statusCode, response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)

  override def googlePlaySimplePackages: Service[GooglePlaySimplePackagesRequest, GooglePlaySimplePackagesResponse] =
    request =>
      for {
        response <- googlePlayService.getGooglePlaySimplePackages(PackagesRequest(request.items), createAuthHeader)
      } yield GooglePlaySimplePackagesResponse(
        response.statusCode,
        response.data.map(playApp => toGooglePlaySimplePackages(playApp)).getOrElse(GooglePlaySimplePackages(Seq.empty, Seq.empty)))

  override def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse] =
    request =>
      for {
        response <- userConfigService.getUserConfig(createAuthHeader)
      } yield GetUserConfigResponse(response.statusCode, response.data map toUserConfig)

  override def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse] =
    request =>
      for {
        response <- userConfigService.saveDevice(
          fromUserConfigDevice(request.userConfigDevice),
          createAuthHeader)
      } yield SaveDeviceResponse(response.statusCode, response.data map toUserConfig)

  override def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse] =
    request =>
      for {
        response <- userConfigService.saveGeoInfo(
          fromUserConfigGeoInfo(request.userConfigGeoInfo),
          createAuthHeader)
      } yield SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse] =
    request =>
      for {
        response <- userConfigService.checkpointPurchaseProduct(
          request.productId,
          createAuthHeader)
      } yield CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse] =
    request =>
      for {
        response <- userConfigService.checkpointCustomCollection(
          createAuthHeader)
      } yield CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse] =
    request =>
      for {
        response <- userConfigService.checkpointJoinedBy(
          request.otherConfigId,
          createAuthHeader)
      } yield CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)

  override def tester: Service[TesterRequest, TesterResponse] =
    request =>
      for {
        response <- userConfigService.tester(
          request.replace,
          createAuthHeader)
      } yield TesterResponse(response.statusCode, response.data map toUserConfig)

  private def createAuthHeader = {
    val (token, androidId) = readAuthHeader 
    baseHeader :+ (HeaderDevice, androidId) :+ (HeaderToken, token)
  }

  private def readAuthHeader: (String, String) = {
    val result = for {
      user <- repositoryServices.getUser
      token <- user.sessionToken
      androidId <- repositoryServices.getAndroidId
    } yield (token, androidId)
    result getOrElse (throw UserNotFoundException())
  }
}