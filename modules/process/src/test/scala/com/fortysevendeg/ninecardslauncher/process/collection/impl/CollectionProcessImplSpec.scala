package com.fortysevendeg.ninecardslauncher.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException, CollectionProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindCollectionByIdRequest, PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait CollectionProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  val appsInstalledException = AppsInstalledException("")

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

    val mockAppsServices = mock[AppsServices]
    mockAppsServices.getInstalledApplications(contextSupport) returns Service(Task(Result.answer(Seq.empty)))

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns Service(Task(Result.answer(Seq.empty)))

    val collectionProcess = new CollectionProcessImpl(
      collectionProcessConfig = collectionProcessConfig,
      persistenceServices = mockPersistenceServices,
      contactsServices = mockContactsServices,
      appsServices = mockAppsServices)

  }

  trait ValidUpdateCollectionsNoInstalled
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCards returns Service(Task(Result.answer(seqServicesCard)))

    mockPersistenceServices.updateCard(any) returns Service(Task(Result.answer(cardId)))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      Service(Task(Result.answer(application1)))
  }

  trait ValidCreateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(collectionForUnformedItem)))

  }

  trait ValidGetCollectionByIdPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
      Service(Task(Result.answer(Some(collection1))))

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId2)) returns
      Service(Task(Result.answer(None)))

  }

  trait WithContactsResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockContactsServices.getFavoriteContacts returns Service(Task(Result.answer(seqContacts)))

    val tasks = seqContactsWithPhones map (contact => Service(Task(Result.answer[Contact, ContactsServiceException](contact))))
    mockContactsServices.findContactByLookupKey(anyString) returns (tasks(0), tasks.tail :_*)

  }

  trait ErrorUpdateCollectionsNoInstalled
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCards returns Service(Task(Result.answer(seqServicesCard)))

    mockPersistenceServices.updateCard(any) returns Service(Task(Result.answer(cardId)))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      Service(Task(Errata(appsInstalledException)))
  }

  trait ErrorCreateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Errata(persistenceServiceException)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorGetCollectionByIdPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
      Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidAddCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Result.answer(servicesCollectionAdded)))
    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))

  }

  trait ErrorFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorAddCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidDeleteCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Result.answer(collectionId)))

  }

  trait ValidCleanCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns Service(Task(Result.answer(collectionsRemoved)))
    mockPersistenceServices.deleteAllCards() returns Service(Task(Result.answer(cardsRemoved)))

  }

  trait CollectionErrorCleanCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns Service(Task(Errata(persistenceServiceException)))
    mockPersistenceServices.deleteAllCards() returns Service(Task(Result.answer(cardsRemoved)))

  }

  trait CardErrorCleanCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns Service(Task(Result.answer(collectionsRemoved)))
    mockPersistenceServices.deleteAllCards() returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorFindCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteCardsByCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.fetchCollections returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteUpdateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns Service(Task(Result.answer(collectionId)))
    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidReorderCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Result.answer(collectionId)))

  }

  trait ErrorReorderFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorReorderFetchCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.fetchCollections returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorReorderUpdateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.fetchCollections returns Service(Task(Result.answer(seqServicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidEditCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Result.answer(collectionId)))

  }

  trait ErrorEditCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns Service(Task(Result.answer(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidAddCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.addCard(any) returns Service(Task(Result.answer(servicesCard)))

  }

  trait ErrorFetchCardsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorAddCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.addCard(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidDeleteCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns Service(Task(Result.answer(cardId)))
    mockPersistenceServices.updateCard(any) returns Service(Task(Result.answer(cardId)))

  }

  trait ErrorDeleteFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteFetchCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorDeleteUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns Service(Task(Result.answer(cardId)))
    mockPersistenceServices.updateCard(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidReorderCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.updateCard(any) returns Service(Task(Result.answer(cardId)))

  }

  trait ErrorReorderFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorReorderFetchCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ErrorReorderUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns Service(Task(Result.answer(seqServicesCard)))
    mockPersistenceServices.updateCard(any) returns Service(Task(Errata(persistenceServiceException)))

  }

  trait ValidEditCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.updateCard(any) returns Service(Task(Result.answer(cardId)))

  }

  trait ErrorEditFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Errata(persistenceServiceException)))

  }


  trait ErrorEditUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns Service(Task(Result.answer(Option(servicesCard))))
    mockPersistenceServices.updateCard(any) returns Service(Task(Errata(persistenceServiceException)))

  }

}

class CollectionProcessImplSpec
  extends CollectionProcessImplSpecification {

  "getCollections" should {

    "returns a sequence of collections for a valid request" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.getCollections.run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollection.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollection.map (_.name)
        }
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.getCollections.run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope with ValidGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId1).run.run
        result must beLike {
          case Answer(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection1.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope with ValidGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId2).run.run
        result must beLike {
          case Answer(resultCollection) => resultCollection shouldEqual None
        }
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId1).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

    "returns empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual 0
        }
      }

    "the size of collections should be equal to size of categories with contact collection" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses with WithContactsResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

   }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections pass by parameter" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map (_.name)
        }
      }

    "returns empty collections when persistence services fails" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).run.run
        result must beLike {
          case Answer(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual 0
        }
      }

  }

  "addCollection" should {

    "returns a the collection added for a valid request" in
      new CollectionProcessScope with ValidAddCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual collectionAdded
        }
      }

    "returns a CollectionException if service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception adding the new collection" in
      new CollectionProcessScope with ErrorAddCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope with ValidDeleteCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorFindCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception deleting the collection" in
      new CollectionProcessScope with ErrorDeleteCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception deleting the cards by the collection" in
      new CollectionProcessScope with ErrorDeleteCardsByCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorDeleteFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a successful answer if the service throws a exception updating the collections" in
      new CollectionProcessScope with ErrorDeleteUpdateCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidCleanCollectionPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().run.run
        result must beLike {
          case Answer(r) =>
            r shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception removing collections" in
      new CollectionProcessScope with CollectionErrorCleanCollectionsPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception removing cards" in
      new CollectionProcessScope with CardErrorCleanCollectionsPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidReorderCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception fetching the collection by position" in
      new CollectionProcessScope with ErrorReorderFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorReorderFetchCollectionsPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a successful answer if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorReorderUpdateCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope with ValidEditCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual updatedCollection
        }
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorFindCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorEditCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CollectionException]
          }
        }
      }
  }

  "addCard" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope with ValidAddCardPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).run.run
        result must beLike {
          case Answer(resultCards) =>
            resultCards shouldEqual seqAddCardResponse
        }
      }

    "returns a CardException if service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorFetchCardsPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns an empty answer if the service throws a exception adding the new card" in
      new CollectionProcessScope with ErrorAddCardPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual Seq()
        }
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope with ValidDeleteCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope with ErrorDeleteFindCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorDeleteFetchCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns a CardException if the service throws a exception deleting the card" in
      new CollectionProcessScope with ErrorDeleteCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns an empty answer if the service throws a exception updating the cards" in
      new CollectionProcessScope with ErrorDeleteUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidReorderCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope with ErrorReorderFindCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorReorderFetchCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns a successful answer if the service throws a exception updating the cards" in
      new CollectionProcessScope with ErrorReorderUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope with ValidEditCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).run.run
        result must beLike {
          case Answer(resultCollection) =>
            resultCollection shouldEqual updatedCard
        }
      }

    "returns a CardException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorEditFindCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorEditUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope with ValidUpdateCollectionsNoInstalled {
        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(r) => r shouldEqual ((): Unit)
        }
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorUpdateCollectionsNoInstalled {
        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CardException]
          }
        }
      }
  }

}
