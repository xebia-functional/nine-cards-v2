package cards.nine.services.api.models

import cards.nine.models.types.NineCardsCategory

case class PackagesByCategory(
  category: NineCardsCategory,
  packages: Seq[String])