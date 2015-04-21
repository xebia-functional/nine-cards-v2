package com.fortysevendeg.ninecardslauncher.api.services

import com.fortysevendeg.ninecardslauncher.api.model._
import com.fortysevendeg.rest.client.ServiceClient
import play.api.libs.json.{Writes, Reads}

import scala.concurrent.ExecutionContext

trait GooglePlayServiceClient {

  val serviceClient: ServiceClient

  private val PrefixGooglePlay = "/googleplay"
  private val PackagePath = "package"
  private val PackagesPath = "packages"
  private val SearchPath = "search"
  private val SetailedPackagesPath = "detailed"
  private val SimplePackagesPath = "simple"

  def getGooglePlayPackage(packageName: String, headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlayPackage]) =
    serviceClient.get[GooglePlayPackage](
      path = s"$PrefixGooglePlay/$PackagePath/$packageName",
      headers = headers,
      reads = Some(reads))

  def getGooglePlayPackages(
      packageRequest: PackagesRequest,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlayPackages], writes: Writes[PackagesRequest]) =
    serviceClient.post[PackagesRequest, GooglePlayPackages](
      path = s"$PrefixGooglePlay/$PackagesPath/$SetailedPackagesPath",
      headers = headers,
      body = packageRequest,
      reads = Some(reads))

  def getGooglePlaySimplePackages(
      packageRequest: PackagesRequest,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlaySimplePackages], writes: Writes[PackagesRequest]) =
    serviceClient.post[PackagesRequest, GooglePlaySimplePackages](
      path = s"$PrefixGooglePlay/$PackagesPath/$SimplePackagesPath",
      headers = headers,
      body = packageRequest,
      reads = Some(reads))
  
  def searchGooglePlay(
      query: String, 
      offset: Int, 
      limit: Int,
      headers: Seq[(String, String)])(implicit executionContext: ExecutionContext, reads: Reads[GooglePlaySearch]) =
    serviceClient.get[GooglePlaySearch](
      path = s"$PrefixGooglePlay/$SearchPath/$query/$offset/$limit",
      headers = headers,
      reads = Some(reads))

}
