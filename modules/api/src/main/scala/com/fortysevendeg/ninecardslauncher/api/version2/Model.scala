package com.fortysevendeg.ninecardslauncher.api.version2

trait Header {

  def apiKey: String
  def sessionToken: String
  def androidId: String
  def androidMarketToken: Option[String]

}

case class SimpleHeader(
  apiKey: String,
  sessionToken: String,
  androidId: String) extends Header {
  override def androidMarketToken: Option[String] = None
}

case class HeaderWithMarketToken(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  marketToken: String) extends Header {
  override def androidMarketToken: Option[String] = Some(marketToken)
}

case class LoginRequest(email: String, androidId: String, tokenId: String)

case class LoginResponse(apiKey: String, sessionToken: String)

case class InstallationRequest(deviceToken: String)

case class InstallationResponse(androidId: String, deviceToken: String)

case class CollectionsResponse(collections: Seq[Collection])

case class CreateCollectionRequest(
  name: String,
  author: String,
  description: String,
  icon: String,
  category: String,
  community: Boolean,
  packages: Seq[String])

case class CreateCollectionResponse(publicIdentifier: String, packagesStats: PackagesStats)

case class UpdateCollectionRequest(
  collectionInfo: Option[CollectionUpdateInfo],
  packages: Option[Seq[String]])

case class UpdateCollectionResponse(publicIdentifier: String, packagesStats: PackagesStats)

case class CategorizeRequest(items: Seq[String])

case class CategorizeResponse(errors: Seq[String], items: Seq[CategorizedApp])

case class PackagesStats(added: Int, removed: Option[Int] = None)

case class Collection(
  name: String,
  author: String,
  description: Option[String],
  icon: String,
  category: String,
  community: Boolean,
  publishedOn: String,
  installations: Option[Int],
  views: Option[Int],
  publicIdentifier: String,
  appsInfo: Seq[CollectionApp],
  packages: Seq[String])

case class CollectionApp(
  stars: Double,
  icon: String,
  packageName: String,
  downloads: String,
  category: String,
  title: String,
  free: Boolean)

case class CollectionUpdateInfo(
  title: String,
  description: Option[String])

case class CategorizedApp(
  packageName: String,
  category: String)