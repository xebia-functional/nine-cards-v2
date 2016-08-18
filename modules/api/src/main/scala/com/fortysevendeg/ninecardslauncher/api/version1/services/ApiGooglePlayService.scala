package com.fortysevendeg.ninecardslauncher.api.version1.services

import com.fortysevendeg.ninecardslauncher.api.version1.model._
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.rest.client.ServiceClient
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import play.api.libs.json.{Reads, Writes}

class ApiGooglePlayService(serviceClient: ServiceClient) {

  private[this] val PrefixGooglePlay = "/googleplay"
  private[this] val PackagePath = "package"
  private[this] val PackagesPath = "packages"
  private[this] val DetailedPackagesPath = "detailed"

  def getGooglePlayPackage(
    packageName: String,
    headers: Seq[(String, String)])
    (implicit reads: Reads[GooglePlayPackage]): CatsService[ServiceClientResponse[GooglePlayPackage]] =
    serviceClient.get[GooglePlayPackage](
      path = s"$PrefixGooglePlay/$PackagePath/$packageName",
      headers = headers,
      reads = Some(reads))

  def getGooglePlayPackages(
    packageRequest: PackagesRequest,
    headers: Seq[(String, String)]
    )(implicit
    reads: Reads[GooglePlayPackages],
    writes: Writes[PackagesRequest]): CatsService[ServiceClientResponse[GooglePlayPackages]] =
    serviceClient.post[PackagesRequest, GooglePlayPackages](
      path = s"$PrefixGooglePlay/$PackagesPath/$DetailedPackagesPath",
      headers = headers,
      body = packageRequest,
      reads = Some(reads))

}
