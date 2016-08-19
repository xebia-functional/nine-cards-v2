package com.fortysevendeg.ninecardslauncher.process.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.api.ApiServiceException
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, FindUserByIdRequest, AndroidIdNotFoundException, PersistenceServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

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

        val result = apiUtils.getRequestConfig(mockContextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ApiServiceException]
          }
        }
      }

    "returns an ApiServiceException when there is an active user but doesn't exists in the database" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns Service(Task(Answer(None)))

        val result = apiUtils.getRequestConfig(mockContextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ApiServiceException]
          }
        }

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have api key" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns Service(Task(Answer(Some(user.copy(apiKey = None)))))

        val result = apiUtils.getRequestConfig(mockContextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ApiServiceException]
          }
        }

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns an ApiServiceException when there is an active in the database but doesn't have a session token" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(any) returns Service(Task(Answer(Some(user.copy(sessionToken = None)))))

        val result = apiUtils.getRequestConfig(mockContextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ApiServiceException]
          }
        }

        there was one(mockPersistenceServices).findUserById(FindUserByIdRequest(userId))
      }

    "returns a request config with the correct data" in
      new ApiUtilsScope {

        mockContextSupport.getActiveUserId returns Some(userId)
        mockPersistenceServices.findUserById(FindUserByIdRequest(userId)) returns Service(Task(Answer(Some(user))))
        mockPersistenceServices.getAndroidId(any) returns Service(Task(Answer(androidId)))

        val result = apiUtils.getRequestConfig(mockContextSupport).run.run
        result must beLike {
          case Answer(resultRequestConfig) =>
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
