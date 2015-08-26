package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models._

trait CollectionProcess {

  /**
   * Creates Collections with the apps installed in the device and their categories, finally it adds a Collection with the favourite contacts if it's possible
   * @param apps the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedItem] with the apps' data
   * @return the List[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]
   * @throws CollectionException if there was an error creating the existing collections
   */
  def createCollectionsFromUnformedItems(apps: Seq[UnformedItem])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]

  /**
   * Creates Collections from some already formed and given Collections
   * @param items the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.FormedCollection] of Collections
   * @return the List[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]
   * @throws CollectionException if there was an error creating the collections
   */
  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]

  /**
   * Gets the existing collections
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]
   * @throws CollectionException if there was an error getting the existing collections
   */
  def getCollections: ServiceDef2[Seq[Collection], CollectionException]

  /**
   * Adds a new Collection after the last existing one
   * @param addCollectionRequest includes the necessary data to create a new collection (name, collectionType, icon, themedColorIndex and appsCategory(optional))
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]]
   * @throws CollectionException if there was an error getting the existing collections or adding the new one
   */
  def addCollection(addCollectionRequest: AddCollectionRequest): ServiceDef2[Collection, CollectionException]

  /**
   * Deletes a Collection and updates the position of the other Collections
   * @param deleteCollectionRequest includes the Id of the Collection
   * @throws CollectionException if there was an error finding the collection, getting the existing collections, deleting the collection or updating the rest of them
   */
  def deleteCollection(deleteCollectionRequest: DeleteCollectionRequest): ServiceDef2[Unit, CollectionException]

  /**
   * Moves a Collection to another position and updates the position of the other Collections
   * @param reorderCollectionRequest includes the position of the collection to move and the new position
   * @throws CollectionException if there was an error finding the collection, getting the existing collections or updating the position of all the collections
   */
  def reorderCollection(reorderCollectionRequest: ReorderCollectionRequest): ServiceDef2[Unit, CollectionException]

  /**
   * Edits a Collection and allows to change the name and the appsCategory of the Collection
   * @param editCollectionRequest includes the Id, the new name and the new appsCategory
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]]
   * @throws CollectionException if there was an error finding the collection or updating it
   */
  def editCollection(editCollectionRequest: EditCollectionRequest): ServiceDef2[Collection, CollectionException]

  /**
   * Gets the Cards included in a given Collection
   * @param collectionId the Id of the Collection
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.Card]
   * @throws CardException if there was an error getting the cards
   */
  def getCardsByCollectionId(collectionId: Int) : ServiceDef2[Seq[Card], CardException]

  /**
   * Adds a new Card after the last existing one in a given Collection
   * @param addCardRequest includes the necessary data to create a new Card (collectionId, term, packageName, intent and imagePath)
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Card]]
   * @throws CardException if there was an error getting the existing cards or adding the new one
   */
  def addCard(addCardRequest: AddCardRequest): ServiceDef2[Card, CardException]

  /**
   * Deletes a Card and updates the position of the other Cards in the Collection
   * @param deleteCardRequest includes the Id of the Collection and the Id of the Card delete
   * @throws CardException if there was an error finding the card, getting the existing collection's cards, deleting the card or updating the rest of them
   */
  def deleteCard(deleteCardRequest: DeleteCardRequest): ServiceDef2[Unit, CardException]

  /**
   * Moves a Card to another position and updates the position of the other Cards in the Collection
   * @param reorderCardRequest includes the Id of the Collection and the Id of the Card to move and the new position of the Card
   * @throws CardException if there was an error finding the card, getting the existing cards or updating the position of all the cards
   */
  def reorderCard(reorderCardRequest: ReorderCardRequest): ServiceDef2[Unit, CardException]

  /**
   * Edits a Card and allows to change its name
   * @param editCardRequest includes the Id of the Collection, the Id of the Card to edit and the new name
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Card]]
   * @throws CardException if there was an error finding the card or updating it
   */
  def editCard(editCardRequest: EditCardRequest): ServiceDef2[Card, CardException]
}
