package com.fortysevendeg.ninecardslauncher.process.collection.impl

import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.Collection
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionProcess, Conversions}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices

import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz._
import scalaz.concurrent.Task

class CollectionProcessImpl(persistenceServices: PersistenceServices)
  extends CollectionProcess
  with Conversions {

  override def getCollections: Task[\/[NineCardsException, Seq[Collection]]] =
    persistenceServices.fetchCollections ▹ eitherT map toCollectionSeq
}
