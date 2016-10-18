package cards.nine.models

import cards.nine.models.types.NineCardsCategory

case class CollectionProcessConfig(
  namesCategories: Map[NineCardsCategory, String])