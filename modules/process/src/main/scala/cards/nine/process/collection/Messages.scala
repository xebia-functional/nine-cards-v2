package cards.nine.process.collection

import cards.nine.models.types.{CardType, CollectionType, NineCardCategory, NineCardsMoment}
import cards.nine.process.commons.models.NineCardIntent

case class CollectionProcessConfig(
  namesCategories: Map[NineCardCategory, String])

case class AddCollectionRequest(
  name: String,
  collectionType: CollectionType,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None,
  cards: Seq[AddCardRequest],
  moment: Option[NineCardsMoment],
  originalSharedCollectionId: Option[String] = None,
  sharedCollectionId: Option[String] = None,
  sharedCollectionSubscribed: Option[Boolean] = None)

case class AddCardRequest(
  term: String,
  packageName: Option[String],
  cardType: CardType,
  intent: NineCardIntent,
  imagePath: Option[String]) extends Serializable

case class EditCollectionRequest(
  name: String,
  icon: String,
  themedColorIndex: Int,
  appsCategory: Option[NineCardCategory] = None)

