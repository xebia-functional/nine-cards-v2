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
  extends Specification
    with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  val appsInstalledException = AppsInstalledException("")

  val apiServiceException = ApiServiceException("")


  trait CollectionProcessScope2
    extends Scope
      with CollectionProcessImplData2 {

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

    "returns a sequence of collections for a valid request without cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithoutCards)))

        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollectionWithoutCards.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollectionWithoutCards.map(_.name)
            resultSeqCollection map (_.cards) shouldEqual Seq(Seq.empty, Seq.empty)
        }
      }

    "returns a sequence of collections for a valid request " in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))

        val result = collectionProcess.getCollections.value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqServicesCollectionWithCards.size
            resultSeqCollection map (_.name) shouldEqual seqServicesCollectionWithCards.map(_.name)
            resultSeqCollection map (_.cards.size) shouldEqual seqServicesCollectionWithCards.map(_.cards.size)
        }
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.getCollections.value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "getCollectionById" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns
          TaskService(Task(Either.right(Some(servicesCollectionById))))

        val result = collectionProcess.getCollectionById(collectionId).value.run
        result must beLike {
          case Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionById.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns
          TaskService(Task(Either.right(None)))

        val result = collectionProcess.getCollectionById(collectionId).value.run
        result shouldEqual Either.right(None)
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(FindCollectionByIdRequest(collectionId)) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.getCollectionById(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "getCollectionByCategory" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns TaskService(Task(Either.right(Some(servicesCollectionByCategory))))

        val result = collectionProcess.getCollectionByCategory(appsCategoryGame).value.run
        result must beLike {
          case Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionByCategory.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns TaskService(Task(Either.right(None)))
        val result = collectionProcess.getCollectionByCategory(appsCategoryGame).value.run
        result shouldEqual Either.right(None)
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionByCategory(appsCategoryGame.name) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.getCollectionByCategory(appsCategoryGame).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }


  "getCollectionBySharedCollectionId" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.right(Some(servicesCollectionBySharedCollectionId))))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result must beLike {
          case Right(resultCollection) => resultCollection must beSome.which { collection =>
            collection.name shouldEqual servicesCollectionBySharedCollectionId.name
            collection.sharedCollectionId shouldEqual Option(sharedCollectionId)
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.right(None)))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result shouldEqual Right(None)
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns
          TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "createCollectionsFromFormedCollections" should {

    "the size of collections should be equal to size of collections passed by parameter" in
      new CollectionProcessScope2 {

        val collections: Seq[Collection] = seqFormedCollection map (_ => collectionForUnformedItem)
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.right(collections)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beLike {
          case Right(resultSeqCollection) =>
            resultSeqCollection.size shouldEqual seqFormedCollection.size
            resultSeqCollection map (_.name) shouldEqual seqFormedCollection.map(_.name)
        }
      }

    "returns CollectionExceptionImpl when persistence services fails" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.addCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.createCollectionsFromFormedCollections(seqFormedCollection)(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

  }

  "generatePrivateCollections" should {

    "return a seq empty if number of cards by category is < minAppsToAdd" in
      new CollectionProcessScope2 {

        val result = collectionProcess.generatePrivateCollections(seqUnformedAppsForPrivateCollections)(contextSupport).value.run
        result shouldEqual Right(Seq.empty)
      }

  }


  "addCollection" should {

    "returns a the collection added for a valid request without cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionAddWithoutCards)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Either.right(servicesCollectionAddedWithoutCards)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result shouldEqual Right(collectionAddedWithoutCards)
      }

    "returns a the collection added for a valid request with cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionAddWithCards)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Either.right(servicesCollectionAddedWithCards)))

        val result = collectionProcess.addCollection(addCollectionRequestWithCards).value.run
        result shouldEqual Right(collectionAddedWithCards)
      }


    "returns a CollectionException if service throws an exception fetching the collections" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception adding the new collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionAddWithoutCards)))
        mockPersistenceServices.addCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.addCollection(addCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionDelete))))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionRemoved)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(cardsRemoved)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.right(Seq(updatedCollection))))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception deleting the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionDelete))))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception deleting the cards by the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionDelete))))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionRemoved)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionDelete))))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionRemoved)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(cardsRemoved)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collections" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionDelete))))
        mockPersistenceServices.deleteCollection(any) returns TaskService(Task(Either.right(collectionRemoved)))
        mockPersistenceServices.deleteCardsByCollection(any) returns TaskService(Task(Either.right(cardsRemoved)))
        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCollection(collectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception removing collections" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(cardsRemoved)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception removing cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(collectionsRemoved)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.cleanCollections().value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.right(Seq(updatedCollections))))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CollectionException if the service throws an exception fetching the collection by position" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        mockPersistenceServices.updateCollections(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCollection(0, newPosition).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionById))))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.right(updatedCollection)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result shouldEqual Right(editedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionById))))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.editCollection(collectionId, editCollectionRequest).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionById))))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.right(updatedCollection)))

        val result = collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).value.run
        result shouldEqual Right(updatedSharedCollection)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Option(servicesCollectionById))))
        mockPersistenceServices.updateCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "addPackages" should {

    "returns a CollectionException when passing a collectionId that doesn't exists" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(None)))

        val result = collectionProcess.addPackages(collectionId, Seq.empty)(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]

        there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      }

    "returns a Xor.Right[Unit] but doesn't call to persistence and api services when all applications are " +
      "already included on the collection" in new CollectionProcessScope2 {

      mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Some(servicesCollectionById))))
      mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardsAddPackages)))

      val result = collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).value.run
      result shouldEqual Either.right((): Unit)

      there was one(mockPersistenceServices).findCollectionById(FindCollectionByIdRequest(collectionId))
      there was one(mockPersistenceServices).fetchCardsByCollection(FetchCardsByCollectionRequest(collectionId))
      there was no(mockPersistenceServices).fetchAppByPackages(any)
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      there was no(mockPersistenceServices).addCards(any)
    }

    "returns a Xor.Right[Unit] but doesn't call to api services when all applications are included on the collection " +
      "or installed in the device" in new CollectionProcessScope2 {

      mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Some(servicesCollectionById))))
      val (firstHalf, secondHalf) = seqServicesCardsAddPackages.splitAt(seqServicesCardsAddPackages.size / 2)
      mockPersistenceServices.fetchCardsByCollection(any) returns
        TaskService(Task(Either.right(firstHalf)))
      val secondHalfApps = seqServicesAppAddPackages.filter(app => secondHalf.exists(_.packageName.contains(app.packageName)))
      mockPersistenceServices.fetchAppByPackages(any) returns
        TaskService(Task(Either.right(secondHalfApps)))
      mockPersistenceServices.addCards(any) returns
        TaskService(Task(Either.right(secondHalf)))

      val result = collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).value.run
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
      new CollectionProcessScope2 {

        mockPersistenceServices.findCollectionById(any) returns TaskService(Task(Either.right(Some(servicesCollectionById))))
        val (firstHalf, secondHalf) = seqServicesCardsAddPackages.splitAt(seqServicesCardsAddPackages.size / 2)
        mockPersistenceServices.fetchCardsByCollection(any) returns
          TaskService(Task(Either.right(firstHalf)))
        mockPersistenceServices.fetchAppByPackages(any) returns
          TaskService(Task(Either.right(Seq.empty)))
        val secondHalfPackages = categorizedDetailPackages.filter(p => secondHalf.exists(_.packageName.contains(p.packageName)))
        mockApiServices.googlePlayPackagesDetail(any)(any) returns
          TaskService(Task(Either.right(GooglePlayPackagesDetailResponse(200, secondHalfPackages))))
        mockPersistenceServices.addCards(any) returns
          TaskService(Task(Either.right(secondHalf)))

        val result = collectionProcess.addPackages(collectionId, seqServicesCardsAddPackages.flatMap(_.packageName))(contextSupport).value.run
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
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesAppRank)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.right(rankAppsResponseList)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result shouldEqual Right(packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.rankApps()(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }

    "returns the ordered packages even if the service throws an exception getting the country location" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesAppRank)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.left(apiServiceException)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.right(rankAppsResponseList)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result shouldEqual Right(packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(seqServicesAppRank)))
        mockAwarenessServices.getLocation(any) returns TaskService(Task(Either.right(awarenessLocation)))
        mockApiServices.rankApps(any, any)(any) returns TaskService(Task(Either.left(apiServiceException)))

        val result = collectionProcess.rankApps()(contextSupport).value.run
        result must beAnInstanceOf[Left[CollectionException, _]]
      }
  }

  "addCards" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardByCollection)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Either.right(seqServicesAddedCards)))

        val result = collectionProcess.addCards(collectionId, seqAddCardsRequest).value.run
        result must beLike {
          case Right(resultCards) =>
            resultCards map (_.term) shouldEqual (seqServicesAddedCards map (_.term))
        }
      }

    "returns a CardException if service throws an exception fetching the cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.addCards(collectionId, seqAddCardsRequest).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns an CardException if the service throws an exception adding the new cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardByCollection)))
        mockPersistenceServices.addCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.addCards(collectionId, seqAddCardsRequest).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.right(cardRemoved)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardByCollection)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result shouldEqual Right((): Unit)

        there was one(mockPersistenceServices).updateCards(any)
      }

    "returns a successful when return sequence empty" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.right(cardRemoved)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(Seq.empty)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result shouldEqual Right((): Unit)

        there was one(mockPersistenceServices).updateCards(UpdateCardsRequest(Seq.empty))
      }

    "returns a CardException if the service throws an exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCard(any, any) returns TaskService(Task(Either.right(cardRemoved)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardByCollection)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCard(collectionId, cardId).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "deleteAllCardsByPackageName" should {

    "returns a successful when delete all Seq.empty of Cards in all collection by package name" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.right(seqServicesCollectionWithCards)))
        val result = collectionProcess.deleteAllCardsByPackageName(packageName).value.run
        result shouldEqual Right((): Unit)

        there was no(mockPersistenceServices).deleteCard(any, any)
        there was no(mockPersistenceServices).fetchCardsByCollection(any)
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCollections returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.deleteAllCardsByPackageName(packageName).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "deleteCards" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCards(any, any) returns TaskService(Task(Either.right(cardsRemoved)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardDelete)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))

        val result = collectionProcess.deleteCards(collectionId, seqCardIds).value.run
        result shouldEqual Right((): Unit)

      }

    "returns a successful when return sequence empty of cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCards(any, any) returns TaskService(Task(Either.right(cardsRemoved)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(Seq.empty)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))

        val result = collectionProcess.deleteCards(collectionId, seqCardIds).value.run
        result shouldEqual Right((): Unit)

        there was one(mockPersistenceServices).updateCards(UpdateCardsRequest(Seq.empty))
      }
    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCards(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.deleteCards(collectionId, seqCardIds).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope2 {

        mockPersistenceServices.deleteCards(any, any) returns TaskService(Task(Either.right(cardId)))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardDelete)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.deleteCards(collectionId, seqCardIds).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCardReorder))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardReorder)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))

        val result = collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCardReorder))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardReorder)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(1))))

        val result = collectionProcess.reorderCard(collectionId, cardIdReorder, samePositionReorder).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CardException if the service throws an exception finding the card by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception fetching the cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCardReorder))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception updating the cards" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(servicesCardReorder))))
        mockPersistenceServices.fetchCardsByCollection(any) returns TaskService(Task(Either.right(seqServicesCardReorder)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(editCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.right(updatedCard)))

        val result = collectionProcess.editCard(collectionId, cardIdEdit, newNameEditCard).value.run
        result shouldEqual Right(editedCard.copy(term = newNameEditCard))
      }

    "returns a CardException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = collectionProcess.editCard(collectionId, cardId, newNameEditCard).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.findCardById(any) returns TaskService(Task(Either.right(Option(editCard))))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = collectionProcess.editCard(collectionId, cardId, newNameEditCard).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCards returns TaskService(Task(Either.right(seqServicesCardUpdateNoInstallation)))
        mockPersistenceServices.updateCards(any) returns TaskService(Task(Either.right(Seq(updatedCards))))
        mockAppsServices.getApplication(packageNameApplication)(contextSupport) returns TaskService(Task(Either.right(applicationUpdateNoInstallation)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageNameApplication)(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope2 {

        mockPersistenceServices.fetchCards returns TaskService(Task(Either.right(seqServicesCardUpdateNoInstallation)))
        mockPersistenceServices.updateCard(any) returns TaskService(Task(Either.right(updatedCard)))
        mockAppsServices.getApplication(packageNameApplication)(contextSupport) returns TaskService(Task(Either.left(appsInstalledException)))

        val result = collectionProcess.updateNoInstalledCardsInCollections(packageNameApplication)(contextSupport).value.run
        result must beAnInstanceOf[Left[CardException, _]]
      }
  }

}
