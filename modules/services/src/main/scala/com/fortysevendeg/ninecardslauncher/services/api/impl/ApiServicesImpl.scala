package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.services.{ApiRecommendationService, ApiGooglePlayService, ApiUserConfigService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import rapture.core.{Answer, Errata}

import scalaz.concurrent.Task

case class ApiServicesConfig(appId: String, appKey: String, localization: String)

class ApiServicesImpl(
  apiServicesConfig: ApiServicesConfig,
  apiUserService: ApiUserService,
  googlePlayService: ApiGooglePlayService,
  userConfigService: ApiUserConfigService,
  recommendationService: ApiRecommendationService)
  extends ApiServices
  with Conversions
  with ImplicitsApiServiceExceptions {

  val headerAppId = "X-Appsly-Application-Id"

  val headerAppKey = "X-Appsly-REST-API-Key"

  val headerDevice = "X-Android-ID"

  val headerToken = "X-Appsly-Session-Token"

  val headerLocalization = "X-Android-Market-Localization"

  val baseHeader: Seq[(String, String)] = Seq(
    (headerAppId, apiServicesConfig.appId),
    (headerAppKey, apiServicesConfig.appKey),
    (headerLocalization, apiServicesConfig.localization))

  val userNotFoundMessage = "User not found"

  val installationNotFoundMessage = "Installation not found"

  val playAppNotFoundMessage = "Google Play Package not found"

  val userConfigNotFoundMessage = "User configuration not found"

  val categoryNotFoundMessage = "Google Play Category not found"

  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.RecommendationImplicits._

  override def login(
    email: String,
    device: GoogleDevice) =
    (for {
      response <- apiUserService.login(toUser(email, device), baseHeader)
      user <- readOption(response.data, userNotFoundMessage)
    } yield LoginResponse(response.statusCode, toUser(user))).resolve[ApiServiceException]

  override def linkGoogleAccount(
    email: String,
    devices: Seq[GoogleDevice])(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiUserService.linkAuthData(toAuthData(email, devices), requestConfig.toHeader)
      user <- readOption(response.data, userNotFoundMessage)
    } yield LoginResponse(response.statusCode, toUser(user))).resolve[ApiServiceException]

  override def createInstallation(
    deviceType: Option[DeviceType],
    deviceToken: Option[String],
    userId: Option[String]) =
    (for {
      response <- apiUserService.createInstallation(toInstallation(None, deviceType, deviceToken, userId), baseHeader)
      installation <- readOption(response.data, installationNotFoundMessage)
    } yield InstallationResponse(response.statusCode, toInstallation(installation))).resolve[ApiServiceException]

  override def updateInstallation(
    id: String,
    deviceType: Option[DeviceType],
    deviceToken: Option[String],
    userId: Option[String]) =
    (for {
      response <- apiUserService.updateInstallation(toInstallation(Some(id), deviceType, deviceToken, userId), baseHeader)
    } yield UpdateInstallationResponse(response.statusCode)).resolve[ApiServiceException]

  override def googlePlayPackage(
    packageName: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- googlePlayService.getGooglePlayPackage(packageName, requestConfig.toHeader)
      playApp <- readOption(response.data, playAppNotFoundMessage)
    } yield GooglePlayPackageResponse(response.statusCode, toGooglePlayApp(playApp.docV2))).resolve[ApiServiceException]

  override def googlePlayPackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- googlePlayService.getGooglePlayPackages(PackagesRequest(packageNames), requestConfig.toHeader)
    } yield GooglePlayPackagesResponse(
        statusCode = response.statusCode,
        packages = response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)).resolve[ApiServiceException]

  override def googlePlaySimplePackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- googlePlayService.getGooglePlaySimplePackages(PackagesRequest(packageNames), requestConfig.toHeader)
    } yield {
        val packages = response.data.map(playApp => toGooglePlaySimplePackages(playApp))
        GooglePlaySimplePackagesResponse(
          statusCode = response.statusCode,
          apps = packages getOrElse GooglePlaySimplePackages(Seq.empty, Seq.empty))
      }).resolve[ApiServiceException]

  override def getUserConfig()(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.getUserConfig(requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield GetUserConfigResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def saveDevice(userConfigDevice: UserConfigDevice)(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.saveDevice(
        toConfigDevice(userConfigDevice),
        requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield SaveDeviceResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def saveGeoInfo(userConfigGeoInfo: UserConfigGeoInfo)(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.saveGeoInfo(
        toUserConfigGeoInfo(userConfigGeoInfo),
        requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield SaveGeoInfoResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def checkpointPurchaseProduct(productId: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.checkpointPurchaseProduct(
        productId, requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield CheckpointPurchaseProductResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def checkpointCustomCollection()(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.checkpointCustomCollection(requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield CheckpointCustomCollectionResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def checkpointJoinedBy(otherConfigId: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.checkpointJoinedBy(otherConfigId, requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield CheckpointJoinedByResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def tester(replace: Map[String, String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.tester(replace, requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield TesterResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def getRecommendedApps(categories: Seq[String], limit: Int)(implicit requestConfig: RequestConfig) =
    (for {
      response <- recommendationService.getRecommendedApps(toRecommendationRequest(categories, limit), requestConfig.toHeader)
      recommendation <- readOption(response.data, categoryNotFoundMessage)
    } yield RecommendationResponse(response.statusCode, toPlayAppSeq(recommendation))).resolve[ApiServiceException]

  implicit class RequestHeaderHeader(request: RequestConfig) {
    def toHeader: Seq[(String, String)] =
      baseHeader :+(headerDevice, request.deviceId) :+(headerToken, request.token)
  }

  private[this] def readOption[T](maybe: Option[T], msg: String = ""): ServiceDef2[T, ApiServiceException] = Service {
    Task {
      maybe match {
        case Some(v) => Answer(v)
        case _ => Errata(ApiServiceException(msg))
      }
    }
  }

}