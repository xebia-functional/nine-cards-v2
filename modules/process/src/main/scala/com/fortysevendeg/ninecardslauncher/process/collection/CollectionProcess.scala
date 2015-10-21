package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models._

trait CollectionProcess {

  /**
   * Creates Collections with the apps installed in the device and their categories, finally it adds a Collection with the favourite contacts if it's possible
   * @param apps the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedApp] with the apps' data
   * @param contacts the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.UnformedContact] with the contacts' data
   * @return the List[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]
   * @throws CollectionException if there was an error creating the existing collections
   */
  def createCollectionsFromUnformedItems(apps: Seq[UnformedApp], contacts: Seq[UnformedContact])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]

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
   * @param collectionId the Id of the Collection
   * @throws CollectionException if there was an error finding the collection, getting the existing collections, deleting the collection or updating the rest of them
   */
  def deleteCollection(collectionId: Int): ServiceDef2[Unit, CollectionException]

  /**
   * Moves a Collection to another position and updates the position of the other Collections
   * @param position the position of the Collection to move
   * @param newPosition the new position of the Collection
   * @throws CollectionException if there was an error finding the collection, getting the existing collections or updating the position of all the collections
   */
  def reorderCollection(position: Int, newPosition: Int): ServiceDef2[Unit, CollectionException]

  /**
   * Edits a Collection and allows to change the name and the appsCategory of the Collection
   * @param collectionId the Id of the Collection
   * @param editCollectionRequest includes the data that can be edit in a collection (name, icon, themedColorIndex and appsCategory)
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Collection]]
   * @throws CollectionException if there was an error finding the collection or updating it
   */
  def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest): ServiceDef2[Collection, CollectionException]

  /**
   * Adds some new Cards after the last existing one in a given Collection
   * @param collectionId the Id of the Collection
   * @param addCardListRequest the Seq[com.fortysevendeg.ninecardslauncher.process.collection.AddCardRequest] includes the necessary data to create a new Card (term, packageName, intent and imagePath)
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.collection.models.Card] of the new cards
   * @throws CardException if there was an error getting the existing cards or adding the new one
   */
  def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]): ServiceDef2[Seq[Card], CardException]

  /**
   * Deletes a Card and updates the position of the other Cards in the Collection
   * @param collectionId the Id of the Collection
   * @param cardId the Id of the Card to delete
   * @throws CardException if there was an error finding the card, getting the existing collection's cards, deleting the card or updating the rest of them
   */
  def deleteCard(collectionId: Int, cardId: Int): ServiceDef2[Unit, CardException]

  /**
   * Moves a Card to another position and updates the position of the other Cards in the Collection
   * @param collectionId the Id of the Collection
   * @param cardId the Id of the Card to delete
   * @param newPosition the new position of the Card
   * @throws CardException if there was an error finding the card, getting the existing cards or updating the position of all the cards
   */
  def reorderCard(collectionId: Int, cardId: Int, newPosition: Int): ServiceDef2[Unit, CardException]

  /**
   * Edits a Card and allows to change its name
   * @param collectionId the Id of the Collection
   * @param cardId the Id of the Card to delete
   * @param name the new name of the Card
   * @return the [[com.fortysevendeg.ninecardslauncher.process.collection.models.Card]]
   * @throws CardException if there was an error finding the card or updating it
   */
  def editCard(collectionId: Int, cardId: Int, name: String): ServiceDef2[Card, CardException]

  /**
    * Convert cards not installed in card from a package name
    * @param packageName package name of app that we want to convert
    * @return [Unit]
    * @throws CardException if there was an error finding the card or updating it
    */
  def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport): ServiceDef2[Unit, CardException]
}
