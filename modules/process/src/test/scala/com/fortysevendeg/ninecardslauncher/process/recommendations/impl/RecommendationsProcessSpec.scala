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
  with Mockito {

  val apiException = new ApiServiceException("")

  trait RecommendationsProcessScope
    extends Scope
    with RecommendationsProcessData {

    val contextSupport = mock[ContextSupport]

    val apiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val process = new RecommendationsProcessImpl(apiServices, mockPersistenceServices) {
      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfigV1(contextSupport) returns
        Service(Task(Result.answer(requestConfig)))
    }

  }

  trait SuccessCategoriesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    val googlePlayApps = generateGooglePlayApps()

    apiServices.getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig) returns
      Service(Task(Result.answer(RecommendationResponse(statusCodeOk, googlePlayApps))))

  }

  trait ErrorCategoriesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    apiServices.getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig) returns
      Service(Task(Errata(apiException)))

  }

  trait SuccessLikePackagesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    val googlePlayApps = generateGooglePlayApps()

    apiServices.getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig) returns
      Service(Task(Result.answer(RecommendationResponse(statusCodeOk, googlePlayApps))))

  }

  trait ErrorLikePackagesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    apiServices.getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig) returns
      Service(Task(Errata(apiException)))

  }

}

class RecommendationsProcessSpec
  extends RecommendationsProcessSpecification {

  "getRecommendedAppsByCategory" should {

    "return an equivalent sequence to the returned by the Service" in
      new SuccessCategoriesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).run.run

        there was one(apiServices).getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Answer(response) =>
            response.seq.map(_.packageName).toSet shouldEqual googlePlayApps.map(_.docid).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new ErrorCategoriesRecommendationsProcessScope {

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
      new SuccessLikePackagesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).run.run

        there was one(apiServices).getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Answer(response) =>
            response.seq.map(_.packageName).toSet shouldEqual googlePlayApps.map(_.docid).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new ErrorLikePackagesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).run.run

        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[RecommendedAppsException]
          }
        }
      }

  }

}
