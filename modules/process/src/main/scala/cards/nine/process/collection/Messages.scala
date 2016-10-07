package cards.nine.process.collection

import cards.nine.models.NineCardsIntent
import cards.nine.models.types.{CardType, CollectionType, NineCardsCategory, NineCardsMoment}

case class CollectionProcessConfig(
  namesCategories: Map[NineCardsCategory, String])

//case class AddCollectionRequest(
//  name: String,
//  collectionType: CollectionType,
//  icon: String,
//  themedColorIndex: Int,
//  appsCategory: Option[NineCardsCategory] = None,
//  cards: Seq[AddCardRequest],
//  moment: Option[NineCardsMoment],
//  originalSharedCollectionId: Option[String] = None,
//  sharedCollectionId: Option[String] = None,
//  sharedCollectionSubscribed: Option[Boolean] = None)
//
//
//case class EditCollectionRequest(
//  name: String,
//  icon: String,
//  themedColorIndex: Int,
//  appsCategory: Option[NineCardsCategory] = None)

