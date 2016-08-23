package com.fortysevendeg.ninecardslauncher.process.utils

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.services.api.models.User
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, RequestConfig}
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, FindUserByIdRequest, AndroidIdNotFoundException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait ApiUtilsSpecification
  extends Specification
  with Mockito {

  val persistenceServicesException = PersistenceServiceException("")
  val androidIdNotFoundException = AndroidIdNotFoundException("")

  trait ApiUtilsScope
    extends Scope
    with ApiUtilsData {

    val mockContextSupport = mock[ContextSupport]
    mockContextSupport.getActiveUserId returns Some(userDBId)
    val mockPersistenceServices = mock[PersistenceServices]
    val apiUtils = new ApiUtils(mockPersistenceServices)
    val mockRequestConfig = mock[RequestConfig]
    val mockUser = mock[User]

    mockPersistenceServices.getAndroidId(mockContextSupport) returns
      CatsService(Task(Xor.right(androidId)))

    mockPersistenceServices.findUserById(FindUserByIdRequest(userDBId)) returns
      CatsService(Task(Xor.right(Some(user))))

    mockRequestConfig.deviceId returns androidId
    mockRequestConfig.token returns token

  }

  trait SessionTokenNoneUserApiUtilsScope {

    self: ApiUtilsScope =>

    mockPersistenceServices.findUserById(FindUserByIdRequest(userDBId)) returns
      CatsService(Task(Xor.right(Some(userSessionTokenNone))))

  }

  trait ErrorUserApiUtilsScope {

    self: ApiUtilsScope =>

    mockPersistenceServices.findUserById(FindUserByIdRequest(userDBId)) returns
      CatsService(Task(Xor.left(persistenceServicesException)))

  }

  trait ErrorAndroidIdApiUtilsScope {

    self: ApiUtilsScope =>

    mockPersistenceServices.getAndroidId(mockContextSupport) returns
      CatsService(Task(Xor.left(androidIdNotFoundException)))

  }

}

class ApiUtilsSpec
  extends ApiUtilsSpecification {

  "Api Utils" should {

    "returns a request config with a correct deviceId and token" in
      new ApiUtilsScope {
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Xor.Right(resultRequestConfig) =>
            resultRequestConfig.deviceId shouldEqual mockRequestConfig.deviceId
            resultRequestConfig.token shouldEqual mockRequestConfig.token
        }
      }

    "returns an ApiServiceException when the session token doesn't exists" in
      new ApiUtilsScope with SessionTokenNoneUserApiUtilsScope {
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ApiServiceException]
        }
      }

    "returns an ApiServiceException when the session token return a exception" in
      new ApiUtilsScope with ErrorUserApiUtilsScope {
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ApiServiceException]
        }
      }

    "returns an ApiServiceException when the android id can't be found" in
      new ApiUtilsScope with ErrorAndroidIdApiUtilsScope {
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ApiServiceException]
        }
      }

  }

}
