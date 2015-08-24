package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServiceException, PersistenceServices, DeleteCollectionRequest => ServicesDeleteCollectionRequest}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.Answer
import rapture.core.scalazInterop.ResultT

import scalaz.concurrent.Task

class CollectionProcessImpl(
  val collectionProcessConfig: CollectionProcessConfig,
  val persistenceServices: PersistenceServices,
  val contactsServices: ContactsServices)
  extends CollectionProcess
  with ImplicitsPersistenceServiceExceptions
  with FormedCollectionConversions
  with FormedCollectionDependencies {

  override val resourceUtils: ResourceUtils = new ResourceUtils

  override def createCollectionsFromUnformedItems(items: Seq[UnformedItem])(implicit context: ContextSupport) = Service {
    val tasks = createCollections(items, categories) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) = Service {
    val tasks = toAddCollectionRequestByFormedCollection(items) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  override def addCollection(addCollectionRequest: AddCollectionRequest) =
    (for {
      existingCollections <- persistenceServices.fetchCollections
      collection <- persistenceServices.addCollection(toAddCollectionRequest(addCollectionRequest, existingCollections.size))
    } yield toCollection(collection)).resolve[CollectionException]

  override def deleteCollection(deleteCollectionRequest: DeleteCollectionRequest) =
    (for {
      Some(collection) <- persistenceServices.findCollectionById(toFindCollectionByIdRequest(deleteCollectionRequest.id))
      _ <- persistenceServices.deleteCollection(ServicesDeleteCollectionRequest(collection))
      collectionList <- getCollections
      reorderedCollectionList = collectionList map(c => if (c.position > collection.position) movedCollectionPosition(c, -1) else c)
    } yield reorderedCollectionList).resolve[CollectionException]

}
