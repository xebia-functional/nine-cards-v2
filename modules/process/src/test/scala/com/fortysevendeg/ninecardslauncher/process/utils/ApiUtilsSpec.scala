package com.fortysevendeg.ninecardslauncher.process.utils

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.api.ApiServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence.{AndroidIdNotFoundException, FindUserByIdRequest, PersistenceServiceException, PersistenceServices}
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
        result must beAnInstanceOf[Xor.Left[ApiServiceException]]
      }

    "returns an ApiServiceException when there is an active user but doesn't exists in the database" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(None)))
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ApiServiceException]]


        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have api key" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user.copy(apiKey = None)))))
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ApiServiceException]]

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have a session token" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns TaskService(Task(Xor.right(Some(user.copy(sessionToken = None)))))
        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ApiServiceException]]

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns a request config with the correct data" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(FindUserByIdRequest(userId)) returns TaskService(Task(Xor.right(Some(user))))
        mockPersistenceServices.getAndroidId(any) returns TaskService(Task(Xor.right(androidId)))

        val result = apiUtils.getRequestConfig(mockContextSupport).value.run
        result must beLike {
          case Xor.Right(resultRequestConfig) =>
            resultRequestConfig.apiKey shouldEqual apiKey
            resultRequestConfig.sessionToken shouldEqual sessionToken
            resultRequestConfig.androidId shouldEqual androidId
            resultRequestConfig.marketToken shouldEqual Some(marketToken)
        }

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
        there was one(mockPersistenceServices).getAndroidId(mockContextSupport)
      }

  }

}
