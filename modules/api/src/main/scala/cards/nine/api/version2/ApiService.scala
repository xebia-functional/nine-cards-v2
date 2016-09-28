package cards.nine.api.version2

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.api.rest.client.http.HttpClientException
import cards.nine.api.rest.client.messages.ServiceClientResponse
import cards.nine.api.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  def baseUrl: String = serviceClient.baseUrl

  type ApiException = HttpClientException with ServiceClientException

  private[this] val headerContentType = "Content-Type"

  private[this] val headerContentTypeValue = "application/json"

  private[this] val headerAuthToken = "X-Auth-Token"

  private[this] val headerSessionToken = "X-Session-Token"

  private[this] val headerAndroidId = "X-Android-ID"

  private[this] val headerMarketLocalization = "X-Android-Market-Localization"

  private[this] val headerMarketLocalizationValue = "en-US"

  private[this] val headerAndroidMarketToken = "X-Google-Play-Token"

  private[this] val loginPath = "/login"

  private[this] val installationsPath = "/installations"

  private[this] val collectionsPath = "/collections"

  private[this] val subscriptionsPath = s"$collectionsPath/subscriptions"

  private[this] val latestCollectionsPath = s"$collectionsPath/latest"

  private[this] val topCollectionsPath = s"$collectionsPath/top"

  private[this] val applicationsPath = "/applications"

  private[this] val categorizePath = s"$applicationsPath/categorize"

  private[this] val rankPath = s"$applicationsPath/rank"

  private[this] val categorizeDetailPath = s"$applicationsPath/details"

  private[this] val recommendationsPath = "/recommendations"

  def login(request: ApiLoginRequest)(
    implicit reads: Reads[ApiLoginResponse], writes: Writes[ApiLoginRequest]): TaskService[ServiceClientResponse[ApiLoginResponse]] =
    serviceClient.post[ApiLoginRequest, ApiLoginResponse](
      path = loginPath,
      headers = Seq((headerContentType, headerContentTypeValue)),
      body = request,
      reads = Some(reads))

  def installations(
    request: InstallationRequest,
    header: ServiceHeader)(
    implicit reads: Reads[InstallationResponse], writes: Writes[InstallationRequest]): TaskService[ServiceClientResponse[InstallationResponse]] =
    serviceClient.put[InstallationRequest, InstallationResponse](
      path = installationsPath,
      headers = createHeaders(installationsPath, header),
      body = request,
      reads = Some(reads))

  def latestCollections(
    category: String,
    offset: Int,
    limit: Int,
    header: ServiceMarketHeader)(
    implicit reads: Reads[CollectionsResponse]): TaskService[ServiceClientResponse[CollectionsResponse]] = {

    val path = s"$latestCollectionsPath/$category/$offset/$limit"

    serviceClient.get[CollectionsResponse](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def topCollections(
    category: String,
    offset: Int,
    limit: Int,
    header: ServiceMarketHeader)(
    implicit reads: Reads[CollectionsResponse]): TaskService[ServiceClientResponse[CollectionsResponse]] = {

    val path = s"$topCollectionsPath/$category/$offset/$limit"

    serviceClient.get[CollectionsResponse](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def createCollection(
    request: CreateCollectionRequest,
    header: ServiceHeader)(
    implicit reads: Reads[CreateCollectionResponse], writes: Writes[CreateCollectionRequest]): TaskService[ServiceClientResponse[CreateCollectionResponse]] =
    serviceClient.post[CreateCollectionRequest, CreateCollectionResponse](
      path = collectionsPath,
      headers = createHeaders(collectionsPath, header),
      body = request,
      reads = Some(reads))

  def updateCollection(
    publicIdentifier: String,
    request: UpdateCollectionRequest,
    header: ServiceHeader)(
    implicit reads: Reads[UpdateCollectionResponse], writes: Writes[UpdateCollectionRequest]): TaskService[ServiceClientResponse[UpdateCollectionResponse]] = {

    val path = s"$collectionsPath/$publicIdentifier"

    serviceClient.put[UpdateCollectionRequest, UpdateCollectionResponse](
      path = path,
      headers = createHeaders(path, header),
      body = request,
      reads = Some(reads))
  }

  def getCollection(
    publicIdentifier: String,
    header: ServiceMarketHeader)(
    implicit reads: Reads[Collection]): TaskService[ServiceClientResponse[Collection]] = {

    val path = s"$collectionsPath/$publicIdentifier"

    serviceClient.get[Collection](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def getCollections(header: ServiceMarketHeader)(
    implicit reads: Reads[CollectionsResponse]): TaskService[ServiceClientResponse[CollectionsResponse]] =
    serviceClient.get[CollectionsResponse](
      path = collectionsPath,
      headers = createHeaders(collectionsPath, header),
      reads = Some(reads))

  def categorize(
    request: CategorizeRequest,
    header: ServiceMarketHeader)(
    implicit reads: Reads[CategorizeResponse], writes: Writes[CategorizeRequest]): TaskService[ServiceClientResponse[CategorizeResponse]] =
    serviceClient.post[CategorizeRequest, CategorizeResponse](
      path = categorizePath,
      headers = createHeaders(categorizePath, header),
      body = request,
      reads = Some(reads))

  def categorizeDetail(
    request: CategorizeRequest,
    header: ServiceMarketHeader)(
    implicit reads: Reads[CategorizeDetailResponse], writes: Writes[CategorizeRequest]): TaskService[ServiceClientResponse[CategorizeDetailResponse]] =
    serviceClient.post[CategorizeRequest, CategorizeDetailResponse](
      path = categorizeDetailPath,
      headers = createHeaders(categorizeDetailPath, header),
      body = request,
      reads = Some(reads))

  def recommendations(
    category: String,
    filter: Option[String],
    request: RecommendationsRequest,
    header: ServiceMarketHeader)(
    implicit reads: Reads[RecommendationsResponse], writes: Writes[RecommendationsRequest]): TaskService[ServiceClientResponse[RecommendationsResponse]] = {

    val path = filter match {
      case Some(f) => s"$recommendationsPath/$category/$f"
      case _ => s"$recommendationsPath/$category"
    }

    serviceClient.post[RecommendationsRequest, RecommendationsResponse](
      path = path,
      headers = createHeaders(path, header),
      body = request,
      reads = Some(reads))
  }

  def recommendationsByApps(
    request: RecommendationsByAppsRequest,
    header: ServiceMarketHeader)(
    implicit reads: Reads[RecommendationsByAppsResponse], writes: Writes[RecommendationsByAppsRequest]): TaskService[ServiceClientResponse[RecommendationsByAppsResponse]] = {

    val path = s"$recommendationsPath"

    serviceClient.post[RecommendationsByAppsRequest, RecommendationsByAppsResponse](
      path = path,
      headers = createHeaders(path, header),
      body = request,
      reads = Some(reads))
  }

  def getSubscriptions(
    header: ServiceHeader)(
    implicit reads: Reads[SubscriptionsResponse]): TaskService[ServiceClientResponse[SubscriptionsResponse]] = {

    val path = s"$subscriptionsPath"

    serviceClient.get[SubscriptionsResponse](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def subscribe(
    publicIdentifier: String,
    header: ServiceHeader): TaskService[ServiceClientResponse[Unit]] = {

    val path = s"$subscriptionsPath/$publicIdentifier"

    serviceClient.emptyPut(
      path = path,
      headers = createHeaders(path, header),
      reads = None,
      emptyResponse = true)
  }

  def unsubscribe(
    publicIdentifier: String,
    header: ServiceHeader): TaskService[ServiceClientResponse[Unit]] = {

    val path = s"$subscriptionsPath/$publicIdentifier"

    serviceClient.delete(
      path = path,
      headers = createHeaders(path, header),
      reads = None,
      emptyResponse = true)
  }

  def rankApps(
    request: RankAppsRequest,
    header: ServiceHeader)(
    implicit reads: Reads[RankAppsResponse], writes: Writes[RankAppsRequest]): TaskService[ServiceClientResponse[RankAppsResponse]] = {

    val path = s"$rankPath"

    serviceClient.post[RankAppsRequest, RankAppsResponse](
      path = path,
      headers = createHeaders(path, header),
      body = request,
      reads = Some(reads))
  }

  private[this] def createHeaders[T <: BaseServiceHeader](
    path: String,
    header: T): Seq[(String, String)] = {

    def readAndroidMarketToken: Option[String] = header match {
      case h: ServiceMarketHeader => h.androidMarketToken
      case _ => None
    }

    val algorithm = "HmacSHA512"
    val charset = "UTF-8"

    def hashMac(apiKey: String, url: String): String = {
      val mac = Mac.getInstance(algorithm)
      val secret = new SecretKeySpec(apiKey.getBytes(charset), algorithm)
      mac.init(secret)
      val bytesResult = mac.doFinal(url.getBytes(charset))
      bytesResult.map("%02x".format(_)).mkString
    }

    Seq(
      (headerContentType, headerContentTypeValue),
      (headerAuthToken, hashMac(header.apiKey, serviceClient.baseUrl.concat(path))),
      (headerSessionToken, header.sessionToken),
      (headerAndroidId, header.androidId),
      (headerMarketLocalization, headerMarketLocalizationValue)) ++
    (readAndroidMarketToken map ((headerAndroidMarketToken, _))).toSeq
  }

}
