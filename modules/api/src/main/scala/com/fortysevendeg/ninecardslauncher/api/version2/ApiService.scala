package com.fortysevendeg.ninecardslauncher.api.version2

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  type ApiException = HttpClientException with ServiceClientException

  private[this] val headerAuthToken = "X-Auth-Token"

  private[this] val headerSessionToken = "X-Session-Token"

  private[this] val headerAndroidId = "X-Android-ID"

  private[this] val headerMarketLocalization = "X-Android-Market-Localization"

  private[this] val headerMarketLocalizationValue = "en-US"

  private[this] val headerAndroidMarketToken = "X-Google-Play-Token"

  private[this] val loginPath = "/login"

  private[this] val installationsPath = "/installations"

  private[this] val collectionsPath = "/collections"

  private[this] val latestCollectionsPath = s"$collectionsPath/latest"

  private[this] val topCollectionsPath = s"$collectionsPath/top"

  private[this] val categorizePath = "/applications/categorize"

  def login(request: LoginRequest)(
    implicit reads: Reads[LoginResponse], writes: Writes[LoginRequest]): CatsService[ServiceClientResponse[LoginResponse]] =
    serviceClient.post[LoginRequest, LoginResponse](
      path = loginPath,
      headers = Seq.empty,
      body = request,
      reads = Some(reads))

  def installations(
    request: InstallationRequest,
    header: SimpleHeader)(
    implicit reads: Reads[InstallationResponse], writes: Writes[InstallationRequest]): CatsService[ServiceClientResponse[InstallationResponse]] =
    serviceClient.put[InstallationRequest, InstallationResponse](
      path = installationsPath,
      headers = createHeaders(installationsPath, header),
      body = request,
      reads = Some(reads))

  def latestCollections(
    category: String,
    offset: Int,
    limit: Int,
    header: HeaderWithMarketToken)(
    implicit reads: Reads[CollectionsResponse]): CatsService[ServiceClientResponse[CollectionsResponse]] = {

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
    header: HeaderWithMarketToken)(
    implicit reads: Reads[CollectionsResponse]): CatsService[ServiceClientResponse[CollectionsResponse]] = {

    val path = s"$topCollectionsPath/$category/$offset/$limit"

    serviceClient.get[CollectionsResponse](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def createCollection(
    request: CreateCollectionRequest,
    header: SimpleHeader)(
    implicit reads: Reads[CreateCollectionResponse], writes: Writes[CreateCollectionRequest]): CatsService[ServiceClientResponse[CreateCollectionResponse]] =
    serviceClient.post[CreateCollectionRequest, CreateCollectionResponse](
      path = collectionsPath,
      headers = createHeaders(collectionsPath, header),
      body = request,
      reads = Some(reads))

  def updateCollection(
    publicIdentifier: String,
    request: UpdateCollectionRequest,
    header: SimpleHeader)(
    implicit reads: Reads[UpdateCollectionResponse], writes: Writes[UpdateCollectionRequest]): CatsService[ServiceClientResponse[UpdateCollectionResponse]] = {

    val path = s"$collectionsPath/$publicIdentifier"

    serviceClient.put[UpdateCollectionRequest, UpdateCollectionResponse](
      path = path,
      headers = createHeaders(path, header),
      body = request,
      reads = Some(reads))
  }

  def getCollection(
    publicIdentifier: String,
    header: HeaderWithMarketToken)(
    implicit reads: Reads[Collection]): CatsService[ServiceClientResponse[Collection]] = {

    val path = s"$collectionsPath/$publicIdentifier"

    serviceClient.get[Collection](
      path = path,
      headers = createHeaders(path, header),
      reads = Some(reads))
  }

  def getCollections(header: HeaderWithMarketToken)(
    implicit reads: Reads[CollectionsResponse]): CatsService[ServiceClientResponse[CollectionsResponse]] =
    serviceClient.get[CollectionsResponse](
      path = collectionsPath,
      headers = createHeaders(collectionsPath, header),
      reads = Some(reads))

  def categorize(
    request: CategorizeRequest,
    header: HeaderWithMarketToken)(
    implicit reads: Reads[CategorizeResponse], writes: Writes[CategorizeRequest]): CatsService[ServiceClientResponse[CategorizeResponse]] =
    serviceClient.post[CategorizeRequest, CategorizeResponse](
      path = categorizePath,
      headers = createHeaders(categorizePath, header),
      body = request,
      reads = Some(reads))

  private[this] def createHeaders(
    path: String,
    header: Header): Seq[(String, String)] = {

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
      (headerAuthToken, hashMac(header.apiKey, serviceClient.baseUrl.concat(path))),
      (headerSessionToken, header.sessionToken),
      (headerAndroidId, header.androidId),
      (headerMarketLocalization, headerMarketLocalizationValue)) ++
    (header.androidMarketToken map ((headerAndroidMarketToken, _))).toSeq
  }

}
