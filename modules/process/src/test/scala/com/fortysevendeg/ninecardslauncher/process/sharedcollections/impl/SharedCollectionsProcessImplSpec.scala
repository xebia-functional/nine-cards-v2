package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cats.syntax.either._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.test.TaskServiceTestOps._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.{SharedCollectionsConfigurationException, SharedCollectionsException}
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.{NotSubscribed, Owned, Subscribed, SubscriptionType}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceConfigurationException, ApiServiceException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.reflect.ClassTag

trait SharedCollectionsProcessImplSpecification
  extends Specification
  with Mockito {

  val apiException = ApiServiceException("")
  val apiConfigException = ApiServiceConfigurationException("")

  trait SharedCollectionsProcessProcessScope
    extends Scope
      with SharedCollectionsProcessImplData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val sharedCollectionsProcess = new SharedCollectionsProcessImpl(
      apiServices = mockApiServices,
      persistenceServices = mockPersistenceServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        TaskService(Task(Either.right(requestConfig)))
    }

    def mustLeft[T <: NineCardException](service: TaskService[_])(implicit classTag: ClassTag[T]): Unit =
      service.value.run must beLike {
        case Left(e) => e must beAnInstanceOf[T]
      }

  }

}

class SharedCollectionsProcessImplSpec
  extends SharedCollectionsProcessImplSpecification {

  "getSharedCollection" should {

    "returns a collection for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollection(anyString)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponse)))
        mockPersistenceServices.fetchCollectionBySharedCollectionId(any) returns
          TaskService(Task(Either.right(None)))

        val result = sharedCollectionsProcess.getSharedCollection(sharedCollectionId)(contextSupport).value.run
        result must beLike {
          case Right(shareCollection) =>
            shareCollection.name shouldEqual sharedCollectionResponseList.items.head.name
            shareCollection.publicCollectionStatus shouldEqual NotSubscribed
        }
      }

    "returns a collection marked as Owned" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollection(anyString)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponse)))
        mockPersistenceServices.fetchCollectionBySharedCollectionId(any) returns
          TaskService(Task(Either.right(collectionPersistenceOwnedSeq.headOption)))

        val result = sharedCollectionsProcess.getSharedCollection(sharedCollectionId)(contextSupport).value.run
        result must beLike {
          case Right(shareCollection) =>
            shareCollection.name shouldEqual sharedCollectionResponseList.items.head.name
            shareCollection.publicCollectionStatus shouldEqual Owned
        }
      }

    "returns a sequence of shared collections for a valid request where the first one is marked as Subscribed" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollection(anyString)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponse)))
        mockPersistenceServices.fetchCollectionBySharedCollectionId(any) returns
          TaskService(Task(Either.right(collectionPersistenceSubscribedSeq.headOption)))

        val result = sharedCollectionsProcess.getSharedCollection(sharedCollectionId)(contextSupport).value.run
        result must beLike {
          case Right(shareCollection) =>
            shareCollection.name shouldEqual sharedCollectionResponseList.items.head.name
            shareCollection.publicCollectionStatus shouldEqual Subscribed
        }
      }

    "returns a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollection(anyString)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](
          sharedCollectionsProcess.getSharedCollection(sharedCollectionId)(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollection(anyString)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](
          sharedCollectionsProcess.getSharedCollection(sharedCollectionId)(contextSupport))
      }
  }

  "getSharedCollectionsByCategory" should {

    "returns a sequence of shared collections for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(Seq.empty)))

        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual NotSubscribed)
        }
      }

    "returns a sequence of shared collections for a valid request where the first one is marked as Owned" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(collectionPersistenceOwnedSeq)))

        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual Owned)
        }
      }

    "returns a sequence of shared collections for a valid request where the first one is marked as Subscribed" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(collectionPersistenceSubscribedSeq)))

        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual Subscribed)
        }
      }

    "returns a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](
          sharedCollectionsProcess.getSharedCollectionsByCategory(
            category = category,
            typeShareCollection = typeShareCollection,
            offset = offset,
            limit = limit)(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](
          sharedCollectionsProcess.getSharedCollectionsByCategory(
            category = category,
            typeShareCollection = typeShareCollection,
            offset = offset,
            limit = limit)(contextSupport))
      }
  }

  "getPublishedCollections" should {

    "returns a sequence of published collections for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(Seq.empty)))

        val result = sharedCollectionsProcess.getPublishedCollections()(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual NotSubscribed)
        }
      }

    "returns a sequence of shared collections for a valid request where the first one is marked as Owned" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(collectionPersistenceOwnedSeq)))

        val result = sharedCollectionsProcess.getPublishedCollections()(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual Owned)
        }
      }

    "returns a sequence of shared collections for a valid request where the first one is marked as Owned" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.right(sharedCollectionResponseList)))
        mockPersistenceServices.fetchCollectionsBySharedCollectionIds(any) returns
          TaskService(Task(Either.right(collectionPersistenceSubscribedSeq)))

        val result = sharedCollectionsProcess.getPublishedCollections()(contextSupport).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual sharedCollectionResponseList.items.size
            shareCollections map (_.name) shouldEqual sharedCollectionResponseList.items.map(_.name)
            forall(shareCollections map (_.publicCollectionStatus)) ((_: SubscriptionType) shouldEqual Subscribed)
        }
      }

    "returns a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.getPublishedCollections()(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](
          sharedCollectionsProcess.getPublishedCollections()(contextSupport))
      }
  }

  "createSharedCollection" should {

    "successfully create a collection for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.createSharedCollection(anyString, anyString, any, anyString, anyString, any)(any) returns
          TaskService(Task(Either.right(createSharedCollectionResponse)))

        val result = sharedCollectionsProcess.createSharedCollection(createSharedCollection)(contextSupport).value.run

        result shouldEqual Right(sharedCollectionId)
      }

    "return a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.createSharedCollection(anyString, anyString, any, anyString, anyString, any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.createSharedCollection(createSharedCollection)(contextSupport))
      }

    "return a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.createSharedCollection(anyString, anyString, any, anyString, anyString, any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](
          sharedCollectionsProcess.createSharedCollection(createSharedCollection)(contextSupport))
      }
  }

  "updateShareCollection" should {

    "successfully create a collection for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.updateSharedCollection(any, any, any)(any) returns
          TaskService(Task(Either.right(updateSharedCollectionResponse)))

        val result = sharedCollectionsProcess.updateSharedCollection(updateSharedCollection)(contextSupport).value.run

        result shouldEqual Right(sharedCollectionId)
      }

    "return a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.updateSharedCollection(any, any, any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.updateSharedCollection(updateSharedCollection)(contextSupport))
      }

    "return a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.updateSharedCollection(any, any, any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](
          sharedCollectionsProcess.updateSharedCollection(updateSharedCollection)(contextSupport))
      }
  }

  "getSubscriptions" should {

    "returns a sequence of the subscriptions for a valid request" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSubscriptions()(any) returns
          TaskService(Task(Either.right(subscriptionList)))

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.right(publicationList)))

        mockPersistenceServices.fetchCollections returns
          TaskService(Task(Either.right(collectionList)))

        val result = sharedCollectionsProcess.getSubscriptions()(contextSupport).value.run

        result must beLike {
          case Right(subscriptions) =>
            subscriptions.size shouldEqual publicCollectionList.size
            subscriptions map (s => Option(s.sharedCollectionId)) shouldEqual publicCollectionList.map(_._1)
        }
      }

    "returns a SharedCollectionsException if the service throws an exception getting the subscriptions" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSubscriptions()(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.getSubscriptions()(contextSupport))
      }

    "returns a SharedCollectionsExceptions if the service throws a exception getting the published collections" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSubscriptions()(any) returns
          TaskService(Task(Either.right(subscriptionList)))

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.getSubscriptions()(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSubscriptions()(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](sharedCollectionsProcess.getSubscriptions()(contextSupport))
      }

    "returns a SharedCollectionsException if the service throws an exception getting the collections" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSubscriptions()(any) returns
          TaskService(Task(Either.right(subscriptionList)))

        mockApiServices.getPublishedCollections()(any) returns
          TaskService(Task(Either.right(publicationList)))

        mockPersistenceServices.fetchCollections returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.getSubscriptions()(contextSupport))
      }
  }

  "subscribe" should {

    "returns a sequence of the subscriptions for a valid request" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.subscribe(any)(any) returns
          TaskService(Task(Either.right(subscribeResponse)))

        val result = sharedCollectionsProcess.subscribe(sharedCollectionId)(contextSupport).value.run
        result mustEqual Right(())
      }

    "returns a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.subscribe(any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.subscribe(sharedCollectionId)(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.subscribe(any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](sharedCollectionsProcess.subscribe(sharedCollectionId)(contextSupport))
      }
  }

  "unsubscribe" should {

    "returns a sequence of the subscriptions for a valid request" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.unsubscribe(any)(any) returns
          TaskService(Task(Either.right(unsubscribeResponse)))

        val result = sharedCollectionsProcess.unsubscribe(sharedCollectionId)(contextSupport).value.run
        result mustEqual Right(())
      }

    "returns a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.unsubscribe(any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[SharedCollectionsException](sharedCollectionsProcess.unsubscribe(sharedCollectionId)(contextSupport))
      }

    "returns a SharedCollectionsConfigurationException if the service throws a config exception" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.unsubscribe(any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[SharedCollectionsConfigurationException](sharedCollectionsProcess.unsubscribe(sharedCollectionId)(contextSupport))
      }
  }
}
