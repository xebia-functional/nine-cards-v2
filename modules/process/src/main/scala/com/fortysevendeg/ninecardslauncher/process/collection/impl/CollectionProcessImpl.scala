package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils

class CollectionProcessImpl(
  val collectionProcessConfig: CollectionProcessConfig,
  val persistenceServices: PersistenceServices,
  val contactsServices: ContactsServices,
  val appsServices: AppsServices)
  extends CollectionProcess
  with CollectionProcessDependencies
  with CollectionsProcessImpl
  with CardsProcessImpl
  with ImplicitsPersistenceServiceExceptions
  with FormedCollectionConversions
  with FormedCollectionDependencies {

  override val resourceUtils: ResourceUtils = new ResourceUtils

  override def createCollectionsFromUnformedItems(apps: Seq[UnformedApp], contacts: Seq[UnformedContact])(implicit context: ContextSupport) =
    super.createCollectionsFromUnformedItems(apps, contacts)

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) =
    super.createCollectionsFromFormedCollections(items)

  override def generatePrivateCollections(apps: Seq[UnformedApp])(implicit context: ContextSupport) = super.generatePrivateCollections(apps)

  override def getCollections = super.getCollections

  override def getCollectionById(id: Int) = super.getCollectionById(id)

  override def addCollection(addCollectionRequest: AddCollectionRequest) = super.addCollection(addCollectionRequest)

  override def deleteCollection(collectionId: Int) = super.deleteCollection(collectionId)

  override def cleanCollections() = super.cleanCollections()

  override def reorderCollection(position: Int, newPosition: Int) = super.reorderCollection(position, newPosition)

  override def editCollection(collectionId: Int, editCollectionRequest: EditCollectionRequest) =
    super.editCollection(collectionId, editCollectionRequest)

  override def addCards(collectionId: Int, addCardListRequest: Seq[AddCardRequest]) = super.addCards(collectionId, addCardListRequest)

  override def deleteCard(collectionId: Int, cardId: Int) = super.deleteCard(collectionId, cardId)

  override def reorderCard(collectionId: Int, cardId: Int, newPosition: Int) = super.reorderCard(collectionId, cardId, newPosition)

  override def editCard(collectionId: Int, cardId: Int, name: String) = super. editCard(collectionId, cardId, name)

  override def updateNoInstalledCardsInCollections(packageName: String)(implicit contextSupport: ContextSupport) =
    super. updateNoInstalledCardsInCollections(packageName)

}
