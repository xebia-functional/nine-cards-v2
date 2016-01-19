package com.fortysevendeg.ninecardslauncher.process.sharedcollections.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.SharedCollectionsExceptions
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.CreatedCollection
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait SharedCollectionsProcessImplSpecification
  extends Specification
  with Mockito {

  val apiException = new ApiServiceException("")

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
        Service(Task(Result.answer(requestConfig)))
    }

  }

  trait ValidSharedCollectionsProcessProcessScope {

    self: SharedCollectionsProcessProcessScope =>

    mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
      Service(Task(Result.answer(shareCollectionList)))

    mockApiServices.createSharedCollection(anyString, anyString, anyString, any, anyString, anyString, any)(any) returns
      Service(Task(Result.answer(createSharedCollectionResponse)))
  }

  trait ErrorSharedCollectionsProcessProcessScope {

    self: SharedCollectionsProcessProcessScope =>

    mockApiServices.getSharedCollectionsByCategory(anyString, anyString, anyInt, anyInt)(any) returns
      Service(Task(Errata(apiException)))

    mockApiServices.createSharedCollection(anyString, anyString, anyString, any, anyString, anyString, any)(any) returns
      Service(Task(Errata(apiException)))
  }

}

class SharedCollectionsProcessImplSpec
  extends SharedCollectionsProcessImplSpecification {

  "getSharedCollectionsByCategory" should {

    "returns a sequence of shared collections for a valid request" in
      new SharedCollectionsProcessProcessScope with ValidSharedCollectionsProcessProcessScope {
        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).run.run
        result must beLike {
          case Answer(shareCollections) =>
            shareCollections.size shouldEqual shareCollectionList.items.size
            shareCollections map (_.name) shouldEqual shareCollectionList.items.map (_.name)
        }
      }

    "returns a SharedCollectionsExceptions if the service throws a exception" in
      new SharedCollectionsProcessProcessScope with ErrorSharedCollectionsProcessProcessScope {
        val result = sharedCollectionsProcess.getSharedCollectionsByCategory(
          category,
          typeShareCollection,
          offset,
          limit)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[SharedCollectionsExceptions]
          }
        }
      }
  }

  "createNewCollection" should {

    "successfully create a collection for a valid request" in
      new SharedCollectionsProcessProcessScope with ValidSharedCollectionsProcessProcessScope {
        val result = sharedCollectionsProcess.createSharedCollection(
          sharedCollection
        )(contextSupport).run.run

        result mustEqual Answer(
          CreatedCollection(
            sharedCollection.name,
            sharedCollection.description,
            sharedCollection.author,
            sharedCollection.packages,
            sharedCollection.category.name,
            sharedCollection.icon,
            sharedCollection.community
          ))
      }

    "return a SharedCollectionsException if the service throws an exception" in
      new SharedCollectionsProcessProcessScope with ErrorSharedCollectionsProcessProcessScope {
        val result = sharedCollectionsProcess.createSharedCollection(
          sharedCollection
        )(contextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[SharedCollectionsExceptions]
          }
        }
      }
  }
}
