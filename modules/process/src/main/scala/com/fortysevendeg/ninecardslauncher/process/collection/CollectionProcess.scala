package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.models._

trait CollectionProcess {
  def createCollectionsFromUnformedItems(apps: Seq[UnformedItem])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]
  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport): ServiceDef2[List[Collection], CollectionException]
  def getCollections: ServiceDef2[Seq[Collection], CollectionException]

  def addCollection(addCollectionRequest: AddCollectionRequest): ServiceDef2[Collection, CollectionException]

  def deleteCollection(deleteCollectionRequest: DeleteCollectionRequest): ServiceDef2[Unit, CollectionException]

  def reorderCollection(reorderCollectionRequest: ReorderCollectionRequest): ServiceDef2[Unit, CollectionException]

  def editCollection(editCollectionRequest: EditCollectionRequest): ServiceDef2[Collection, CollectionException]

  def getCardsByCollectionId(collectionId: Int) : ServiceDef2[Seq[Card], CardException]

  def addCard(addCardRequest: AddCardRequest): ServiceDef2[Card, CardException]

  def deleteCard(deleteCardRequest: DeleteCardRequest): ServiceDef2[Unit, CardException]

  def reorderCard(reorderCardRequest: ReorderCardRequest): ServiceDef2[Unit, CardException]

  def editCard(editCardRequest: EditCardRequest): ServiceDef2[Card, CardException]
}
