package cards.nine.services.api.models

case class PackagesByCategory(
  category: String,
  packages: Seq[String])