package com.fortysevendeg.ninecardslauncher.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.collection.{CardException, CollectionException, CollectionProcessConfig}
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.NoInstalledAppCardType
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices, GooglePlayPackagesDetailResponse, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.awareness.AwarenessServices
import com.fortysevendeg.ninecardslauncher.services.contacts.ContactsServices
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._

trait CollectionProcessImplSpecification
  extends Specification
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  val appsInstalledException = AppsInstalledException("")

  val apiServiceException = ApiServiceException("")

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
    mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Either.right(Seq.empty)))

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(Seq.empty)))

    val mockApiServices = mock[ApiServices]

    val mockAwarenessServices = mock[AwarenessServices]

    val mockApiUtils = mock[ApiUtils]

    val mockRequestConfig = mock[RequestConfig]

    mockApiUtils.getRequestConfig(any) returns TaskService(Task(Either.right(mockRequestConfig)))

    val collectionProcess = new CollectionProcessImpl(
      collectionProcessConfig = collectionProcessConfig,
      persistenceServices = mockPersistenceServices,
      contactsServices = mockContactsServices,
      appsServices = mockAppsServices,
      apiServices = mockApiServices,
      awarenessServices = mockAwarenessServices) {

      override val apiUtils: ApiUtils = mockApiUtils

    }

  }

}

class CollectionProcessImplSpec
  extends CollectionProcessImplSpecification {

  "getCollections" should {

    "returns a sequence of collections for a valid request" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq.empty
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.right(collections)))

        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollection.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollection.map(_.name)
        }
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.getCollections.value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
          TaskService(Task(Either.right(Some(collection1))))

        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beLike {
          case Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection1.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns
          TaskService(Task(Either.right(None)))

        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result shouldEqual Either.right(None)
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId1)) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.getCollectionById(collectionId1).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "getCollectionBySharedCollectionId" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.right(Some(collection1))))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result must beLike {
          case Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection1.name
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.right(None)))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result shouldEqual Right(None)
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "createCollectionsFromUnformedItems" should {

    "the size of collections should be equal to size of categories" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.right(collections)))

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "the size of collections should be equal to size of categories with contact collection" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = Seq(collectionForUnformedItem, collectionForUnformedItem)

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.right(collections)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(seqContacts)))

        val tasks = seqContactsWithPhones map (contact => TaskService(Task(Either.right(contact))))
        mockContactsServices.findContactByLookupKey(anyString) returns(tasks(0), tasks.tail: _*)


        val result = collectionProcess.createCollectionsFromUnformedItems(unformedApps, unformedContacts)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual categoriesUnformedItems.size
        }
      }

  }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections passed by parameter" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = seqFormedCollection map (_ => collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.right(collections)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map(_.name)
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

  }

  "addCollection" should {

    "returns a the collection added for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Either.right(servicesCollectionAdded)))
        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result shouldEqual Right(collectionAdded)
      }

    "returns a CollectionException if service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception adding the new collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.right(Seq(collectionId))))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception deleting the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception deleting the cards by the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(collectionId)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception removing collections" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception removing cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.right(Seq(collectionId))))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception fetching the collection by position" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionByPosition(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollection)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.right(collectionId)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result shouldEqual Right(editedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.right(collectionId)))

        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result shouldEqual Right(updatedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(servicesCollection)))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.updateSharedCollection(collectionId, sharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "addPackages" should {

    "returns a CollectionException when passing a collectionId that doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(None)))

        val result = collectionProcess.addPackages(collectionId, Seq.empty)(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]

        there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      }

    "returns a Xor.Right[Unit] but doesn't call to persistence and api services when all applications are " +
      "already included on the collection" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Some(collection1))))
      mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))

      val result = collectionProcess.addPackages(collectionId, seqServicesCard.flatMap(_.packageName))(contextSupport).value.run
      result shouldEqual Either.right((): Unit)

      there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      there was one(mockPersistenceServices).fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
      there was no(mockPersistenceServices).fetchAppByPackages(any)
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      there was no(mockPersistenceServices).addCards(any)
    }

    "returns a Xor.Right[Unit] but doesn't call to api services when all applications are included on the collection " +
      "or installed in the device" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns
        TaskService(Task(Either.right(Some(collection1))))
      val (firstHalf, secondHalf) = seqServicesCard.splitAt(seqServicesCard.size / 2)
      mockPersistenceServices.fetchCardsByCollection(any) returns
        TaskService(Task(Either.right(firstHalf)))
      val secondHalfApps = seqServicesApp.filter(app => secondHalf.exists(_.packageName.contains(app.packageName)))
      mockPersistenceServices.fetchAppByPackages(any) returns
        TaskService(Task(Either.right(secondHalfApps)))
      mockPersistenceServices.addCards(any) returns
        TaskService(Task(Either.right(secondHalf)))

      val result = collectionProcess.addPackages(collectionId, seqServicesCard.flatMap(_.packageName))(contextSupport).value.run
      result shouldEqual Either.right((): Unit)

      there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      there was one(mockPersistenceServices).fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
      there was one(mockPersistenceServices).fetchAppByPackages(secondHalf.flatMap(_.packageName))
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      val addCardRequestSeq = secondHalfApps.zipWithIndex.map {
        case (app, index) => collectionProcess.toAddCardRequest(collectionId, app, firstHalf.size + index)
      }
      there was one(mockPersistenceServices).addCards(Seq(AddCardWithCollectionIdRequest(collectionId, addCardRequestSeq)))
    }

    "returns a Xor.Right[Unit] and call to api services with the applications not installed on the device" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns
          TaskService(Task(Either.right(Some(collection1))))
        val (firstHalf, secondHalf) = seqServicesCard.splitAt(seqServicesCard.size / 2)
        mockPersistenceServices.fetchCardsByCollection(any) returns
          TaskService(Task(Either.right(firstHalf)))
        mockPersistenceServices.fetchAppByPackages(any) returns
          TaskService(Task(Either.right(Seq.empty)))
        val secondHalfPackages = categorizedDetailPackages.filter(p => secondHalf.exists(_.packageName.contains(p.packageName)))
        mockApiServices.googlePlayPackagesDetail(any)(any) returns
          TaskService(Task(Either.right(GooglePlayPackagesDetailResponse(200, secondHalfPackages))))
        mockPersistenceServices.addCards(any) returns
          TaskService(Task(Either.right(secondHalf)))

        val result = collectionProcess.addPackages(collectionId, seqServicesCard.flatMap(_.packageName))(contextSupport).value.run
        result shouldEqual Either.right((): Unit)

        there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
        there was one(mockPersistenceServices).fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
        there was one(mockPersistenceServices).fetchAppByPackages(secondHalf.flatMap(_.packageName))
        there was one(mockApiServices).googlePlayPackagesDetail(secondHalf.flatMap(_.packageName))(mockRequestConfig)
        val addCardRequestSeq = secondHalfPackages.zipWithIndex.map {
          case (app, index) => collectionProcess.toAddCardRequest(collectionId, app, NoInstalledAppCardType, firstHalf.size + index)
        }
        there was one(mockPersistenceServices).addCards(Seq(AddCardWithCollectionIdRequest(collectionId, addCardRequestSeq)))
      }

  }

  "rankApps" should {

    "returns a the ordered packages for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesApp)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.right(rankAppsResponseList)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result shouldEqual Right(packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.rankApps()(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns the ordered packages even if the service throws an exception getting the country location" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesApp)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.left(apiServiceException)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.right(rankAppsResponseList)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result shouldEqual Right(packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesApp)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.left(apiServiceException)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "addCard" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Either.right(seqServicesCard)))

        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beLike {
          case Right(resultCards) =>
            resultCards map (_.term) shouldEqual (seqAddCardRequest map (_.term))
        }
      }

    "returns a CardException if service throws an exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns an CardException if the service throws an exception adding the new cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.addCards(collectionId, seqAddCardRequest).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.right(cardId)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(1))))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CardException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(1))))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(1))))

        val result = collectionProcess.reorderCard(collectionId, cardId, position).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CardException if the service throws an exception finding the card by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception updating the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardId, newPosition).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.right(cardId)))

        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result shouldEqual Right(updatedCard.copy(term = name))
      }

    "returns a CardException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.editCard(collectionId, cardId, name).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(1))))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(application1)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns TaskService(Task(Either.right(seqServicesCard)))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.right(cardId)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.left(appsInstalledException)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

}
