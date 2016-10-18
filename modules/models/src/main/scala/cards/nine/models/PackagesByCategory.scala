package cards.nine.models

import cards.nine.models.types.NineCardsCategory

case class PackagesByCategory(
  category: NineCardsCategory,
  packages: Seq[String])