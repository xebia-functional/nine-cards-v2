package com.fortysevendeg.ninecardslauncher.process.collection

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.collection.impl.CollectionProcessImpl
import com.fortysevendeg.ninecardslauncher.process.collection.models.{Collection, NineCardIntent}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

trait CollectionProcessSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  trait CollectionProcessScope
    extends Scope {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val collectionProcessConfig = CollectionProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardIntent]

    val collectionProcess = new CollectionProcessImpl(collectionProcessConfig, mockPersistenceServices)
  }

  trait ValidPersistenceServicesResponses
    extends CollectionProcessData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Task(\/-(seqServicesCollection))
    mockPersistenceServices.addCollection(any) returns Task(\/-(collectionForUnformedItem))
  }

  trait ErrorPersistenceServicesResponses
    extends CollectionProcessData {

    self: CollectionProcessScope =>

    val exception = NineCardsException("Irrelevant message")

    mockPersistenceServices.fetchCollections returns Task(-\/(exception))
    mockPersistenceServices.addCollection(any) returns Task(-\/(exception))
  }

}

class CollectionProcessSpec
  extends CollectionProcessSpecification {

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

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope with ValidPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedItems)(contextSupport)

        result.run must be_\/-[Seq[Collection]].which { collections =>
          collections.size shouldEqual categoriesUnformedItems.size
        }
      }

    "return empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedItems)(contextSupport)

        result.run must be_\/-[Seq[Collection]].which { collections =>
          collections.size shouldEqual 0
        }
      }

  }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections pass by parameter" in
      new CollectionProcessScope with ValidPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport)

        result.run must be_\/-[Seq[Collection]].which { collections =>
          collections.size shouldEqual seqFormedCollection.size
        }
      }

    "return empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport)

        result.run must be_\/-[Seq[Collection]].which { collections =>
          collections.size shouldEqual 0
        }
      }

  }

}
