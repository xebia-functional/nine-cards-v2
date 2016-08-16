package com.fortysevendeg.ninecardslauncher.api.version2

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiService(serviceClient: ServiceClient) {

  type ApiException = HttpClientException with ServiceClientException

  private[this] val headerAuthToken = "X-Auth-Token"

  private[this] val headerSessionToken = "X-Session-Token"

  private[this] val headerAndroidId = "X-Android-ID"

  private[this] val headerMarketLocalization = "X-Android-Market-Localization"

  private[this] val headerMarketLocalizationValue = "en-US"

  private[this] val loginPath = "/login"

  private[this] val installationsPath = "/installations"

  def login(login: LoginRequest)(
    implicit reads: Reads[LoginResponse], writes: Writes[LoginRequest]): ServiceDef2[ServiceClientResponse[LoginResponse], ApiException] =
    serviceClient.post[LoginRequest, LoginResponse](
      path = loginPath,
      headers = Seq.empty,
      body = login,
      reads = Some(reads))

  def installations(
    installation: InstallationRequest,
    apiKey: String,
    sessionToken: String,
    androidId: String)(
    implicit reads: Reads[InstallationResponse], writes: Writes[InstallationRequest]): ServiceDef2[ServiceClientResponse[InstallationResponse], ApiException] =
    serviceClient.put[InstallationRequest, InstallationResponse](
      path = installationsPath,
      headers = createHeaders(apiKey, installationsPath, sessionToken, androidId),
      body = installation,
      reads = Some(reads))

  private[this] def createHeaders(
    apiKey: String,
    path: String,
    sessionToken: String,
    androidId: String): Seq[(String, String)] = {

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
      (headerAuthToken, hashMac(apiKey, serviceClient.baseUrl.concat(path))),
      (headerSessionToken, sessionToken),
      (headerAndroidId, androidId),
      (headerMarketLocalization, headerMarketLocalizationValue))
  }

}
