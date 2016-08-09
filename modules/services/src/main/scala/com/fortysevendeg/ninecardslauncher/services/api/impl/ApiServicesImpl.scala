package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.version1.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.version1.reads.GooglePlayImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.RecommendationImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.SharedCollectionImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.UserConfigImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.UserImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.services._
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
  recommendationService: ApiRecommendationService,
  sharedCollectionsService: ApiSharedCollectionsService)
  extends ApiServices
  with Conversions
  with ImplicitsApiServiceExceptions {

  val headerAppId = "X-Appsly-Application-Id"

  val headerAppKey = "X-Appsly-REST-API-Key"

  val headerDevice = "X-Android-ID"

  val headerToken = "X-Appsly-Session-Token"

  val headerLocalization = "X-Android-Market-Localization"

  val headerGooglePlayToken = "X-Google-Play-Token"

  val baseHeader: Seq[(String, String)] = Seq(
    (headerAppId, apiServicesConfig.appId),
    (headerAppKey, apiServicesConfig.appKey),
    (headerLocalization, apiServicesConfig.localization))

  val userNotFoundMessage = "User not found"

  val installationNotFoundMessage = "Installation not found"

  val playAppNotFoundMessage = "Google Play Package not found"

  val userConfigNotFoundMessage = "User configuration not found"

  val categoryNotFoundMessage = "Google Play Category not found"

  val shareCollectionNotFoundMessage = "Shared Collections not found"

  val createSharedCollectionNotFoundMessage = "Shared Collection not found"

  override def login(
    email: String,
    device: GoogleDevice) =
    (for {
      response <- apiUserService.login(toUser(email, device), baseHeader)
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
      response <- googlePlayService.getGooglePlayPackage(packageName, requestConfig.toGooglePlayHeader)
      playApp <- readOption(response.data, playAppNotFoundMessage)
    } yield GooglePlayPackageResponse(response.statusCode, toGooglePlayApp(playApp.docV2))).resolve[ApiServiceException]

  override def googlePlayPackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- googlePlayService.getGooglePlayPackages(PackagesRequest(packageNames), requestConfig.toGooglePlayHeader)
    } yield GooglePlayPackagesResponse(
        statusCode = response.statusCode,
        packages = response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)).resolve[ApiServiceException]

  override def getUserConfig()(implicit requestConfig: RequestConfig) =
    (for {
      response <- userConfigService.getUserConfig(requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield GetUserConfigResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def getRecommendedApps(
    categories: Seq[String],
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    (for {
      response <- recommendationService.getRecommendedApps(
        toRecommendationRequest(categories, likePackages, excludePackages, limit), requestConfig.toHeader)
      recommendation <- readOption(response.data, categoryNotFoundMessage)
    } yield RecommendationResponse(response.statusCode, toPlayAppSeq(recommendation))).resolve[ApiServiceException]

  override def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig) =
    (for {
      response <- sharedCollectionsService.getSharedCollectionListByCategory(
        collectionType, category, offset, limit, requestConfig.toHeader)
      sharedCollections <- readOption(response.data, shareCollectionNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(sharedCollections.items))).resolve[ApiServiceException]

  override def createSharedCollection(
    name: String,
    description: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig) =
    (for {
      response <- sharedCollectionsService.shareCollection(toShareCollection(description, author, name, packages, category, icon, community), requestConfig.toHeader)
      createdCollection <- readOption(response.data, shareCollectionNotFoundMessage)
    } yield CreateSharedCollectionResponse(response.statusCode, toCreateSharedCollection(createdCollection))).resolve[ApiServiceException]

  implicit class RequestHeaderHeader(request: RequestConfig) {
    def toHeader: Seq[(String, String)] =
      baseHeader :+ ((headerDevice, request.deviceId)) :+ ((headerToken, request.token))

    def toGooglePlayHeader: Seq[(String, String)] = request.androidToken.map((headerGooglePlayToken, _)) ++: toHeader
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
