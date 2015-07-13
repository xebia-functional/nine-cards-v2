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
  userConfigService: ApiUserConfigService
  )
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

  override def login(
    email: String,
    device: GoogleDevice
    ): Task[NineCardsException \/ LoginResponse] =
    for {
      response <- apiUserService.login(toUser(email, device), baseHeader) ▹ eitherT
    } yield LoginResponse(response.statusCode, response.data map toUser)

  override def linkGoogleAccount(
    email: String,
    devices: Seq[GoogleDevice]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ LoginResponse] =
    for {
      response <- apiUserService.linkAuthData(toAuthData(email, devices), requestConfig.toHeader) ▹ eitherT
    } yield LoginResponse(response.statusCode, response.data map toUser)

  override def createInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]
    ): Task[NineCardsException \/ InstallationResponse] =
    for {
      response <- apiUserService.createInstallation(toInstallation(id, deviceType, deviceToken, userId), baseHeader) ▹ eitherT
    } yield InstallationResponse(response.statusCode, response.data map toInstallation)

  override def updateInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]
    ): Task[NineCardsException \/ UpdateInstallationResponse] =
    for {
      response <- apiUserService.updateInstallation(toInstallation(id, deviceType, deviceToken, userId), baseHeader) ▹ eitherT
    } yield UpdateInstallationResponse(response.statusCode)

  override def googlePlayPackage(
    packageName: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlayPackageResponse] =
    for {
      response <- googlePlayService.getGooglePlayPackage(packageName, requestConfig.toHeader) ▹ eitherT
    } yield GooglePlayPackageResponse(response.statusCode, response.data map (playApp => toGooglePlayApp(playApp.docV2)))

  override def googlePlayPackages(
    packageNames: Seq[String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlayPackagesResponse] =
    for {
      response <- googlePlayService.getGooglePlayPackages(PackagesRequest(packageNames), requestConfig.toHeader) ▹ eitherT
    } yield GooglePlayPackagesResponse(response.statusCode, response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)

  override def googlePlaySimplePackages(
    items: Seq[String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GooglePlaySimplePackagesResponse] =
    for {
      response <- googlePlayService.getGooglePlaySimplePackages(PackagesRequest(items), requestConfig.toHeader) ▹ eitherT
    } yield GooglePlaySimplePackagesResponse(
      response.statusCode,
      response.data.map(playApp => toGooglePlaySimplePackages(playApp)).getOrElse(GooglePlaySimplePackages(Seq.empty, Seq.empty)))

  override def getUserConfig(
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ GetUserConfigResponse] =
    for {
      response <- userConfigService.getUserConfig(requestConfig.toHeader) ▹ eitherT
    } yield GetUserConfigResponse(response.statusCode, response.data map toUserConfig)

  override def saveDevice(
    userConfigDevice: UserConfigDevice
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ SaveDeviceResponse] =
    for {
      response <- userConfigService.saveDevice(
        toConfigDevice(userConfigDevice),
        requestConfig.toHeader) ▹ eitherT
    } yield SaveDeviceResponse(response.statusCode, response.data map toUserConfig)

  override def saveGeoInfo(
    userConfigGeoInfo: UserConfigGeoInfo
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ SaveGeoInfoResponse] =
    for {
      response <- userConfigService.saveGeoInfo(
        toUserConfigGeoInfo(userConfigGeoInfo),
        requestConfig.toHeader) ▹ eitherT
    } yield SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointPurchaseProduct(
    productId: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointPurchaseProductResponse] =
    for {
      response <- userConfigService.checkpointPurchaseProduct(
        productId, requestConfig.toHeader) ▹ eitherT
    } yield CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointCustomCollection(
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointCustomCollectionResponse] =
    for {
      response <- userConfigService.checkpointCustomCollection(requestConfig.toHeader) ▹ eitherT
    } yield CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)

  override def checkpointJoinedBy(
    otherConfigId: String
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ CheckpointJoinedByResponse] =
    for {
      response <- userConfigService.checkpointJoinedBy(otherConfigId, requestConfig.toHeader) ▹ eitherT
    } yield CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)

  override def tester(
    replace: Map[String, String]
    )(implicit requestConfig: RequestConfig): Task[NineCardsException \/ TesterResponse] =
      for {
        response <- userConfigService.tester(replace, requestConfig.toHeader) ▹ eitherT
      } yield TesterResponse(response.statusCode, response.data map toUserConfig)

  implicit class RequestHeaderHeader(request: RequestConfig) {
    def toHeader: Seq[(String, String)] =
      baseHeader :+(headerDevice, request.deviceId) :+(headerToken, request.token)
  }

}