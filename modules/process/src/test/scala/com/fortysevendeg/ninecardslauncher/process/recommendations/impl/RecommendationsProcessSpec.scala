package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.process.recommendations.RecommendedAppsException
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices, RecommendationResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait RecommendationsProcessSpecification
  extends Specification
  with Mockito
  with RecommendationsProcessData {

  val apiException = ApiServiceException("")

  val recommendationApps = generateRecommendationApps()

  trait RecommendationsProcessScope
    extends Scope {

    val contextSupport = mock[ContextSupport]

    val apiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val process = new RecommendationsProcessImpl(apiServices, mockPersistenceServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        CatsService(Task(Xor.right(requestConfig)))
    }

  }

}

class RecommendationsProcessSpec
  extends RecommendationsProcessSpecification {

  "getRecommendedApps" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
          CatsService(Task(Xor.right(RecommendationResponse(statusCodeOk, recommendationApps))))

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).value.run

        there was one(apiServices).getRecommendedApps(category.name, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Xor.Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual recommendationApps.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
          CatsService(Task(Xor.left(apiException)))

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[RecommendedAppsException]
          }
      }

  }

  "getRecommendedAppsByPackages" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
          CatsService(Task(Xor.right(RecommendationResponse(statusCodeOk, recommendationApps))))

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).value.run

        there was one(apiServices).getRecommendedAppsByPackages(likePackages, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Xor.Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual recommendationApps.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
          CatsService(Task(Xor.left(apiException)))

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[RecommendedAppsException]
          }
      }

  }

}
