package cards.nine.models

case class CategorizedDetailPackage(
  packageName: String,
  title: String,
  category: Option[String],
  icon: String,
  free: Boolean,
  downloads: String,
  stars: Double)

case class CategorizedPackage(
  packageName: String,
  category: Option[String])

case class RankApps(
  category: String,
  packages: Seq[String])

case class RecommendationApp(
  packageName: String,
  name: String,
  downloads: String,
  icon: String,
  stars: Double,
  free: Boolean,
  screenshots: Seq[String])

case class RequestConfigV1(
  deviceId: String,
  token: String,
  marketToken: Option[String])

case class RequestConfig(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  marketToken: Option[String] = None)

case class SharedCollection(
  id: String,
  sharedCollectionId: String,
  publishedOn: Long,
  author: String,
  name: String,
  packages: Seq[String],
  resolvedPackages: Seq[SharedCollectionPackage],
  views: Int,
  subscriptions: Option[Int],
  category: String,
  icon: String,
  community: Boolean)

case class SharedCollectionPackage(
  packageName: String,
  title: String,
  icon: String,
  stars: Double,
  downloads: String,
  free: Boolean)

