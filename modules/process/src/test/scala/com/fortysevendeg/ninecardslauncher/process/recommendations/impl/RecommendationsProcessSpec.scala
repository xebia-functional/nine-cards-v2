package com.fortysevendeg.ninecardslauncher.process.recommendations.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.recommendations.RecommendedAppsException
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServiceException, ApiServices, RecommendationResponse}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

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

      apiUtils.getRequestConfig(contextSupport) returns Service(Task(Result.answer(requestConfig)))
    }

  }

}

class RecommendationsProcessSpec
  extends RecommendationsProcessSpecification {

  "getRecommendedApps" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
              Service(Task(Result.answer(RecommendationResponse(statusCodeOk, recommendationApps))))

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).run.run

        there was one(apiServices).getRecommendedApps(category.name, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Answer(response) =>
            response.seq.map(_.packageName).toSet shouldEqual recommendationApps.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
              Service(Task(Errata(apiException)))

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[RecommendedAppsException]
          }
        }
      }

  }

  "getRecommendedAppsByPackages" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
              Service(Task(Result.answer(RecommendationResponse(statusCodeOk, recommendationApps))))

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).run.run

        there was one(apiServices).getRecommendedAppsByPackages(likePackages, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Answer(response) =>
            response.seq.map(_.packageName).toSet shouldEqual recommendationApps.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
              Service(Task(Errata(apiException)))

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[RecommendedAppsException]
          }
        }
      }

  }

}
