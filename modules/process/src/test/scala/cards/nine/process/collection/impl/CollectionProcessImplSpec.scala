package cards.nine.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.process.collection.{CardException, CollectionException, CollectionProcessConfig}
import cards.nine.process.commons.models.NineCardIntent
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceException, ApiServices, GooglePlayPackagesDetailResponse, RequestConfig}
import cards.nine.services.apps.{AppsInstalledException, AppsServices}
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.contacts.models.Contact
import cards.nine.services.persistence._
import cards.nine.services.persistence.models.Collection
import cards.nine.models.types.NoInstalledAppCardType
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import cats.syntax.either._
import cards.nine.commons.test.TaskServiceTestOps._

trait CollectionProcessImplSpecification
  extends TaskServiceSpecification
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
    mockAppsServices.getInstalledApplications(contextSupport) returns serviceRight(Seq.empty)

    val mockContactsServices = mock[ContactsServices]
    mockContactsServices.getFavoriteContacts returns serviceRight(Seq.empty)

    val mockApiServices = mock[ApiServices]

    val mockAwarenessServices = mock[AwarenessServices]

    val mockApiUtils = mock[ApiUtils]

    val mockRequestConfig = mock[RequestConfig]

    mockApiUtils.getRequestConfig(any) returns serviceRight(mockRequestConfig)

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

    "returns a sequence of collections for a valid request without cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithoutCards)

        collectionProcess.getCollections.mustRight { resultSeqCollection =>
          resultSeqCollection.size shouldEqual seqServicesCollectionWithoutCards.size
          resultSeqCollection map (_.name) shouldEqual seqServicesCollectionWithoutCards.map(_.name)
          resultSeqCollection map (_.cards) shouldEqual Seq(Seq.empty, Seq.empty)
        }
      }

    "returns a sequence of collections for a valid request " in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)

        collectionProcess.getCollections.mustRight { resultSeqCollection =>
          resultSeqCollection.size shouldEqual seqServicesCollectionWithCards.size
          resultSeqCollection map (_.name) shouldEqual seqServicesCollectionWithCards.map(_.name)
          resultSeqCollection map (_.cards.size) shouldEqual seqServicesCollectionWithCards.map(_.cards.size)
        }
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollections.mustLeft[CollectionException]
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns
          serviceRight(Some(servicesCollectionById))

        collectionProcess.getCollectionById(collectionId).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionById.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns serviceRight(None)
        collectionProcess.getCollectionById(collectionId).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollectionById(collectionId).mustLeft[CollectionException]
      }
  }

  "getCollectionByCategory" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns serviceRight(Some(servicesCollectionByCategory))

        collectionProcess.getCollectionByCategory(appsCategoryGame).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionByCategory.name
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns serviceRight(None)
        collectionProcess.getCollectionByCategory(appsCategoryGame).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollectionByCategory(appsCategoryGame).mustLeft[CollectionException]
      }
  }


  "getCollectionBySharedCollectionId" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          serviceRight(Option(servicesCollectionBySharedCollectionId))

        collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionBySharedCollectionId.name
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns serviceRight(None)
        collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).mustLeft[CollectionException]
      }
  }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections passed by parameter" in
      new CollectionProcessScope {

        val collections: Seq[Collection] = seqFormedCollection map (_ => collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        mockPersistenceServices.addCollections(any) returns serviceRight(collections)

        collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).mustRight { resultSeqCollection =>
          resultSeqCollection.size shouldEqual seqFormedCollection.size
          resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map(_.name)
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        mockPersistenceServices.addCollections(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).mustLeft[CollectionException]
      }

  }

  "generatePrivateCollections" should {

    "return a seq empty if number of cards by category is < minAppsToAdd" in
      new CollectionProcessScope {

        val result = collectionProcess.generatePrivateCollections(seqUnformedAppsForPrivateCollections)(contextSupport).mustRight { r =>
          r shouldEqual Seq.empty
        }
      }

  }


  "addCollection" should {

    "returns a the collection added for a valid request without cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionAddWithoutCards)
        mockPersistenceServices.addCollection(any) returns serviceRight(servicesCollectionAddedWithoutCards)

        val result = collectionProcess.addCollection(addCollectionRequest).run
        result shouldEqual Right(collectionAddedWithoutCards)
      }

    "returns a the collection added for a valid request with cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionAddWithCards)
        mockPersistenceServices.addCollection(any) returns serviceRight(servicesCollectionAddedWithCards)
        val result = collectionProcess.addCollection(addCollectionRequestWithCards).run
        result shouldEqual Right(collectionAddedWithCards)
      }


    "returns a CollectionException if service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.addCollection(addCollectionRequest).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception adding the new collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionAddWithoutCards)
        mockPersistenceServices.addCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.addCollection(addCollectionRequest).mustLeft[CollectionException]
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionDelete))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(collectionRemoved)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(cardsRemoved)
        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        mockPersistenceServices.updateCollections(any) returns serviceRight(Seq(updatedCollection))

        collectionProcess.deleteCollection(collectionId).mustRightUnit
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception deleting the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionDelete))
        mockPersistenceServices.deleteCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception deleting the cards by the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionDelete))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(collectionRemoved)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionDelete))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(collectionRemoved)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(cardsRemoved)
        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionDelete))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(collectionRemoved)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(cardsRemoved)
        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        mockPersistenceServices.updateCollections(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceRight(collectionsRemoved)
        mockPersistenceServices.deleteAllCards() returns serviceRight(cardsRemoved)
        collectionProcess.cleanCollections().mustRightUnit
      }

    "returns a CollectionException if the service throws an exception removing collections" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceLeft(persistenceServiceException)
        mockPersistenceServices.deleteAllCards() returns serviceRight(cardsRemoved)
        collectionProcess.cleanCollections().mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception removing cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceRight(collectionsRemoved)
        mockPersistenceServices.deleteAllCards() returns serviceLeft(persistenceServiceException)
        collectionProcess.cleanCollections().mustLeft[CollectionException]
      }
  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        mockPersistenceServices.updateCollections(any) returns serviceRight(Seq(updatedCollections))
        collectionProcess.reorderCollection(0, newPosition).mustRightUnit
      }

    "returns a CollectionException if the service throws an exception fetching the collection by position" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, newPosition).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, newPosition).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        mockPersistenceServices.updateCollections(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, newPosition).mustLeft[CollectionException]
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionById))
        mockPersistenceServices.updateCollection(any) returns serviceRight(updatedCollection)

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).run
        result shouldEqual Right(editedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCollection(collectionId, editCollectionRequest).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionById))
        mockPersistenceServices.updateCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCollection(collectionId, editCollectionRequest).mustLeft[CollectionException]
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionById))
        mockPersistenceServices.updateCollection(any) returns serviceRight(updatedCollection)

        val result = collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).run
        result shouldEqual Right(updatedSharedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(servicesCollectionById))
        mockPersistenceServices.updateCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).mustLeft[CollectionException]
      }
  }

  "addPackages" should {

    "returns a CollectionException when passing a collectionId that doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(None)
        collectionProcess.addPackages(collectionId, Seq.empty)(contextSupport).mustLeft[CollectionException]

        there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      }

    "returns a Xor.Right[Unit] but doesn't call to persistence and api services when all applications are " +
      "already included on the collection" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(servicesCollectionById))
      mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardsAddPackages)

      val result = collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).mustRightUnit

      there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      there was one(mockPersistenceServices).fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
      there was no(mockPersistenceServices).fetchAppByPackages(any)
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      there was no(mockPersistenceServices).addCards(any)
    }

    "returns a Xor.Right[Unit] but doesn't call to api services when all applications are included on the collection " +
      "or installed in the device" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(servicesCollectionById))
      val (firstHalf, secondHalf) = seqServicesCardsAddPackages.splitAt(seqServicesCardsAddPackages.size / 2)
      mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(firstHalf)
      val secondHalfApps = seqServicesAppAddPackages.filter(app => secondHalf.exists(_.packageName.contains(app.packageName)))
      mockPersistenceServices.fetchAppByPackages(any) returns serviceRight(secondHalfApps)
      mockPersistenceServices.addCards(any) returns serviceRight(secondHalf)

      val result = collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).mustRightUnit

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

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(servicesCollectionById))
        val (firstHalf, secondHalf) = seqServicesCardsAddPackages.splitAt(seqServicesCardsAddPackages.size / 2)
        mockPersistenceServices.fetchCardsByCollection(any) returns
          serviceRight(firstHalf)
        mockPersistenceServices.fetchAppByPackages(any) returns
          serviceRight(Seq.empty)
        val secondHalfPackages = categorizedDetailPackages.filter(p => secondHalf.exists(_.packageName.contains(p.packageName)))
        mockApiServices.googlePlayPackagesDetail(any)(any) returns
          serviceRight(GooglePlayPackagesDetailResponse(200, secondHalfPackages))
        mockPersistenceServices.addCards(any) returns
          serviceRight(secondHalf)

        collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).mustRightUnit

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

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqServicesAppRank)
        mockAwarenessServices.getLocation(any) returns serviceRight(awarenessLocation)
        mockApiServices.rankApps(any, any)(any) returns serviceRight(rankAppsResponseList)

        collectionProcess.rankApps()(contextSupport).mustRight { r =>
          r shouldEqual packagesByCategory
        }
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.rankApps()(contextSupport).mustLeft[CollectionException]
      }

    "returns the ordered packages even if the service throws an exception getting the country location" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqServicesAppRank)
        mockAwarenessServices.getLocation(any) returns serviceLeft(apiServiceException)
        mockApiServices.rankApps(any, any)(any) returns serviceRight(rankAppsResponseList)

        collectionProcess.rankApps()(contextSupport).mustRight { r =>
          r shouldEqual packagesByCategory
        }
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqServicesAppRank)
        mockAwarenessServices.getLocation(any) returns serviceRight(awarenessLocation)
        mockApiServices.rankApps(any, any)(any) returns serviceLeft(apiServiceException)

        collectionProcess.rankApps()(contextSupport).mustLeft[CollectionException]
      }
  }

  "addCards" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardByCollection)
        mockPersistenceServices.addCards(any) returns serviceRight(seqServicesAddedCards)

        collectionProcess.addCards(collectionId, seqAddCardsRequest).mustRight { resultCards =>
          resultCards map (_.term) shouldEqual (seqServicesAddedCards map (_.term))
        }
      }

    "returns a CardException if service throws an exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.addCards(collectionId, seqAddCardsRequest).mustLeft[CardException]
      }

    "returns an CardException if the service throws an exception adding the new cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardByCollection)
        mockPersistenceServices.addCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.addCards(collectionId, seqAddCardsRequest).mustLeft[CardException]
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(cardRemoved)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardByCollection)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCard(collectionId, cardId).mustRightUnit

        there was one(mockPersistenceServices).updateCards(any)
      }

    "returns a successful when return sequence empty" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(cardRemoved)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(Seq.empty)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCard(collectionId, cardId).mustRightUnit

        there was one(mockPersistenceServices).updateCards(UpdateCardsRequest(Seq.empty))
      }

    "returns a CardException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteCard(collectionId, cardId).mustLeft[CardException]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(cardRemoved)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardByCollection)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCard(collectionId, cardId).mustLeft[CardException]
      }
  }

  "deleteAllCardsByPackageName" should {

    "returns a successful when delete all Seq.empty of Cards in all collection by package name" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqServicesCollectionWithCards)
        collectionProcess.deleteAllCardsByPackageName(packageName).mustRightUnit

        there was no(mockPersistenceServices).deleteCard(any, any)
        there was no(mockPersistenceServices).fetchCardsByCollection(any)
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteAllCardsByPackageName(packageName).mustLeft[CardException]
      }
  }

  "deleteCards" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(cardsRemoved)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardDelete)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCards(collectionId, seqCardIds).mustRightUnit
      }

    "returns a successful when return sequence empty of cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(cardsRemoved)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(Seq.empty)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCards(collectionId, seqCardIds).mustRightUnit

        there was one(mockPersistenceServices).updateCards(UpdateCardsRequest(Seq.empty))
      }
    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteCards(collectionId, seqCardIds).mustLeft[CardException]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(cardId)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardDelete)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCards(collectionId, seqCardIds).mustLeft[CardException]
      }
  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(servicesCardReorder))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardReorder)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustRightUnit
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(servicesCardReorder))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardReorder)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(1))

        collectionProcess.reorderCard(collectionId, cardIdReorder, samePositionReorder).mustRightUnit
      }

    "returns a CardException if the service throws an exception finding the card by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustLeft[CardException]
      }

    "returns a CardException if the service throws an exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(servicesCardReorder))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustLeft[CardException]
      }

    "returns a CardException if the service throws an exception updating the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(servicesCardReorder))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqServicesCardReorder)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustLeft[CardException]
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(editCard))
        mockPersistenceServices.updateCard(any) returns serviceRight(updatedCard)

        collectionProcess.editCard(collectionId, cardIdEdit, newNameEditCard).mustRight { r =>
          r shouldEqual editedCard.copy(term = newNameEditCard)
        }
      }

    "returns a CardException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCard(collectionId, cardId, newNameEditCard).mustLeft[CardException]
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(editCard))
        mockPersistenceServices.updateCard(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.editCard(collectionId, cardId, newNameEditCard).mustLeft[CardException]
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns serviceRight(seqServicesCardUpdateNoInstallation)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))
        mockAppsServices.getApplication(packageNameApplication)(contextSupport) returns serviceRight(applicationUpdateNoInstallation)

        collectionProcess.updateNoInstalledCardsInCollections(packageNameApplication)(contextSupport).mustRightUnit
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns serviceRight(seqServicesCardUpdateNoInstallation)
        mockPersistenceServices.updateCard(any) returns serviceRight(updatedCard)
        mockAppsServices.getApplication(packageNameApplication)(contextSupport) returns serviceLeft(appsInstalledException)

        collectionProcess.updateNoInstalledCardsInCollections(packageNameApplication)(contextSupport).mustLeft[CardException]
      }
  }

}
