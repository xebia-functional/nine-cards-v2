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
      apiUtils.getRequestConfig(contextSupport) returns
        CatsService(Task(Xor.right(requestConfig)))
    }

  }

  trait SuccessCategoriesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    val googlePlayApps = generateGooglePlayApps()

    apiServices.getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig) returns
      CatsService(Task(Xor.right(RecommendationResponse(statusCodeOk, googlePlayApps))))

  }

  trait ErrorCategoriesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    apiServices.getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig) returns
      CatsService(Task(Xor.left(apiException)))

  }

  trait SuccessLikePackagesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    val googlePlayApps = generateGooglePlayApps()

    apiServices.getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig) returns
      CatsService(Task(Xor.right(RecommendationResponse(statusCodeOk, googlePlayApps))))

  }

  trait ErrorLikePackagesRecommendationsProcessScope
    extends RecommendationsProcessScope {

    apiServices.getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig) returns
      CatsService(Task(Xor.left(apiException)))

  }

}

class RecommendationsProcessSpec
  extends RecommendationsProcessSpecification {

  "getRecommendedAppsByCategory" should {

    "return an equivalent sequence to the returned by the Service" in
      new SuccessCategoriesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).value.run

        there was one(apiServices).getRecommendedApps(categories, Seq.empty, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Xor.Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual googlePlayApps.map(_.docid).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new ErrorCategoriesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[RecommendedAppsException]
          }
      }

  }

  "getRecommendedAppsByPackages" should {

    "return an equivalent sequence to the returned by the Service" in
      new SuccessLikePackagesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).value.run

        there was one(apiServices).getRecommendedApps(Seq.empty, likePackages, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Xor.Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual googlePlayApps.map(_.docid).toSet
        }

      }

    "returns a RecommendedAppsException if app service fails" in
      new ErrorLikePackagesRecommendationsProcessScope {

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).value.run

        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[RecommendedAppsException]
          }
      }

  }

}
