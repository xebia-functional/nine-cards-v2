package com.fortysevendeg.ninecardslauncher.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.process.collection.{CollectionException, CollectionProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.CollectionType
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer, Result}

import scala.collection.immutable.IndexedSeq
import scalaz.concurrent.Task

trait CollectionProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

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

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns Service(Task(Result.answer(Seq.empty)))

    val collectionProcess = new CollectionProcessImpl(
      collectionProcessConfig = collectionProcessConfig,
      persistenceServices = mockPersistenceServices,
      contactsServices = mockContactsServices)
  }

  trait ValidCreatePersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(collectionForUnformedItem)))

  }

  trait ValidPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(servicesCollectionAdded)))

  }

  trait ErrorPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns
      Service(Task(Errata(persistenceServiceException)))
    mockPersistenceServices.addCollection(any) returns
      Service(Task(Errata(persistenceServiceException)))

  }

  trait WithContactsResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockContactsServices.getFavoriteContacts returns Service(Task(Result.answer(seqContacts)))

    val tasks = seqContactsWithPhones map (contact => Service(Task(Result.answer[Contact, ContactsServiceException](contact))))
    mockContactsServices.findContactByLookupKey(anyString) returns (tasks.head, tasks.tail :_*)

  }

}

class CollectionProcessImplSpec
  extends CollectionProcessImplSpecification {

  "getCollections" should {

    "return a sequence of collections for a valid request" in
      new CollectionProcessScope with ValidCreatePersistenceServicesResponses {
        val result = collectionProcess.getCollections.run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollection.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollection.map (_.name)
        }
      }

    "return a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.getCollections.run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope with ValidCreatePersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedItems)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

    "return empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedItems)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual 0
        }
      }

    "the size of collections should be equal to size of categories with contact collection" in
      new CollectionProcessScope with ValidCreatePersistenceServicesResponses with WithContactsResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedItems)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size + 1
        }
      }

   }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections pass by parameter" in
      new CollectionProcessScope with ValidCreatePersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map (_.name)
        }
      }

    "return empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual 0
        }
      }

  }

  "addCollection" should {

    "return a collections for a valid request" in
      new CollectionProcessScope with ValidPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual collectionAdded
        }
      }

    "return a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

}
