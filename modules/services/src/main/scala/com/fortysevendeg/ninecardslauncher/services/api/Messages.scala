package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.api.models._

case class RequestConfig(deviceId: String, token: String)

case class LoginRequest(
  email: String,
  device: GoogleDevice)

case class LoginResponse(
  statusCode: Int,
  user: Option[User])

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

case class GooglePlayPackageResponse(
  statusCode: Int,
  app: Option[GooglePlayApp])

case class GooglePlayPackagesResponse(
  statusCode: Int,
  packages: Seq[GooglePlayPackage])

case class GooglePlaySimplePackagesResponse(
  statusCode: Int,
  apps: GooglePlaySimplePackages)

trait UserConfigResponse {
  def statusCode: Int
  def userConfig: Option[UserConfig]
}

case class GetUserConfigResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class SaveDeviceResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class SaveGeoInfoResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointPurchaseProductResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointCustomCollectionResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class CheckpointJoinedByResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse

case class TesterResponse(
  statusCode: Int,
  userConfig: Option[UserConfig]) extends UserConfigResponse
