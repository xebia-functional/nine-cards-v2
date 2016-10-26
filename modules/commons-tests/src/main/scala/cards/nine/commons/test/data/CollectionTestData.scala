package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.commons.test.data.SharedCollectionValues._
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
    appsCategory = Option(category),
    cards = Seq(card(0), card(1), card(2)),
    moment = Option(moment(num)),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId + num),
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val collection: Collection = collection(0)
  val seqCollection: Seq[Collection] = Seq(collection(0), collection(1), collection(2))

  val collectionData: CollectionData = collection.toData
  val seqCollectionData: Seq[CollectionData] = seqCollection map (_.toData)

  val availableMoments =
    Seq((moment(0), collection(0)),
      (moment(1), collection(1)),
      (moment(2), collection(2)))
}
