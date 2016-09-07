package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsExceptions
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreatedCollection
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait SharedCollectionsProcessImplSpecification
  extends Specification
    with Mockito {

  val apiException = ApiServiceException("")

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
        TaskService(Task(Xor.right(requestConfig)))
    }

  }

}

class SharedCollectionsProcessImplSpec
  extends SharedCollectionsProcessImplSpecification {

  "getSharedCollectionsByCategory" should {

    "returns a sequence of shared collections for a valid request" in
      new SharedCollectionsProcessProcessScope {
        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Xor.right(shareCollectionList)))

        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).value.run
        result must beLike {
          case Xor.Right(shareCollections) =>
            shareCollections.size shouldEqual shareCollectionList.items.size
            shareCollections map (_.name) shouldEqual shareCollectionList.items.map(_.name)
        }
      }

    "returns a SharedCollectionsExceptions if the service throws a exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
          TaskService(Task(Xor.left(apiException)))

        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[SharedCollectionsExceptions]]
      }
  }

  "createNewCollection" should {

    "successfully create a collection for a valid request" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.createSharedCollection(anyString, anyString, anyString, any, anyString, anyString, any)(any) returns
          TaskService(Task(Xor.right(createSharedCollectionResponse)))

        val result = sharedCollectionsProcess.createSharedCollection(
          createSharedCollection
        )(contextSupport).value.run

        result mustEqual Xor.Right(sharedCollectionId)
      }

    "return a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope {

        mockApiServices.createSharedCollection(anyString, anyString, anyString, any, anyString, anyString, any)(any) returns
          TaskService(Task(Xor.left(apiException)))

        val result = sharedCollectionsProcess.createSharedCollection(
          createSharedCollection
        )(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[SharedCollectionsExceptions]]
      }
  }
}
