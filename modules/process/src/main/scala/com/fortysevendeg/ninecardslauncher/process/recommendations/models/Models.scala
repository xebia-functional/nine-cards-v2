package cards.nine.process.recommendations.models

case class RecommendedApp(
  packageName: String,
  title: String,
  icon: Option[String],
  downloads: String,
  stars: Double,
  free: Boolean,
  screenshots: Seq[String])