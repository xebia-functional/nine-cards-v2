package com.fortysevendeg.ninecardslauncher.modules.api

trait UserRequest {
  def deviceId: String
  def token: String
}

trait UserResponse {
  def statusCode: Int
  def userConfig: Option[UserConfig]
}

case class GetUserConfigRequest(
    deviceId: String,
    token: String) extends UserRequest

case class GetUserConfigResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class SaveDeviceRequest(
    deviceId: String,
    token: String,
    userConfigDevice: UserConfigDevice) extends UserRequest

case class SaveDeviceResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class SaveGeoInfoRequest(
    deviceId: String,
    token: String,
    userConfigGeoInfo: UserConfigGeoInfo) extends UserRequest

case class SaveGeoInfoResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class CheckpointPurchaseProductRequest(
    deviceId: String,
    token: String,
    productId: String) extends UserRequest

case class CheckpointPurchaseProductResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class CheckpointCustomCollectionRequest(
    deviceId: String,
    token: String) extends UserRequest

case class CheckpointCustomCollectionResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class CheckpointJoinedByRequest(
    deviceId: String,
    token: String,
    otherConfigId: String) extends UserRequest

case class CheckpointJoinedByResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse

case class TesterRequest(
    deviceId: String,
    token: String,
    replace: Map[String, String]) extends UserRequest

case class TesterResponse(
    statusCode: Int,
    userConfig: Option[UserConfig]) extends UserResponse
