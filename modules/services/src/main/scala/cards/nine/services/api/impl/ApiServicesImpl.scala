package cards.nine.services.api.impl

import cards.nine.api._
import cards.nine.api.rest.client.messages.ServiceClientResponse
import cards.nine.api.version2._
import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.{LoginV1Device, PackagesByCategory}
import cards.nine.services.api._
import monix.eval.Task

case class ApiServicesConfig(appId: String, appKey: String, localization: String)

class ApiServicesImpl(
  apiServicesConfig: ApiServicesConfig,
  apiService: cards.nine.api.version2.ApiService,
  apiServiceV1: cards.nine.api.version1.ApiService)
  extends ApiServices
  with Conversions
  with ImplicitsApiServiceExceptions {

  import cards.nine.api.version1.JsonImplicits._
  import cards.nine.api.version2.JsonImplicits._

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

  val errorRankingAppsMessage = "Unknown error ranking apps"

  override def loginV1(
    email: String,
    device: LoginV1Device) =
    for {
      baseHeader <- prepareV1Header
      response <- apiServiceV1.login(toUser(email, device), baseHeader).readOption(userNotFoundMessage)
    } yield toLoginResponseV1(response.statusCode, response.data)

  override def getUserConfigV1()(implicit requestConfig: RequestConfigV1) =
    for {
      baseHeader <- prepareV1Header
      header = baseHeader :+ ((headerDevice, requestConfig.deviceId)) :+ ((headerToken, requestConfig.token))
      response <- apiServiceV1.getUserConfig(header).readOption(userConfigNotFoundMessage)
    } yield GetUserV1Response(response.statusCode, toUserConfig(response.data))

  override def login(
    email: String,
    androidId: String,
    tokenId: String) =
    for {
      _ <- validateConfig
      response <- apiService
        .login(ApiLoginRequest(email, androidId, tokenId))
        .readOption(userNotAuthenticatedMessage)
    }  yield LoginResponse(response.statusCode, response.data.apiKey, response.data.sessionToken)

  override def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .installations(InstallationRequest(deviceToken getOrElse ""), requestConfig.toServiceHeader)
        .readOption(installationNotFoundMessage)
    } yield UpdateInstallationResponse(response.statusCode)

  override def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .categorize(CategorizeRequest(Seq(packageName)), requestConfig.toGooglePlayHeader)
        .readOption(playAppNotFoundMessage)
    } yield GooglePlayPackageResponse(response.statusCode, toCategorizedPackage(packageName, response.data))

  override def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .categorize(CategorizeRequest(packageNames), requestConfig.toGooglePlayHeader)
        .resolve[ApiServiceException]
    } yield GooglePlayPackagesResponse(
      statusCode = response.statusCode,
      packages = response.data map toCategorizedPackages getOrElse Seq.empty)

  override def googlePlayPackagesDetail(packageNames: Seq[String])(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .categorizeDetail(CategorizeRequest(packageNames), requestConfig.toGooglePlayHeader)
        .resolve[ApiServiceException]
    } yield GooglePlayPackagesDetailResponse(
      statusCode = response.statusCode,
      packages = response.data map toCategorizedDetailPackages getOrElse Seq.empty)

  override def getRecommendedApps(
    category: String,
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .recommendations(category, None, RecommendationsRequest(excludePackages, limit), requestConfig.toGooglePlayHeader)
        .readOption(categoryNotFoundMessage)
    }  yield  RecommendationResponse(response.statusCode, toRecommendationAppSeq(response.data.items))

  override def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .recommendationsByApps(RecommendationsByAppsRequest(packages, excludePackages, limit), requestConfig.toGooglePlayHeader)
        .resolve[ApiServiceException]
      apps = response.data.map(_.apps) getOrElse Seq.empty
    } yield RecommendationResponse(response.statusCode, toRecommendationAppSeq(apps))

  override def getSharedCollection(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .getCollection(sharedCollectionId, requestConfig.toGooglePlayHeader)
        .readOption(publishedCollectionsNotFoundMessage)
    } yield SharedCollectionResponse(response.statusCode, toSharedCollection(response.data))

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

    for {
      _ <- validateConfig
      response <- serviceCall(requestConfig.toGooglePlayHeader)
        .readOption(shareCollectionNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(response.data.collections))
  }

  override def getPublishedCollections()(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .getCollections(requestConfig.toGooglePlayHeader)
        .readOption(publishedCollectionsNotFoundMessage)
    } yield SharedCollectionResponseList(response.statusCode, toSharedCollectionResponseSeq(response.data.collections))

  override def createSharedCollection(
    name: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig) = {

    val request = version2.CreateCollectionRequest(
      name = name,
      author = author,
      icon = icon,
      category = category,
      community = community,
      packages = packages)

    for {
      _ <- validateConfig
      response <- apiService
        .createCollection(request, requestConfig.toServiceHeader)
        .readOption(errorCreatingCollectionMessage)
    } yield CreateSharedCollectionResponse(response.statusCode, response.data.publicIdentifier)
  }

  override def updateSharedCollection(
    sharedCollectionId: String,
    maybeName: Option[String],
    packages: Seq[String])(implicit requestConfig: RequestConfig) = {

    def toUpdateInfo: Option[CollectionUpdateInfo] = maybeName map (name => CollectionUpdateInfo(name))

    val request = version2.UpdateCollectionRequest(collectionInfo = toUpdateInfo, packages = Some(packages))

    for {
      _ <- validateConfig
      response <- apiService
        .updateCollection(sharedCollectionId, request, requestConfig.toServiceHeader)
        .readOption(errorCreatingCollectionMessage)
    } yield UpdateSharedCollectionResponse(response.statusCode, response.data.publicIdentifier)
  }

  override def getSubscriptions()(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .getSubscriptions(requestConfig.toServiceHeader)
        .readOption(subscriptionsNotFoundMessage)
    } yield SubscriptionResponseList(response.statusCode, toSubscriptionResponseSeq(response.data.subscriptions))

  override def subscribe(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService.subscribe(sharedCollectionId, requestConfig.toServiceHeader).resolve[ApiServiceException]
    } yield SubscribeResponse(response.statusCode)

  override def unsubscribe(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService.unsubscribe(sharedCollectionId, requestConfig.toServiceHeader).resolve[ApiServiceException]
    } yield UnsubscribeResponse(response.statusCode)

  override def rankApps(
    packagesByCategorySeq: Seq[PackagesByCategory], location: Option[String])(implicit requestConfig: RequestConfig) =
    for {
      _ <- validateConfig
      response <- apiService
        .rankApps(RankAppsRequest(toItemsMap(packagesByCategorySeq), location), requestConfig.toServiceHeader)
        .readOption(errorRankingAppsMessage)
    } yield RankAppsResponseList(response.statusCode, toRankAppsResponse(response.data.items))

  private[this] def prepareV1Header: TaskService[Seq[(String, String)]] = {

    def isConfigValid: Boolean = apiServiceV1.baseUrl.nonEmpty &&
      apiServicesConfig.appId.nonEmpty &&
      apiServicesConfig.appKey.nonEmpty

    TaskService {
      Task {
        if (isConfigValid) {
          Right(Seq(
            (headerAppId, apiServicesConfig.appId),
            (headerAppKey, apiServicesConfig.appKey),
            (headerLocalization, apiServicesConfig.localization)))
        } else {
          Left(ApiServiceV1ConfigurationException("Invalid configuration"))
        }
      }
    }

  }

  case class ServiceResponse[T](statusCode: Int, data: T)

  implicit class ServiceOptionExt[T](taskService: TaskService[ServiceClientResponse[T]]) {

    def readOption(msg: String): TaskService[ServiceResponse[T]] =
      taskService.resolveSides(
        mapRight = {
          case ServiceClientResponse(statusCode, Some(value)) => Right(ServiceResponse(statusCode, value))
          case _ => Left(ApiServiceException(msg))
        },
        mapLeft = e => Left(ApiServiceException(e.getMessage, Some(e))))

  }

  private[this] def validateConfig: TaskService[Unit] = TaskService {
    Task {
      if (apiService.baseUrl.nonEmpty) Right((): Unit) else Left(ApiServiceConfigurationException("Invalid configuration"))
    }
  }

  implicit class RequestConfigExt(request: RequestConfig) {
    def toServiceHeader: cards.nine.api.version2.ServiceHeader =
      version2.ServiceHeader(request.apiKey, request.sessionToken, request.androidId)

    def toGooglePlayHeader: cards.nine.api.version2.ServiceMarketHeader =
      version2.ServiceMarketHeader(request.apiKey, request.sessionToken, request.androidId, request.marketToken)
  }

}
