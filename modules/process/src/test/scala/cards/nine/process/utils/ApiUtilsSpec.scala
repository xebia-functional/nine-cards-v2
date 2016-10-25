package cards.nine.process.utils

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.UserTestData
import cards.nine.commons.test.data.UserValues._
import cards.nine.services.api.ApiServiceException
import cards.nine.services.persistence.{AndroidIdNotFoundException, PersistenceServiceException, PersistenceServices}
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ApiUtilsSpecification
  extends Specification
  with Mockito
  with UserTestData {

  val persistenceServicesException = PersistenceServiceException("")
  val androidIdNotFoundException = AndroidIdNotFoundException("")

  trait ApiUtilsScope
    extends Scope {

    val mockContextSupport = mock[ContextSupport]
    val mockPersistenceServices = mock[PersistenceServices]
    val apiUtils = new ApiUtils(mockPersistenceServices)

  }

}

class ApiUtilsSpec
  extends ApiUtilsSpecification {

  "Api Utils" should {

    "returns an ApiServiceException when there ins't any active user" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns None
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]
      }

    "returns an ApiServiceException when there is an active user but doesn't exists in the database" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(None)))

        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]

        there was one(mockPersistenceServices).findUserById(userId)
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have api key" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user.copy(apiKey = None)))))

        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]

        there was one(mockPersistenceServices).findUserById(userId)
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have a session token" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user.copy(sessionToken = None)))))

        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]

        there was one(mockPersistenceServices).findUserById(userId)
      }

    "returns a request config with the correct data" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Either.right(Some(user))))
        mockPersistenceServices.getAndroidId(any) returns TaskService(Task(Either.right(androidId)))

        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Right(resultRequestConfig) =>
            resultRequestConfig.apiKey shouldEqual apiKey
            resultRequestConfig.sessionToken shouldEqual sessionToken
            resultRequestConfig.androidId shouldEqual androidId
            resultRequestConfig.marketToken shouldEqual Some(marketToken)
        }

        there was one(mockPersistenceServices).findUserById(userId)
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
      }

  }

}
