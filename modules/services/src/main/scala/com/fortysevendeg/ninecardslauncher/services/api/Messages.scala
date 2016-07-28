package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.api.models._

case class RequestConfig(deviceId: String, token: String, androidToken: Option[String])

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

case class RecommendationResponse(
  statusCode: Int,
  seq: Seq[GooglePlayApp])

case class SharedCollectionResponseList(
  statusCode: Int,
  items: Seq[SharedCollectionResponse])

case class SharedCollectionResponse(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  description: String,
  screenshots: Seq[String],
  author: String,
  tags: Seq[String],
  name: String,
  shareLink: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackageResponse],
  views: Int,
  category: String,
  icon: String,
  community: Boolean)

case class SharedCollectionPackageResponse(
  packageName: String,
  title: String,
  description: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

case class CreateSharedCollectionResponse(
  statusCode: Int,
  newSharedCollection: CreateSharedCollection)

case class CreateSharedCollection(
  name: String,
  description: String,
  author: String,
  packages: Seq[String],
  category: String,
  shareLink: String,
  icon: String,
  community: Boolean)
