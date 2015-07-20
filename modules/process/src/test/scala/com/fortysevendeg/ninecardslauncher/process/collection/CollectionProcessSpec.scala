package com.fortysevendeg.ninecardslauncher.process.collection

import android.content.Intent
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.collection.DeviceProcessConfig
import com.fortysevendeg.ninecardslauncher.process.collection.impl.CollectionProcessImpl
import com.fortysevendeg.ninecardslauncher.process.collection.models.{NineCardIntent, Collection}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

trait CollectionProcessSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CollectionProcessScope
    extends Scope {

    val deviceProcessConfig = DeviceProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardIntent]

    val collectionProcess = new CollectionProcessImpl(deviceProcessConfig, mockPersistenceServices)
  }

  trait ValidPersistenceServicesResponses extends CollectionProcessData {
    self: CollectionProcessScope =>
    mockPersistenceServices.fetchCollections returns Task(\/-(seqServicesCollection))
  }

  trait ErrorPersistenceServicesResponses extends CollectionProcessData {
    self: CollectionProcessScope =>

    val exception = NineCardsException("Irrelevant message")

    mockPersistenceServices.fetchCollections returns Task(-\/(exception))
  }

}

class CollectionProcessSpec extends CollectionProcessSpecification {

  "getCollections" should {

    "return a sequence of collections for a valid request" in
      new CollectionProcessScope with ValidPersistenceServicesResponses {
      val result = collectionProcess.getCollections

      result.run must be_\/-[Seq[Collection]].which { collections =>
        collections.size shouldEqual seqCollection.size
      }
    }

    "return a NineCardException if the service throws a exception" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
      val result = collectionProcess.getCollections

      result.run must be_-\/[NineCardsException]
    }
  }
}
