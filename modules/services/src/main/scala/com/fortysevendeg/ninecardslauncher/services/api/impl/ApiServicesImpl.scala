package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.api.version2.{CollectionUpdateInfo, CollectionsResponse, ServiceMarketHeader}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import monix.eval.Task

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
    device: LoginV1Device) = withV1Config { baseHeader =>
    resolveOption(userNotFoundMessage)(apiServiceV1.login(toUser(email, device), baseHeader)) map {
      case (statusCode, user) => toLoginResponseV1(statusCode, user)
    }
  }

  override def getUserConfigV1()(implicit requestConfig: RequestConfigV1) = withV1Config { baseHeader =>
    val header = baseHeader :+ ((headerDevice, requestConfig.deviceId)) :+ ((headerToken, requestConfig.token))
    resolveOption(userConfigNotFoundMessage)(apiServiceV1.getUserConfig(header)) map {
      case (statusCode, userConfig) => GetUserV1Response(statusCode, toUserConfig(userConfig))
    }
  }

  override def login(
    email: String,
    androidId: String,
    tokenId: String) =
    withConfig {
      resolveOption(userNotAuthenticatedMessage)(apiService.login(version2.LoginRequest(email, androidId, tokenId)))
    } map {
      case (statusCode, loginResponse) => LoginResponse(statusCode, loginResponse.apiKey, loginResponse.sessionToken)
    }

  override def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig) =
    withConfigHeaderOption(installationNotFoundMessage) { header =>
      apiService.installations(version2.InstallationRequest(deviceToken getOrElse ""), header)
    } map {
      case (statusCode, _) => UpdateInstallationResponse(statusCode)
    }

  override def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeaderOption(playAppNotFoundMessage) { header =>
      apiService.categorize(version2.CategorizeRequest(Seq(packageName)), header)
    } map {
      case (statusCode, response) => GooglePlayPackageResponse(statusCode, toCategorizedPackage(packageName, response))
    }

  override def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeader { header =>
      apiService.categorize(version2.CategorizeRequest(packageNames), header)
    } map { response =>
      GooglePlayPackagesResponse(
        statusCode = response.statusCode,
        packages = response.data map toCategorizedPackages getOrElse Seq.empty)
    }

  override def googlePlayPackagesDetail(packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeader { header =>
      apiService.categorizeDetail(version2.CategorizeRequest(packageNames), header)
    } map { response =>
      GooglePlayPackagesDetailResponse(
        statusCode = response.statusCode,
        packages = response.data map toCategorizedDetailPackages getOrElse Seq.empty)
    }

  override def getRecommendedApps(
    category: String,
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeaderOption(categoryNotFoundMessage) { header =>
      apiService.recommendations(category, version2.RecommendationsRequest(filter = None, excludePackages, limit), header)
    } map {
      case (statusCode, recommendation) => RecommendationResponse(statusCode, toRecommendationAppSeq(recommendation.apps))
    }

  override def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeaderOption(categoryNotFoundMessage) { header =>
      apiService.recommendationsByApps(version2.RecommendationsByAppsRequest(packages, filter = None, excludePackages, limit), header)
    } map {
      case (statusCode, recommendation) => RecommendationResponse(statusCode, toRecommendationAppSeq(recommendation.apps))
    }

  override def getSharedCollection(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeaderOption(publishedCollectionsNotFoundMessage) { header =>
      apiService.getCollection(sharedCollectionId, header)
    } map {
      case (statusCode, collection) => SharedCollectionResponse(statusCode, toSharedCollection(collection))
    }

  override def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig) = {

    def serviceCall(header: ServiceMarketHeader): TaskService[ServiceClientResponse[CollectionsResponse]] =
      collectionType.toLowerCase match {
        case "top" =>
          apiService.topCollections(category, offset, limit, header)
        case "latest" =>
          apiService.latestCollections(category, offset, limit, header)
        case _ => TaskService(Task(Left(ApiServiceException(shareCollectionNotFoundMessage))))

      }

    withConfigGooglePlayHeaderOption(shareCollectionNotFoundMessage)(serviceCall) map {
      case (statusCode, response) => SharedCollectionResponseList(statusCode, toSharedCollectionResponseSeq(response.collections))
    }
  }

  override def getPublishedCollections()(implicit requestConfig: RequestConfig) =
    withConfigGooglePlayHeaderOption(publishedCollectionsNotFoundMessage)(apiService.getCollections) map {
      case (statusCode, response) => SharedCollectionResponseList(statusCode, toSharedCollectionResponseSeq(response.collections))
    }

  override def createSharedCollection(
    name: String,
    description: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig) = {

    val request = version2.CreateCollectionRequest(
      name = name,
      author = author,
      description = description,
      icon = icon,
      category = category,
      community = community,
      packages = packages)

    withConfigHeaderOption(errorCreatingCollectionMessage) { header =>
      apiService.createCollection(request, header)
    } map {
      case (statusCode, response) => CreateSharedCollectionResponse(statusCode, response.publicIdentifier)
    }
  }

  override def updateSharedCollection(
    sharedCollectionId: String,
    maybeName: Option[String],
    maybeDescription: Option[String],
    packages: Seq[String])(implicit requestConfig: RequestConfig) = {

    def toUpdateInfo: Option[CollectionUpdateInfo] = maybeName map (name => CollectionUpdateInfo(name, maybeDescription))

    val request = version2.UpdateCollectionRequest(collectionInfo = toUpdateInfo, packages = Some(packages))

    withConfigHeaderOption(errorCreatingCollectionMessage) { header =>
      apiService.updateCollection(sharedCollectionId, request, header)
    } map {
      case (statusCode, response) => UpdateSharedCollectionResponse(statusCode, response.publicIdentifier)
    }
  }

  override def getSubscriptions()(implicit requestConfig: RequestConfig) =
    withConfigHeaderOption(subscriptionsNotFoundMessage)(apiService.getSubscriptions) map {
      case (statusCode, response) => SubscriptionResponseList(statusCode, toSubscriptionResponseSeq(response.subscriptions))
    }

  override def subscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    withConfigHeader[ServiceClientResponse[Unit]] { header =>
      apiService.subscribe(originalSharedCollectionId, header)
    } map (response => SubscribeResponse(response.statusCode))

  override def unsubscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    withConfigHeader[ServiceClientResponse[Unit]] { header =>
      apiService.unsubscribe(originalSharedCollectionId, header)
    } map (response => UnsubscribeResponse(response.statusCode))

  private[this] def withV1Config[T](service: (Seq[(String, String)]) => TaskService[T]): TaskService[T] = {

    def isConfigValid: Boolean = apiServiceV1.baseUrl.nonEmpty &&
      apiServicesConfig.appId.nonEmpty &&
      apiServicesConfig.appKey.nonEmpty

    if (isConfigValid) {
      val header = Seq(
        (headerAppId, apiServicesConfig.appId),
        (headerAppKey, apiServicesConfig.appKey),
        (headerLocalization, apiServicesConfig.localization))
      service(header)
    } else {
      TaskService(Task(Left(ApiServiceV1ConfigurationException("Invalid configuration"))))
    }

  }

  implicit class RequestConfigExt(request: RequestConfig) {
    def toServiceHeader: version2.ServiceHeader =
      version2.ServiceHeader(request.apiKey, request.sessionToken, request.androidId)

    def toGooglePlayHeader: version2.ServiceMarketHeader =
      version2.ServiceMarketHeader(request.apiKey, request.sessionToken, request.androidId, request.marketToken)
  }

  private[this] def withConfig[T](service: TaskService[T]): TaskService[T] =
    if (apiService.baseUrl.nonEmpty) service else {
      TaskService(Task(Left(ApiServiceConfigurationException("Invalid configuration"))))
    }

  private[this] def withConfigHeader[T](
    service: (version2.ServiceHeader) => TaskService[T])(implicit request: RequestConfig): TaskService[T] =
    withConfig {
      service(request.toServiceHeader).resolve[ApiServiceException]
    }

  private[this] def withConfigHeaderOption[T](msg: String = "")
    (service: (version2.ServiceHeader) => TaskService[ServiceClientResponse[T]])
    (implicit request: RequestConfig): TaskService[(Int, T)] =
    withConfig {
      resolveOption(msg)(service(request.toServiceHeader))
    }

  private[this] def withConfigGooglePlayHeader[T](
    service: (version2.ServiceMarketHeader) => TaskService[T])(implicit request: RequestConfig): TaskService[T] =
    withConfig {
      service(request.toGooglePlayHeader).resolve[ApiServiceException]
    }

  private[this] def withConfigGooglePlayHeaderOption[T](msg: String = "")
    (service: (version2.ServiceMarketHeader) => TaskService[ServiceClientResponse[T]])
    (implicit request: RequestConfig): TaskService[(Int, T)] =
    withConfig {
      resolveOption(msg)(service(request.toGooglePlayHeader))
    }

  private[this] def resolveOption[T](msg: String = "")
    (taskService: TaskService[ServiceClientResponse[T]]): TaskService[(Int, T)] =
    taskService.resolveSides(
      mapRight = {
        case ServiceClientResponse(statusCode, Some(value)) => Right((statusCode, value))
        case _ => Left(ApiServiceException(msg))
      },
      mapLeft = e => Left(ApiServiceException(e.getMessage, Some(e))))

}
