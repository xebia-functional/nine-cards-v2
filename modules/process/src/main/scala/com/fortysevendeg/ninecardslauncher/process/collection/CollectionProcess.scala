package com.fortysevendeg.ninecardslauncher.process.collection

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection

import scalaz.\/
import scalaz.concurrent.Task

trait CollectionProcess {

  def getCollections: Task[NineCardsException \/ Seq[Collection]]
}
