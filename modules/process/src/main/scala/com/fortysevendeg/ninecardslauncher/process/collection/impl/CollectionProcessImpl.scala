package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection._
import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedCollection, UnformedItem}
import com.fortysevendeg.ninecardslauncher.process.collection.utils.NineCardAppUtils._
import com.fortysevendeg.ninecardslauncher.process.commons.CollectionType
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.commons.Spaces._
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, ImplicitsPersistenceServiceExceptions, AddCollectionRequest, PersistenceServices}
import rapture.core.Answer

import scala.annotation.tailrec
import scalaz.concurrent.Task

class CollectionProcessImpl(collectionProcessConfig: CollectionProcessConfig, persistenceServices: PersistenceServices)
  extends CollectionProcess
  with ImplicitsCollectionException
  with ImplicitsPersistenceServiceExceptions
  with Conversions {

  override def createCollectionsFromUnformedItems(items: Seq[UnformedItem])(implicit context: ContextSupport) = Service {
    val tasks = generateAddCollections(items, categories, Seq.empty) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport) = Service {
    val tasks = toAddCollectionRequestFromFormedCollections(items) map (persistenceServices.addCollection(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(collection) => toCollection(collection) }))
  }.resolve[CollectionException]

  override def getCollections = (persistenceServices.fetchCollections map toCollectionSeq).resolve[CollectionException]

  @tailrec
  private[this] def generateAddCollections(
    items: Seq[UnformedItem],
    categories: Seq[String],
    acc: Seq[AddCollectionRequest]): Seq[AddCollectionRequest] = {
    categories match {
      case Nil => acc
      case h :: t =>
        val insert = generateAddCollection(items, h, acc.length)
        val a = if (insert.cards.length >= minAppsToAdd) acc :+ insert else acc
        generateAddCollections(items, t, a)
    }
  }

  private[this] def generateAddCollection(items: Seq[UnformedItem], category: String, index: Int): AddCollectionRequest = {
    val appsCategory = items.filter(_.category.contains(category)).sortWith(mfIndex(_) < mfIndex(_)).take(numSpaces)
    val pos = if (index >= numSpaces) index % numSpaces else index
    AddCollectionRequest(
      position = pos,
      name = collectionProcessConfig.namesCategories.getOrElse(category, category.toLowerCase),
      collectionType = CollectionType.apps,
      icon = category.toLowerCase,
      themedColorIndex = pos,
      appsCategory = Some(category),
      sharedCollectionSubscribed = Option(false),
      cards = toAddCardRequestSeq(appsCategory)
    )
  }

}
