package cards.nine.process.collection.impl

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.ApplicationValues._
import cards.nine.commons.test.data.CardValues._
import cards.nine.commons.test.data.CollectionValues._
import cards.nine.commons.test.data.{ApiTestData, CollectionTestData}
import cards.nine.models.{CollectionProcessConfig, NineCardsIntent, RequestConfig}
import cards.nine.process.collection.{CardException, CollectionException}
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceException, ApiServices}
import cards.nine.services.apps.{AppsInstalledException, AppsServices}
import cards.nine.services.awareness.AwarenessServices
import cards.nine.services.contacts.ContactsServices
import cards.nine.services.persistence._
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait CollectionProcessImplSpecification
  extends TaskServiceSpecification
  with ApiTestData
  with Mockito {

  val persistenceServiceException = PersistenceServiceException("")

  val appsInstalledException = AppsInstalledException("")

  val apiServiceException = ApiServiceException("")

  trait CollectionProcessScope
    extends Scope
    with CollectionTestData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val collectionProcessConfig = CollectionProcessConfig(Map.empty)

    val mockPersistenceServices = mock[PersistenceServices]
    val mockIntent = mock[Intent]
    val mockNineCardIntent = mock[NineCardsIntent]

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

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection.map(_.copy(cards = Seq.empty)))

        collectionProcess.getCollections.mustRight { resultSeqCollection =>
          resultSeqCollection.size shouldEqual seqCollection.size
          resultSeqCollection map (_.name) shouldEqual seqCollection.map(_.name)
          resultSeqCollection map (_.cards) shouldEqual Seq(Seq.empty, Seq.empty, Seq.empty)
        }
      }

    "returns a sequence of collections for a valid request " in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)

        collectionProcess.getCollections.mustRight { resultSeqCollection =>
          resultSeqCollection.size shouldEqual seqCollection.size
          resultSeqCollection map (_.name) shouldEqual seqCollection.map(_.name)
          resultSeqCollection map (_.cards.size) shouldEqual seqCollection.map(_.cards.size)
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

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(collection.copy(id = collectionId)))

        collectionProcess.getCollectionById(collectionId).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection.name
          }
        }
      }

    "returns None for a valid request if the collection id don't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(None)
        collectionProcess.getCollectionById(collectionId).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollectionById(collectionId).mustLeft[CollectionException]
      }
  }

  "getCollectionByCategory" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryStr) returns serviceRight(Some(collection.copy(appsCategory = Option(appsCategory))))

        collectionProcess.getCollectionByCategory(appsCategory).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection.name
          }
        }
      }

    "returns None for a valid request if the collection id doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryStr) returns serviceRight(None)
        collectionProcess.getCollectionByCategory(appsCategory).mustRightNone
      }

    "returns a CollectionException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionByCategory(appsCategoryStr) returns serviceLeft(persistenceServiceException)
        collectionProcess.getCollectionByCategory(appsCategory).mustLeft[CollectionException]
      }
  }


  "getCollectionBySharedCollectionId" should {

    "returns a collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollectionBySharedCollectionId(sharedCollectionId) returns serviceRight(Option(collection.copy(sharedCollectionId = Option(sharedCollectionId))))

        collectionProcess.getCollectionBySharedCollectionId(sharedCollectionId).mustRight { resultCollection =>
          resultCollection must beSome.which { collection =>
            collection.name shouldEqual collection.name
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

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.addCollections(any) returns serviceRight(seqCollection)

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

        collectionProcess.generatePrivateCollections(seqApplicationData)(contextSupport).mustRight(_ shouldEqual Seq.empty)
      }

  }


  "addCollection" should {

    "returns a the collection added for a valid request without cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection map (_.copy(cards = Seq.empty)))
        mockPersistenceServices.addCollection(any) returns serviceRight(collection.copy(id = seqCollection.size, position = seqCollection.size, cards = Seq.empty))

        val result = collectionProcess.addCollection(collectionData.copy(cards = Seq.empty)).run
        result shouldEqual Right(collection.copy(id = seqCollection.size, position = seqCollection.size, cards = Seq.empty))
      }

    "returns a the collection added for a valid request with cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.addCollection(any) returns serviceRight(collection.copy(id = seqCollection.size, position = seqCollection.size))
        val result = collectionProcess.addCollection(collectionData).run
        result shouldEqual Right(collection.copy(id = seqCollection.size, position = seqCollection.size))
      }


    "returns a CollectionException if service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.addCollection(collectionData).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception adding the new collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.addCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.addCollection(collectionData).mustLeft[CollectionException]
      }
  }

  "deleteCollection" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy()))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(deletedCards)
        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
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

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.deleteCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception deleting the cards by the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(deletedCards)
        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.deleteCollection(any) returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteCardsByCollection(any) returns serviceRight(deletedCards)
        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.updateCollections(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCollection(collectionId).mustLeft[CollectionException]
      }
  }

  "cleanCollections" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteAllCards() returns serviceRight(deletedCards)
        collectionProcess.cleanCollections().mustRightUnit
      }

    "returns a CollectionException if the service throws an exception removing collections" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceLeft(persistenceServiceException)
        mockPersistenceServices.deleteAllCards() returns serviceRight(deletedCards)
        collectionProcess.cleanCollections().mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception removing cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteAllCollections() returns serviceRight(deletedCollection)
        mockPersistenceServices.deleteAllCards() returns serviceLeft(persistenceServiceException)
        collectionProcess.cleanCollections().mustLeft[CollectionException]
      }
  }

  "reorderCollection" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.updateCollections(any) returns serviceRight(Seq(updatedCollections))
        collectionProcess.reorderCollection(0, collectionNewPosition).mustRightUnit
      }

    "returns a CollectionException if the service throws an exception fetching the collection by position" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, collectionNewPosition).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception fetching the collections" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, collectionNewPosition).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        mockPersistenceServices.updateCollections(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.reorderCollection(0, collectionNewPosition).mustLeft[CollectionException]
      }
  }

  "editCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection))
        mockPersistenceServices.updateCollection(any) returns serviceRight(updatedCollection)
        val editedCollectionData = collectionData.copy(
          name = newCollectionName,
          icon = newCollectionIcon,
          themedColorIndex = newThemedColorIndex,
          appsCategory = Option(applicationCategory))
        val result = collectionProcess.editCollection(collectionId, editedCollectionData).run
        result shouldEqual Right(collection.copy(
          name = newCollectionName,
          icon = newCollectionIcon,
          themedColorIndex = newThemedColorIndex,
          appsCategory = Option(applicationCategory)))
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCollection(collectionId, collectionData).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.updateCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCollection(collectionId, collectionData).mustLeft[CollectionException]
      }
  }

  "updateSharedCollection" should {

    "returns a the updated collection for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.updateCollection(any) returns serviceRight(updatedCollection)

        val result = collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).run
        result shouldEqual Right(collection.copy(sharedCollectionId = Option(newSharedCollectionId)))
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).mustLeft[CollectionException]
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Option(collection.copy(id = collectionId)))
        mockPersistenceServices.updateCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.updateSharedCollection(collectionId, newSharedCollectionId).mustLeft[CollectionException]
      }
  }

  "addPackages" should {

    "returns a CollectionException when passing a collectionId that doesn't exists" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(None)
        collectionProcess.addPackages(collectionId, Seq.empty)(contextSupport).mustLeft[CollectionException]

        there was one(mockPersistenceServices).findCollectionById(collectionId)
      }

    "returns a Xor.Right[Unit] but doesn't call to persistence and api services when all applications are " +
      "already included on the collection" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(collection.copy(id = collectionId)))
      mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)

      val result = collectionProcess.addPackages(collectionId, seqCard.flatMap(_.packageName))(contextSupport).mustRightUnit

      there was one(mockPersistenceServices).findCollectionById(collectionId)
      there was one(mockPersistenceServices).fetchCardsByCollection(collectionId)
      there was no(mockPersistenceServices).fetchAppByPackages(any)
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      there was no(mockPersistenceServices).addCards(any)
    }

    "returns a Xor.Right[Unit] but doesn't call to api services when all applications are included on the collection " +
      "or installed in the device" in new CollectionProcessScope {

      mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(collection.copy(id = collectionId)))
      val (firstHalf, secondHalf) = seqCard.splitAt(seqCard.size / 2)
      mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(firstHalf)
      val secondHalfApps = seqApplication.filter(application => secondHalf.exists(_.packageName.contains(application.packageName)))
      mockPersistenceServices.fetchAppByPackages(any) returns serviceRight(secondHalfApps)
      mockPersistenceServices.addCards(any) returns serviceRight(secondHalf)

      val result = collectionProcess.addPackages(collectionId, seqCard.flatMap(_.packageName))(contextSupport).mustRightUnit

      there was one(mockPersistenceServices).findCollectionById(collectionId)
      there was one(mockPersistenceServices).fetchCardsByCollection(collectionId)
      there was one(mockPersistenceServices).fetchAppByPackages(secondHalf.flatMap(_.packageName))
      there was no(mockApiServices).googlePlayPackagesDetail(any)(any)
      there was one(mockPersistenceServices).addCards(Seq((collectionId, seqCardData)))
    }.pendingUntilFixed("Issue #943")

    "returns a Xor.Right[Unit] and call to api services with the applications not installed on the device" in
      new CollectionProcessScope {

        mockPersistenceServices.findCollectionById(any) returns serviceRight(Some(collection.copy(id = collectionId)))
        val (firstHalf, secondHalf) = seqCard.splitAt(seqCard.size / 2)
        mockPersistenceServices.fetchCardsByCollection(any) returns
          serviceRight(firstHalf)
        mockPersistenceServices.fetchAppByPackages(any) returns
          serviceRight(Seq.empty)
        val secondHalfPackages = categorizedDetailPackages.filter(p => secondHalf.exists(_.packageName.contains(p.packageName)))
        mockApiServices.googlePlayPackagesDetail(any)(any) returns
          serviceRight(secondHalfPackages)
        mockPersistenceServices.addCards(any) returns
          serviceRight(secondHalf)

        collectionProcess.addPackages(collectionId, seqCard.flatMap(_.packageName))(contextSupport).mustRightUnit

        there was one(mockPersistenceServices).findCollectionById(collectionId)
        there was one(mockPersistenceServices).fetchCardsByCollection(collectionId)
        there was one(mockPersistenceServices).fetchAppByPackages(secondHalf.flatMap(_.packageName))
        there was one(mockApiServices).googlePlayPackagesDetail(secondHalf.flatMap(_.packageName))(mockRequestConfig)
        there was one(mockPersistenceServices).addCards(Seq((collectionId, seqCardData)))
      }.pendingUntilFixed("Issue #943")

  }

  "rankApps" should {

    "returns a the ordered packages for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqApplication)
        mockAwarenessServices.getLocation(any) returns serviceRight(awarenessLocation)
        mockApiServices.rankApps(any, any)(any) returns serviceRight(rankApps)

        collectionProcess.rankApps()(contextSupport).mustRight(_ shouldEqual packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.rankApps()(contextSupport).mustLeft[CollectionException]
      }

    "returns the ordered packages even if the service throws an exception getting the country location" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqApplication)
        mockAwarenessServices.getLocation(any) returns serviceLeft(apiServiceException)
        mockApiServices.rankApps(any, any)(any) returns serviceRight(rankApps)

        collectionProcess.rankApps()(contextSupport).mustRight(_ shouldEqual packagesByCategory)
      }

    "returns a CollectionException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns serviceRight(seqApplication)
        mockAwarenessServices.getLocation(any) returns serviceRight(awarenessLocation)
        mockApiServices.rankApps(any, any)(any) returns serviceLeft(apiServiceException)

        collectionProcess.rankApps()(contextSupport).mustLeft[CollectionException]
      }
  }

  "addCards" should {

    "returns a sequence of cards for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.addCards(any) returns serviceRight(seqCard)

        collectionProcess.addCards(collectionId, seqCardData).mustRight { resultCards =>
          resultCards map (_.term) shouldEqual (seqCard map (_.term))
        }
      }

    "returns a CardException if service throws an exception fetching the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.addCards(collectionId, seqCardData).mustLeft[CardException]
      }

    "returns an CardException if the service throws an exception adding the new cards" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.addCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.addCards(collectionId, seqCardData).mustLeft[CardException]
      }
  }

  "deleteCard" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(deletedCard)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCard(collectionId, cardId).mustRightUnit

        there was one(mockPersistenceServices).updateCards(any)
      }

    "returns a successful when return sequence empty" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(deletedCard)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(Seq.empty)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCard(collectionId, cardId).mustRightUnit

        there was one(mockPersistenceServices).updateCards(Seq.empty)
      }

    "returns a CardException if the service throws an exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteCard(collectionId, cardId).mustLeft[CardException]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCard(any, any) returns serviceRight(deletedCard)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCard(collectionId, cardId).mustLeft[CardException]
      }
  }

  "deleteAllCardsByPackageName" should {

    "returns a successful when delete all Seq.empty of Cards in all collection by package name" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceRight(seqCollection)
        collectionProcess.deleteAllCardsByPackageName(cardPackageName).mustRightUnit

        there was no(mockPersistenceServices).deleteCard(any, any)
        there was no(mockPersistenceServices).fetchCardsByCollection(any)
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCollections returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteAllCardsByPackageName(cardPackageName).mustLeft[CardException]
      }
  }

  "deleteCards" should {

    "returns a successful answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(deletedCards)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCards(collectionId, seqCard.map(_.id)).mustRightUnit
      }

    "returns a successful when return sequence empty of cards" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(deletedCards)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(Seq.empty)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.deleteCards(collectionId, seqCard.map(_.id)).mustRightUnit

        there was one(mockPersistenceServices).updateCards(Seq.empty)
      }
    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceLeft(persistenceServiceException)
        collectionProcess.deleteCards(collectionId, seqCard.map(_.id)).mustLeft[CardException]
      }

    "returns a CardException if the service throws a exception" in
      new CollectionProcessScope {

        mockPersistenceServices.deleteCards(any, any) returns serviceRight(cardId)
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.deleteCards(collectionId, seqCard.map(_.id)).mustLeft[CardException]
      }
  }

  "reorderCard" should {

    "returns a empty answer for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustRightUnit
      }

    "returns an empty answer for a valid request, even if new position is the same" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
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

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustLeft[CardException]
      }

    "returns a CardException if the service throws an exception updating the cards" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.fetchCardsByCollection(any) returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.reorderCard(collectionId, cardIdReorder, newPositionReorder).mustLeft[CardException]
      }
  }

  "editCard" should {

    "returns a the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.updateCard(any) returns serviceRight(updatedCard)

        collectionProcess.editCard(collectionId, card.id, newCardName).mustRight { r =>
          r shouldEqual card.copy(term = newCardName)
        }
      }

    "returns a CardException if the service throws an exception finding the collection by Id" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceLeft(persistenceServiceException)
        collectionProcess.editCard(collectionId, card.id, newCardName).mustLeft[CardException]
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.findCardById(any) returns serviceRight(Option(card))
        mockPersistenceServices.updateCard(any) returns serviceLeft(persistenceServiceException)

        collectionProcess.editCard(collectionId, card.id, newCardName).mustLeft[CardException]
      }
  }

  "updateNoInstalledCardsInCollections" should {

    "returns Unit if the updated card for a valid request" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns serviceRight(seqCard)
        mockPersistenceServices.updateCards(any) returns serviceRight(Seq(updatedCards))
        mockAppsServices.getApplication(applicationPackageName)(contextSupport) returns serviceRight(applicationData)

        collectionProcess.updateNoInstalledCardsInCollections(applicationPackageName)(contextSupport).mustRightUnit
      }

    "returns a CardException if the service throws an exception updating the collection" in
      new CollectionProcessScope {

        mockPersistenceServices.fetchCards returns serviceRight(seqCard)
        mockPersistenceServices.updateCard(any) returns serviceRight(updatedCard)
        mockAppsServices.getApplication(applicationPackageName)(contextSupport) returns serviceLeft(appsInstalledException)

        collectionProcess.updateNoInstalledCardsInCollections(applicationPackageName)(contextSupport).mustLeft[CardException]
      }
  }

}
