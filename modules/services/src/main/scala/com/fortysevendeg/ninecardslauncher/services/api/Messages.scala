package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.api.models._

case class RequestConfigV1(deviceId: String, token: String, marketToken: Option[String])

case class RequestConfig(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  marketToken: Option[String] = None)

case class LoginResponse(
  apiKey: String,
  sessionToken: String)

case class LoginResponseV1(
  statusCode: Int,
  userId: Option[String],
  sessionToken: Option[String],
  email: Option[String],
  devices: Seq[GoogleDevice])

case class InstallationResponse(
  statusCode: Int,
  installation: Installation)

case class UpdateInstallationResponse(
  statusCode: Int)

case class GooglePlayPackageResponse(
  statusCode: Int,
  app: CategorizedPackage)

case class CategorizedPackage(
  packageName: String,
  category: Option[String])

case class GooglePlayPackagesResponse(
  statusCode: Int,
  packages: Seq[CategorizedPackage])

trait UserConfigResponse {
  def statusCode: Int

  def userConfig: UserConfig
}

case class GetUserConfigResponse(
  statusCode: Int,
  userConfig: UserConfig) extends UserConfigResponse

case class RecommendationResponse(
  statusCode: Int,
  seq: Seq[RecommendationApp])

case class RecommendationApp(
  packageName: String,
  name: String,
  downloads: String,
  icon: String,
  stars: Double,
  free: Boolean,
  description: String,
  screenshots: Seq[String]
)

case class SharedCollectionResponseList(
  statusCode: Int,
  items: Seq[SharedCollectionResponse])

case class SharedCollectionResponse(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  description: String,
  author: String,
  name: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackageResponse],
  views: Int,
  category: String,
  icon: String,
  community: Boolean)

case class SharedCollectionPackageResponse(
  packageName: String,
  title: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

case class CreateSharedCollectionResponse(
  statusCode: Int,
  sharedCollectionId: String)
