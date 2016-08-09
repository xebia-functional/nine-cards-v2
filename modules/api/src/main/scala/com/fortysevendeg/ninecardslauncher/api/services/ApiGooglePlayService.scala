package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import com.fortysevendeg.rest.client.{ServiceClient, ServiceClientException}
import play.api.libs.json.{Reads, Writes}

class ApiGooglePlayService(serviceClient: ServiceClient) {

  private[this] val PrefixGooglePlay = "/googleplay"
  private[this] val PackagePath = "package"
  private[this] val PackagesPath = "packages"
  private[this] val DetailedPackagesPath = "detailed"

  /**
    * @deprecated v1
    */
  def getGooglePlayPackage(
    packageName: String,
    headers: Seq[(String, String)])
    (implicit reads: Reads[GooglePlayPackage]): ServiceDef2[ServiceClientResponse[GooglePlayPackage], HttpClientException with ServiceClientException] =
    serviceClient.get[GooglePlayPackage](
      path = s"$PrefixGooglePlay/$PackagePath/$packageName",
      headers = headers,
      reads = Some(reads))

  /**
    * @deprecated v1
    */
  def getGooglePlayPackages(
    packageRequest: PackagesRequest,
    headers: Seq[(String, String)]
    )(implicit
    reads: Reads[GooglePlayPackages],
    writes: Writes[PackagesRequest]): ServiceDef2[ServiceClientResponse[GooglePlayPackages], HttpClientException with ServiceClientException] =
    serviceClient.post[PackagesRequest, GooglePlayPackages](
      path = s"$PrefixGooglePlay/$PackagesPath/$DetailedPackagesPath",
      headers = headers,
      body = packageRequest,
      reads = Some(reads))

}
