package com.fortysevendeg.ninecardslauncher.services.api.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.api.version2.{CollectionUpdateInfo, CollectionsResponse}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._

import scalaz.concurrent.Task

case class ApiServicesConfig(appId: String, appKey: String, localization: String)

class ApiServicesImpl(
  apiServicesConfig: ApiServicesConfig,
  apiService: version2.ApiService,
  apiServiceV1: version1.ApiService)
  extends ApiServices
  with Conversions
  with ImplicitsApiServiceExceptions {

  import version1.JsonImplicits._
  import version2.JsonImplicits._

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

  val userNotAuthenticatedMessage = "User not authenticated"

  val installationNotFoundMessage = "Installation not found"

  val playAppNotFoundMessage = "Google Play Package not found"

  val userConfigNotFoundMessage = "User configuration not found"

  val categoryNotFoundMessage = "Google Play Category not found"

  val subscriptionsNotFoundMessage = "Subscriptions not found"

  val errorCreatingCollectionMessage = "Unknown error creating collection"

  val shareCollectionNotFoundMessage = "Shared Collections not found"

  val createSharedCollectionNotFoundMessage = "Shared Collection not found"

  val publishedCollectionsNotFoundMessage = "Published Collections not found"

  override def loginV1(
    email: String,
    device: LoginV1Device) =
    (for {
      response <- apiServiceV1.login(toUser(email, device), baseHeader)
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

  override def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig) = {
    (for {
      response <- apiService.installations(version2.InstallationRequest(deviceToken getOrElse ""), requestConfig.toServiceHeader)
      installation <- readOption(response.data, installationNotFoundMessage)
    } yield UpdateInstallationResponse(response.statusCode)).resolve[ApiServiceException]
  }

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
      response <- apiServiceV1.getUserConfig(requestConfig.toHeader)
      userConfig <- readOption(response.data, userConfigNotFoundMessage)
    } yield GetUserV1Response(response.statusCode, toUserConfig(userConfig))).resolve[ApiServiceException]

  override def getRecommendedApps(
    category: String,
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.recommendations(category, version2.RecommendationsRequest(filter = None, excludePackages, limit), requestConfig.toGooglePlayHeader)
      recommendation <- readOption(response.data, categoryNotFoundMessage)
    } yield RecommendationResponse(response.statusCode, toRecommendationAppSeq(recommendation.apps))).resolve[ApiServiceException]

  override def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.recommendationsByApps(version2.RecommendationsByAppsRequest(packages, filter = None, excludePackages, limit), requestConfig.toGooglePlayHeader)
      recommendation <- readOption(response.data, categoryNotFoundMessage)
    } yield RecommendationResponse(response.statusCode, toRecommendationAppSeq(recommendation.apps))).resolve[ApiServiceException]

  override def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig) = {

    def serviceCall: TaskService[ServiceClientResponse[CollectionsResponse]] =
      collectionType.toLowerCase match {
        case "top" =>
          apiService.topCollections(category, offset, limit, requestConfig.toGooglePlayHeader).resolve[ApiServiceException]
        case "latest" =>
          apiService.latestCollections(category, offset, limit, requestConfig.toGooglePlayHeader).resolve[ApiServiceException]
        case _ => TaskService(Task(Xor.left(ApiServiceException(shareCollectionNotFoundMessage))))

      }

    for {
      response <- serviceCall
      sharedCollections <- readOption(response.data, shareCollectionNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(sharedCollections.collections))
  }

  override def getPublishedCollections()(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.getCollections(header = requestConfig.toGooglePlayHeader)
      publishedCollections <- readOption(response.data, publishedCollectionsNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(publishedCollections.collections))).resolve[ApiServiceException]

  override def createSharedCollection(
    name: String,
    description: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.createCollection(version2.CreateCollectionRequest(
        name = name,
        author = author,
        description = description,
        icon = icon,
        category = category,
        community = community,
        packages = packages), requestConfig.toServiceHeader)
      createdCollection <- readOption(response.data, errorCreatingCollectionMessage)
    } yield CreateSharedCollectionResponse(response.statusCode, createdCollection.publicIdentifier)).resolve[ApiServiceException]

  override def updateSharedCollection(
    sharedCollectionId: String,
    maybeName: Option[String],
    maybeDescription: Option[String],
    packages: Seq[String])(implicit requestConfig: RequestConfig) = {

    def toUpdateInfo: Option[CollectionUpdateInfo] = maybeName map (name => CollectionUpdateInfo(name, maybeDescription))

    (for {
      response <- apiService.updateCollection(
        publicIdentifier = sharedCollectionId,
        request = version2.UpdateCollectionRequest(collectionInfo = toUpdateInfo, packages = Some(packages)),
        header = requestConfig.toServiceHeader)
      createdCollection <- readOption(response.data, errorCreatingCollectionMessage)
    } yield UpdateSharedCollectionResponse(response.statusCode, createdCollection.publicIdentifier)).resolve[ApiServiceException]
  }

  override def getSubscriptions()(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.getSubscriptions(requestConfig.toServiceHeader)
      subscriptionsResponse <- readOption(response.data, subscriptionsNotFoundMessage)
    } yield SubscriptionResponseList(response.statusCode, toSubscriptionResponseSeq(subscriptionsResponse.subscriptions))).resolve[ApiServiceException]

  override def subscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.subscribe(originalSharedCollectionId, requestConfig.toServiceHeader)
    } yield SubscribeResponse(response.statusCode)).resolve[ApiServiceException]

  override def unsubscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    (for {
      response <- apiService.unsubscribe(originalSharedCollectionId, requestConfig.toServiceHeader)
    } yield UnsubscribeResponse(response.statusCode)).resolve[ApiServiceException]

  implicit class RequestHeaderHeader(request: RequestConfigV1) {
    def toHeader: Seq[(String, String)] =
      baseHeader :+ ((headerDevice, request.deviceId)) :+ ((headerToken, request.token))
  }

  implicit class RequestConfigExt(request: RequestConfig) {
    def toServiceHeader: version2.ServiceHeader =
      version2.ServiceHeader(request.apiKey, request.sessionToken, request.androidId)

    def toGooglePlayHeader: version2.ServiceMarketHeader =
      version2.ServiceMarketHeader(request.apiKey, request.sessionToken, request.androidId, request.marketToken)
  }

  private[this] def readOption[T](maybe: Option[T], msg: String = ""): TaskService[T] = TaskService {
    Task {
      maybe match {
        case Some(v) => Xor.right(v)
        case _ => Xor.left(ApiServiceException(msg))
      }
    }
  }

}
