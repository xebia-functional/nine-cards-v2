package com.fortysevendeg.ninecardslauncher.modules.api

import com.fortysevendeg.ninecardslauncher.models._

case class LoginRequest(
    email: String,
    device: GoogleDevice)

case class LoginResponse(
    statusCode: Int,
    user: Option[User])

case class LinkGoogleAccountRequest(
    email: String,
    devices: Seq[GoogleDevice])

case class InstallationRequest(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String])

case class InstallationResponse(
    statusCode: Int,
    installation: Option[Installation])

case class UpdateInstallationResponse(
    statusCode: Int)

case class GooglePlayPackageRequest(
    packageName: String)

case class GooglePlayPackageResponse(
    statusCode: Int,
    app: Option[GooglePlayApp])

case class GooglePlayPackagesRequest(
    packageNames: Seq[String])

case class GooglePlayPackagesResponse(
    statusCode: Int,
    packages: Seq[GooglePlayPackage])

case class GooglePlaySimplePackagesRequest(
    items: Seq[String])

case class GooglePlaySimplePackagesResponse(
    statusCode: Int,
    apps: GooglePlaySimplePackages)

trait UserConfigResponse {
    def statusCode: Int
    def userConfig: Option[UserConfig]
}

case class GetUserConfigRequest()

case class GetUserConfigResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class SaveDeviceRequest(userConfigDevice: UserConfigDevice)

case class SaveDeviceResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class SaveGeoInfoRequest(userConfigGeoInfo: UserConfigGeoInfo)

case class SaveGeoInfoResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointPurchaseProductRequest(productId: String)

case class CheckpointPurchaseProductResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointCustomCollectionRequest()

case class CheckpointCustomCollectionResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointJoinedByRequest(otherConfigId: String)

case class CheckpointJoinedByResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse

case class TesterRequest(replace: Map[String, String])

case class TesterResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserConfigResponse
