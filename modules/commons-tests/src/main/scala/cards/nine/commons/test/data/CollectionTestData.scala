package cards.nine.commons.test.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.models.types._
import cards.nine.models.{Card, Collection, CollectionData, Moment}

trait CollectionTestData
  extends CardTestData
  with MomentTestData {

  def createSeqCollection(
    num: Int = 5,
    id: Int = collectionId,
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    cards: Seq[Card] = seqCard,
    moment: Moment = moment,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    publicCollectionStatus: PublicCollectionStatus = publicCollectionStatus): Seq[Collection] = List.tabulate(num)(
    item =>
      Collection(
        id = id + item,
        position = position,
        name = name,
        collectionType = collectionType,
        icon = icon,
        themedColorIndex = themedColorIndex,
        appsCategory = Option(NineCardsCategory(appsCategory)),
        cards = cards,
        moment = Option(moment),
        originalSharedCollectionId = Option(originalSharedCollectionId),
        sharedCollectionId = Option(sharedCollectionId),
        sharedCollectionSubscribed = sharedCollectionSubscribed,
        publicCollectionStatus = publicCollectionStatus))

  def createCollectionData(
    position: Int = position,
    name: String = name,
    collectionType: CollectionType = collectionType,
    icon: String = icon,
    themedColorIndex: Int = themedColorIndex,
    appsCategory: String = appsCategory,
    originalSharedCollectionId: String = originalSharedCollectionId,
    sharedCollectionId: String = sharedCollectionId,
    sharedCollectionSubscribed: Boolean = sharedCollectionSubscribed,
    cards: Seq[Card] = seqCard): CollectionData =
    CollectionData(
      position = position,
      name = name,
      collectionType = collectionType,
      icon = icon,
      themedColorIndex = themedColorIndex,
      appsCategory = Option(NineCardsCategory(appsCategory)),
      cards = seqCardData,
      moment = Option(createMomentData()),
      originalSharedCollectionId = Option(originalSharedCollectionId),
      sharedCollectionId = Option(sharedCollectionId),
      sharedCollectionSubscribed = sharedCollectionSubscribed)

  def createSeqCollectionData(
    num: Int = 5) :Seq[CollectionData]  =
    List.tabulate(num)(item => createCollectionData())

  val seqCollection: Seq[Collection] = createSeqCollection()
  val collection: Collection = seqCollection(0)

  val collectionData = createCollectionData()
  val seqCollectionData = createSeqCollectionData()

}
