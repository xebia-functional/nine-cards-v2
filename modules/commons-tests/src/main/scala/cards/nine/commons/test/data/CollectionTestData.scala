package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.FormedValues._
import cards.nine.commons.test.data.MomentValues._
import cards.nine.models._
import cards.nine.models.reads.MomentImplicits
import cards.nine.models.types.NineCardsMoment
import play.api.libs.json.Json

trait CollectionTestData
  extends CardTestData
  with MomentTestData {

  import MomentImplicits._

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
    sharedCollectionId = Option(sharedCollectionId + num),
    sharedCollectionSubscribed = sharedCollectionSubscribed,
    publicCollectionStatus = publicCollectionStatus)

  val collection: Collection = collection(0)
  val seqCollection: Seq[Collection] = Seq(collection(0), collection(1), collection(2))

  val collectionData: CollectionData = collection.toData
  val seqCollectionData: Seq[CollectionData] = seqCollection map (_.toData)

  def formedItem(num: Int) = FormedItem(
    itemType = itemType,
    title = title,
    intent = formedIntent,
    uriImage = Option(uriImage))

  val seqFormedItem: Seq[FormedItem] = Seq(formedItem(0), formedItem(1), formedItem(2))

  def formedMoment(num: Int) = FormedMoment(
    collectionId = Option(collectionId + num),
    timeslot = Json.parse(timeslotJson).as[Seq[MomentTimeSlot]],
    wifi = Seq(wifiSeq(num)),
    headphone = headphone,
    momentType = NineCardsMoment(momentTypeSeq(num)),
    widgets = Option(seqWidgetData))

  val formedMoment: FormedMoment = formedMoment(0)

  def formedCollection(num: Int) = FormedCollection(
    name = collectionName + num,
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = Option(sharedCollectionSubscribed),
    items = seqFormedItem,
    collectionType = collectionType,
    icon = icon,
    category = Option(appsCategory),
    moment = Option(formedMoment))

  val seqFormedCollection: Seq[FormedCollection] = Seq(formedCollection(0), formedCollection(1), formedCollection(2))

  val availableMoments =
    Seq((moment(0), collection(0)),
      (moment(1), collection(1)),
      (moment(2), collection(2)))
}
