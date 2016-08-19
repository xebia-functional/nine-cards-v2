package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.version1.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.version1.reads.GooglePlayImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.RecommendationImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.SharedCollectionImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.UserConfigImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.reads.UserImplicits._
import com.fortysevendeg.ninecardslauncher.api.version1.services._
import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.api.version2.CollectionsResponse
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import rapture.core.{Answer, Errata}

import scalaz.concurrent.Task

case class ApiServicesConfig(appId: String, appKey: String, localization: String)

class ApiServicesImpl(
  apiServicesConfig: ApiServicesConfig,
  apiService: version2.ApiService,
  apiUserService: ApiUserService,
  googlePlayService: ApiGooglePlayService,
  userConfigService: ApiUserConfigService,
  recommendationService: ApiRecommendationService,
  sharedCollectionsService: ApiSharedCollectionsService)
  extends ApiServices
  with Conversions
  with ImplicitsApiServiceExceptions {

  import version2.JsonImplicits._

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

  val userNotAuthenticatedMessage = "User not authenticated"

  val installationNotFoundMessage = "Installation not found"

  val playAppNotFoundMessage = "Google Play Package not found"

  val userConfigNotFoundMessage = "User configuration not found"

  val categoryNotFoundMessage = "Google Play Category not found"

  val shareCollectionNotFoundMessage = "Shared Collections not found"

  val createSharedCollectionNotFoundMessage = "Shared Collection not found"

  override def loginV1(
    email: String,
    device: GoogleDevice) =
    (for {
      response <- apiUserService.login(toUser(email, device), baseHeader)
      user <- readOption(response.data, userNotFoundMessage)
    } yield toLoginResponseV1(response.statusCode, user)).resolve[ApiServiceException]

  override def login(
    email: String,
    androidId: String,
    tokenId: String) =
    (for {
      serviceClientResponse <- apiService.login(version2.LoginRequest(email, androidId, tokenId))
      loginResponse <- readOption(serviceClientResponse.data, userNotAuthenticatedMessage)
    } yield LoginResponse(loginResponse.apiKey, loginResponse.sessionToken)).resolve[ApiServiceException]

  override def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.installations(version2.InstallationRequest(deviceToken getOrElse ""), requestConfig.toServiceHeader)
      installation <- readOption(response.data, installationNotFoundMessage)
    } yield UpdateInstallationResponse(response.statusCode)).resolve[ApiServiceException]

  override def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.categorize(version2.CategorizeRequest(Seq(packageName)), requestConfig.toGooglePlayHeader)
      categorizeResponse <- readOption(response.data, playAppNotFoundMessage)
    } yield GooglePlayPackageResponse(response.statusCode, toCategorizedPackage(packageName, categorizeResponse))).resolve[ApiServiceException]

  override def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.categorize(version2.CategorizeRequest(packageNames), requestConfig.toGooglePlayHeader)
    } yield GooglePlayPackagesResponse(
        statusCode = response.statusCode,
        packages = response.data map toCategorizedPackages getOrElse Seq.empty)).resolve[ApiServiceException]

  override def getUserConfigV1()(implicit requestConfig: RequestConfigV1) =
    (for {
      response <- userConfigService.getUserConfig(requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield GetUserConfigResponse(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def getRecommendedApps(
    categories: Seq[String],
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfigV1) =
    (for {
      response <- recommendationService.getRecommendedApps(
        toRecommendationRequest(categories, likePackages, excludePackages, limit), requestConfig.toHeader)
      recommendation <- readOption(response.data, categoryNotFoundMessage)
    } yield RecommendationResponse(response.statusCode, toPlayAppSeq(recommendation))).resolve[ApiServiceException]

  override def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig) = {

    def serviceCall: ServiceDef2[ServiceClientResponse[CollectionsResponse], ApiServiceException] =
      collectionType.toLowerCase match {
        case "top" =>
          apiService.topCollections(category, offset, limit, requestConfig.toGooglePlayHeader).resolve[ApiServiceException]
        case "latest" =>
          apiService.latestCollections(category, offset, limit, requestConfig.toGooglePlayHeader).resolve[ApiServiceException]
        case _ => Service(Task(Errata(ApiServiceException(shareCollectionNotFoundMessage))))

      }

    for {
      response <- serviceCall
      sharedCollections <- readOption(response.data, shareCollectionNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(sharedCollections.collections))
  }

  override def createSharedCollection(
    name: String,
    description: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfigV1) =
    (for {
      response <- sharedCollectionsService.shareCollection(toShareCollection(description, author, name, packages, category, icon, community), requestConfig.toHeader)
      createdCollection <- readOption(response.data, shareCollectionNotFoundMessage)
    } yield CreateSharedCollectionResponse(response.statusCode, toCreateSharedCollection(createdCollection))).resolve[ApiServiceException]

  implicit class RequestHeaderHeader(request: RequestConfigV1) {
    def toHeader: Seq[(String, String)] =
      baseHeader :+ ((headerDevice, request.deviceId)) :+ ((headerToken, request.token))

    def toGooglePlayHeader: Seq[(String, String)] = request.marketToken.map((headerGooglePlayToken, _)) ++: toHeader
  }

  implicit class RequestConfigExt(request: RequestConfig) {
    def toServiceHeader: version2.ServiceHeader =
      version2.ServiceHeader(request.apiKey, request.sessionToken, request.androidId)

    def toGooglePlayHeader: version2.ServiceMarketHeader =
      version2.ServiceMarketHeader(request.apiKey, request.sessionToken, request.androidId, request.marketToken)
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
