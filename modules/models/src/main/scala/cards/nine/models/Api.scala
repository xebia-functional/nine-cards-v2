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

case class LoginResponse(
  apiKey: String,
  sessionToken: String)

case class RankApps(
  category: String,
  packages: Seq[String])

case class RecommendedApp(
  packageName: String,
  title: String,
  icon: Option[String],
  downloads: String,
  stars: Double,
  free: Boolean,
  screenshots: Seq[String])

case class RequestConfig(
  apiKey: String,
  sessionToken: String,
  androidId: String,
  marketToken: Option[String] = None)

case class RequestConfigV1(
  deviceId: String,
  token: String,
  marketToken: Option[String])


