package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{FormedCollection, UnformedItem, Collection}

import scalaz.\/
import scalaz.concurrent.Task

trait CollectionProcess {
  def createCollectionsFromUnformedItems(apps: Seq[UnformedItem])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[Collection]]
  def createCollectionsFromFormedCollections(items: Seq[FormedCollection])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[Collection]]
  def getCollections: Task[NineCardsException \/ Seq[Collection]]
}
