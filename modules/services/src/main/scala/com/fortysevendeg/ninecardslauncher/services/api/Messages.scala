package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.api.models._

case class RequestConfig(deviceId: String, token: String)

case class LoginResponse(
  statusCode: Int,
  user: User)

case class InstallationResponse(
  statusCode: Int,
  installation: Installation)

case class UpdateInstallationResponse(
  statusCode: Int)

case class GooglePlayPackageResponse(
  statusCode: Int,
  app: GooglePlayApp)

case class GooglePlayPackagesResponse(
  statusCode: Int,
  packages: Seq[GooglePlayPackage])

case class GooglePlaySimplePackagesResponse(
  statusCode: Int,
  apps: GooglePlaySimplePackages)

trait UserConfigResponse {
  def statusCode: Int

  def userConfig: UserConfig
}

case class GetUserConfigResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class SaveDeviceResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class SaveGeoInfoResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class CheckpointPurchaseProductResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class CheckpointCustomCollectionResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class CheckpointJoinedByResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class TesterResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse
