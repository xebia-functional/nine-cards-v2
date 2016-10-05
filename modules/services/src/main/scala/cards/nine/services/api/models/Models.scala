package cards.nine.services.api.models

import cards.nine.models.types.NineCardCategory

case class PackagesByCategory(
  category: NineCardCategory,
  packages: Seq[String])