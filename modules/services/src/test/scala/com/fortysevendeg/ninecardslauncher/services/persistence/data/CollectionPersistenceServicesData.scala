package cards.nine.services.persistence.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.repository.model.{Collection, CollectionData}

trait CollectionPersistenceServicesData {

  def collectionData(num: Int = 0) = CollectionData(
    position = collectionPosition + num,
    name = collectionName + num,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryStr),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  val repoCollectionData: CollectionData = collectionData(0)
  val seqRepoCollectionData = Seq(collectionData(0), collectionData(1), collectionData(2))

  def collection(num: Int = 0) = Collection(
    id = collectionId + num,
    data = repoCollectionData)

  val repoCollection: Collection = collection(0)
  val seqRepoCollection = Seq(collection(0), collection(1), collection(2))

}
