package cards.nine.services.persistence.data

import cards.nine.commons.test.data.CollectionValues._
import cards.nine.repository.model.{Collection, CollectionData}

trait CollectionPersistenceServicesData {

  def repoCollectionData(num: Int = 0) = CollectionData(
    position = collectionPosition + num,
    name = collectionName + num,
    collectionType = collectionType.name,
    icon = icon,
    themedColorIndex = themedColorIndex,
    appsCategory = Option(appsCategoryStr),
    originalSharedCollectionId = Option(originalSharedCollectionId),
    sharedCollectionId = Option(sharedCollectionId),
    sharedCollectionSubscribed = Option(sharedCollectionSubscribed))

  val repoCollectionData: CollectionData = repoCollectionData(0)
  val seqRepoCollectionData = Seq(repoCollectionData(0), repoCollectionData(1), repoCollectionData(2))

  def repoCollection(num: Int = 0) = Collection(
    id = collectionId + num,
    data = repoCollectionData)

  val repoCollection: Collection = repoCollection(0)
  val seqRepoCollection = Seq(repoCollection(0), repoCollection(1), repoCollection(2))

}
