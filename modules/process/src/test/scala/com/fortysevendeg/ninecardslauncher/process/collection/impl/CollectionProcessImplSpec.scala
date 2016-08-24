package com.fortysevendeg.ninecardslauncher.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.NineCardException
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException, CollectionProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.services.api.ApiServices
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import com.fortysevendeg.ninecardslauncher.services.persistence.{FindCollectionByIdRequest, PersistenceServiceException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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
    mockAppsServices.getInstalledApplications(contextSupport) returns CatsService(Task(Xor.right(Seq.empty)))

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns CatsService(Task(Xor.right(Seq.empty)))

    val mockApiServices = mock[ApiServices]

    val collectionProcess = new CollectionProcessImpl(
      collectionProcessConfig = collectionProcessConfig,
      persistenceServices = mockPersistenceServices,
      contactsServices = mockContactsServices,
      appsServices = mockAppsServices,
      apiServices = mockApiServices)

  }

  trait ValidUpdateCollectionsNoInstalled
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCards returns CatsService(Task(Xor.right(seqServicesCard)))

    mockPersistenceServices.updateCards(any) returns CatsService(Task(Xor.right(Seq(1))))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      CatsService(Task(Xor.right(application1)))
  }

  trait ValidCreateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    val collections: Seq[Collection]

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.addCollections(any) returns CatsService(Task(Xor.right(collections)))

  }

  trait ValidGetCollectionByIdPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
      CatsService(Task(Xor.right(Some(collection1))))

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId2)) returns
      CatsService(Task(Xor.right(None)))

  }

  trait WithContactsResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockContactsServices.getFavoriteContacts returns CatsService(Task(Xor.right(seqContacts)))

    val tasks = seqContactsWithPhones map (contact => CatsService(Task(Xor.right(contact))))
    mockContactsServices.findContactByLookupKey(anyString) returns (tasks(0), tasks.tail :_*)

  }

  trait ErrorUpdateCollectionsNoInstalled
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCards returns CatsService(Task(Xor.right(seqServicesCard)))

    mockPersistenceServices.updateCard(any) returns CatsService(Task(Xor.right(cardId)))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      CatsService(Task(Xor.left(appsInstalledException)))
  }

  trait ErrorCreateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))
    mockPersistenceServices.addCollections(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorGetCollectionByIdPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
      CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidAddCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns CatsService(Task(Xor.right(servicesCollectionAdded)))
    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))

  }

  trait ErrorFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorAddCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.addCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidDeleteCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.updateCollections(any) returns CatsService(Task(Xor.right(Seq(collectionId))))

  }

  trait ValidCleanCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns CatsService(Task(Xor.right(collectionsRemoved)))
    mockPersistenceServices.deleteAllCards() returns CatsService(Task(Xor.right(cardsRemoved)))

  }

  trait CollectionErrorCleanCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns CatsService(Task(Xor.left(persistenceServiceException)))
    mockPersistenceServices.deleteAllCards() returns CatsService(Task(Xor.right(cardsRemoved)))

  }

  trait CardErrorCleanCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.deleteAllCollections() returns CatsService(Task(Xor.right(collectionsRemoved)))
    mockPersistenceServices.deleteAllCards() returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorFindCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteCardsByCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteUpdateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.deleteCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.deleteCardsByCollection(any) returns CatsService(Task(Xor.right(collectionId)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.updateCollections(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidReorderCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.updateCollections(any) returns CatsService(Task(Xor.right(Seq(collectionId))))

  }

  trait ErrorReorderFetchCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorReorderFetchCollectionsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorReorderUpdateCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCollectionByPosition(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.fetchCollections returns CatsService(Task(Xor.right(seqServicesCollection)))
    mockPersistenceServices.updateCollections(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidEditCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns CatsService(Task(Xor.right(collectionId)))

  }

  trait ErrorEditCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidUpdateSharedCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns CatsService(Task(Xor.right(collectionId)))

  }

  trait ErrorUpdateSharedCollectionPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCollectionById(any) returns CatsService(Task(Xor.right(servicesCollection)))
    mockPersistenceServices.updateCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidAddCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.addCards(any) returns CatsService(Task(Xor.right(seqServicesCard)))

  }

  trait ErrorFetchCardsPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorAddCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.addCards(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidDeleteCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns CatsService(Task(Xor.right(cardId)))
    mockPersistenceServices.updateCards(any) returns CatsService(Task(Xor.right(Seq(1))))

  }

  trait ErrorDeleteFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteFetchCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorDeleteUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.deleteCard(any) returns CatsService(Task(Xor.right(cardId)))
    mockPersistenceServices.updateCards(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidReorderCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.updateCards(any) returns CatsService(Task(Xor.right(Seq(1))))

  }

  trait ErrorReorderFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorReorderFetchCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ErrorReorderUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.fetchCardsByCollection(any) returns CatsService(Task(Xor.right(seqServicesCard)))
    mockPersistenceServices.updateCards(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

  trait ValidEditCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.updateCard(any) returns CatsService(Task(Xor.right(cardId)))

  }

  trait ErrorEditFindCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }


  trait ErrorEditUpdateCardPersistenceServicesResponses
    extends CollectionProcessImplData {

    self: CollectionProcessScope =>

    mockPersistenceServices.findCardById(any) returns CatsService(Task(Xor.right(Option(servicesCard))))
    mockPersistenceServices.updateCard(any) returns CatsService(Task(Xor.left(persistenceServiceException)))

  }

}

class CollectionProcessImplSpec
  extends CollectionProcessImplSpecification {

  "getCollections" should {

    "returns a sequence of collections for a valid request" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {

        override val collections: Seq[Collection] = Seq.empty

        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollection.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollection.map (_.name)
        }
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope with ValidGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beLike {
          case Xor.Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection1.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope with ValidGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId2).value.run
        result must beLike {
          case Xor.Right(resultCollection) => resultCollection shouldEqual None
        }
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope with ErrorGetCollectionByIdPersistenceServicesResponses {
        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {

        override val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "the size of collections should be equal to size of categories with contact collection" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses with WithContactsResponses {

        override val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

   }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections pass by parameter" in
      new CollectionProcessScope with ValidCreateCollectionPersistenceServicesResponses {

        override val collections: Seq[Collection] = seqFormedCollection map (_ => collectionForUnformedItem)

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map (_.name)
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope with ErrorCreateCollectionPersistenceServicesResponses {
        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

  }

  "addCollection" should {

    "returns a the collection added for a valid request" in
      new CollectionProcessScope with ValidAddCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual collectionAdded
        }
      }

    "returns a CollectionException if service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception adding the new collection" in
      new CollectionProcessScope with ErrorAddCollectionPersistenceServicesResponses {
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope with ValidDeleteCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorFindCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception deleting the collection" in
      new CollectionProcessScope with ErrorDeleteCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception deleting the cards by the collection" in
      new CollectionProcessScope with ErrorDeleteCardsByCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorDeleteFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception updating the collections" in
      new CollectionProcessScope with ErrorDeleteUpdateCollectionPersistenceServicesResponses {
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidCleanCollectionPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().value.run
        result must beLike {
          case Xor.Right(r) =>
            r shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception removing collections" in
      new CollectionProcessScope with CollectionErrorCleanCollectionsPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception removing cards" in
      new CollectionProcessScope with CardErrorCleanCollectionsPersistenceServicesResponses {
        val result = collectionProcess.cleanCollections().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidReorderCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CollectionException if the service throws a exception fetching the collection by position" in
      new CollectionProcessScope with ErrorReorderFetchCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope with ErrorReorderFetchCollectionsPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorReorderUpdateCollectionPersistenceServicesResponses {
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope with ValidEditCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual updatedCollection
        }
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorFindCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorEditCollectionPersistenceServicesResponses {
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope with ValidUpdateSharedCollectionPersistenceServicesResponses {
        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual updatedCollection
        }
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorFindCollectionPersistenceServicesResponses {
        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorUpdateSharedCollectionPersistenceServicesResponses {
        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CollectionException]
          }
      }
  }

  "addCard" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope with ValidAddCardPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beLike {
          case Xor.Right(resultCards) =>
            resultCards map (_.term) shouldEqual (seqAddCardRequest map (_.term))
        }
      }

    "returns a CardException if service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorFetchCardsPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns an CardException if the service throws a exception adding the new cards" in
      new CollectionProcessScope with ErrorAddCardPersistenceServicesResponses {
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope with ValidDeleteCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope with ErrorDeleteFindCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorDeleteFetchCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception deleting the card" in
      new CollectionProcessScope with ErrorDeleteCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception updating the cards" in
      new CollectionProcessScope with ErrorDeleteUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope with ValidReorderCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope with ValidReorderCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, position).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual ((): Unit)
        }
    }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope with ErrorReorderFindCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope with ErrorReorderFetchCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception updating the cards" in
      new CollectionProcessScope with ErrorReorderUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope with ValidEditCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beLike {
          case Xor.Right(resultCollection) =>
            resultCollection shouldEqual updatedCard
        }
      }

    "returns a CardException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope with ErrorEditFindCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorEditUpdateCardPersistenceServicesResponses {
        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope with ValidUpdateCollectionsNoInstalled {
        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(r) => r shouldEqual ((): Unit)
        }
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope with ErrorUpdateCollectionsNoInstalled {
        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CardException]
          }
      }
  }

}
