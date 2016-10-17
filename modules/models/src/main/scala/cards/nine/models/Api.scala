package cards.nine.models

case class CategorizedPackage(
  packageName: String,
  category: Option[String])

case class CategorizedDetailPackage(
  packageName: String,
  title: String,
  category: Option[String],
  icon: String,
  free: Boolean,
  downloads: String,
  stars: Double)

