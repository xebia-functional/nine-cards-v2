package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models._

trait CollectionTestData
  extends CardTestData
  with MomentTestData {

  def collection(num: Int = 0) = Collection(
    id = collectionId + num,
    position = collectionPosition + num,
    name = collectionName + num,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    cards = Seq(card(0), card(1), card(2)),
    moment = Option(moment(num)),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val collection: Collection = collection(0)
  val seqCollection: Seq[Collection] = Seq(collection(0), collection(1), collection(2))

  def collectionData(num: Int = 0) = CollectionData(
    position = collectionPosition + num,
    name = collectionName + num,
    collectionType = collectionType,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategory),
    cards = Seq(cardData(0), cardData(1), cardData(2)),
    moment = Option(momentData(num)),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val collectionData: CollectionData = collectionData(0)
  val seqCollectionData: Seq[CollectionData] = Seq(collectionData(0), collectionData(1), collectionData(2))

}
