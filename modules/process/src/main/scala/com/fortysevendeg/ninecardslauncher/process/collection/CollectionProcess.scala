package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardApp, Collection}

import scalaz.\/
import scalaz.concurrent.Task

trait CollectionProcess {
  def createCollectionsFromMyDevice(apps: Seq[NineCardApp])(implicit context: ContextSupport): Task[NineCardsException \/ Seq[Collection]]
  def getCollections: Task[NineCardsException \/ Seq[Collection]]
}
