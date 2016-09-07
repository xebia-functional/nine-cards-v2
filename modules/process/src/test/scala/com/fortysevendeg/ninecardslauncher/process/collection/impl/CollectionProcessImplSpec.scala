package com.fortysevendeg.ninecardslauncher.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
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
    extends Scope
      with CollectionProcessImplData {

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
    mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.right(Seq.empty)))

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(Seq.empty)))

    val mockApiServices = mock[ApiServices]

    val collectionProcess = new CollectionProcessImpl(
      collectionProcessConfig = collectionProcessConfig,
      persistenceServices = mockPersistenceServices,
      contactsServices = mockContactsServices,
      appsServices = mockAppsServices,
      apiServices = mockApiServices)

  }

}

class CollectionProcessImplSpec
  extends CollectionProcessImplSpecification {

  "getCollections" should {

    "returns a sequence of collections for a valid request" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq.empty
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.right(collections)))

        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollection.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollection.map(_.name)
        }
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.getCollections.value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
          TaskService(Task(Xor.right(Some(collection1))))

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId2)) returns
          TaskService(Task(Xor.right(None)))

        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beLike {
          case Xor.Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection1.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
          TaskService(Task(Xor.right(Some(collection1))))

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId2)) returns
          TaskService(Task(Xor.right(None)))

        val result = collectionProcess.getCollectionById(collectionId2).value.run
        result shouldEqual Xor.Right(None)
      }

    "returns a CollectionException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.right(collections)))

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "the size of collections should be equal to size of categories with contact collection" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.right(collections)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(seqContacts)))

        val tasks = seqContactsWithPhones map (contact => TaskService(Task(Xor.right(contact))))
        mockContactsServices.findContactByLookupKey(anyString) returns(tasks(0), tasks.tail: _*)


        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

  }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections pass by parameter" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = seqFormedCollection map (_ => collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.right(collections)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map(_.name)
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

  }

  "addCollection" should {

    "returns a the collection added for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Xor.right(servicesCollectionAdded)))
        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result shouldEqual Xor.Right(collectionAdded)
      }

    "returns a CollectionException if service throws a exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception adding the new collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Xor.right(Seq(collectionId))))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception deleting the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception deleting the cards by the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception updating the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Xor.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Xor.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Xor.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CollectionException if the service throws a exception removing collections" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Xor.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception removing cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Xor.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Xor.right(Seq(collectionId))))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CollectionException if the service throws a exception fetching the collection by position" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Xor.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Xor.right(collectionId)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result shouldEqual Xor.Right(updatedCollection)
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Xor.right(collectionId)))

        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result shouldEqual Xor.Right(updatedCollection)
      }

    "returns a CollectionException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }

    "returns a CollectionException if the service throws a exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Xor.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beAnInstanceOf[Xor.Left[CollectionException]]
      }
  }

  "addCard" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Xor.right(seqServicesCard)))

        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beLike {
          case Xor.Right(resultCards) =>
            resultCards map (_.term) shouldEqual (seqAddCardRequest map (_.term))
        }
      }

    "returns a CardException if service throws a exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns an CardException if the service throws a exception adding the new cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.deleteCard(any) returns TaskService(Task(Xor.right(cardId)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.right(Seq(1))))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception deleting the card" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.deleteCard(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception updating the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.deleteCard(any) returns TaskService(Task(Xor.right(cardId)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.right(Seq(1))))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.right(Seq(1))))

        val result = collectionProcess.reorderCard(collectionId, cardId, position).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CardException if the service throws a exception finding the card by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception updating the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Xor.right(cardId)))

        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result shouldEqual Xor.Right(updatedCard)
      }

    "returns a CardException if the service throws a exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Xor.right(Option(servicesCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Xor.right(Seq(1))))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(application1)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a CardException if the service throws a exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns TaskService(Task(Xor.right(seqServicesCard)))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Xor.right(cardId)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.left(appsInstalledException)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[CardException]]
      }
  }

}
