package cards.nine.process.recommendations.impl

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.ApiTestData
import cards.nine.commons.test.data.ApiValues._
import cards.nine.commons.test.data.CommonValues._
import cards.nine.process.recommendations.{RecommendedAppsConfigurationException, RecommendedAppsException}
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api.{ApiServiceConfigurationException, ApiServiceException, ApiServices}
import cards.nine.services.persistence.PersistenceServices
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.reflect.ClassTag

trait RecommendationsProcessSpecification
  extends Specification
  with Mockito
  with ApiTestData {

  val apiException = ApiServiceException("")
  val apiConfigException = ApiServiceConfigurationException("")

  trait RecommendationsProcessScope
    extends Scope {

    val contextSupport = mock[ContextSupport]

    val apiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val process = new RecommendationsProcessImpl(apiServices, mockPersistenceServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        TaskService(Task(Either.right(requestConfig)))
    }

    def mustLeft[T <: NineCardException](service: TaskService[_])(implicit classTag: ClassTag[T]): Unit =
      service.value.run must beLike {
        case Left(e) => e must beAnInstanceOf[T]
      }

  }

}

class RecommendationsProcessSpec
  extends RecommendationsProcessSpecification {

  "getRecommendedApps" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
          TaskService(Task(Either.right(seqRecommendedApp)))

        val result = process.getRecommendedAppsByCategory(category)(contextSupport).value.run

        there was one(apiServices).getRecommendedApps(category.name, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual seqRecommendedApp.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if service returns an exception" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[RecommendedAppsException](process.getRecommendedAppsByCategory(category)(contextSupport))
      }

    "returns a RecommendedAppsConfigurationException if service returns a config exception" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedApps(any, any, any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[RecommendedAppsConfigurationException](process.getRecommendedAppsByCategory(category)(contextSupport))
      }

  }

  "getRecommendedAppsByPackages" should {

    "return an equivalent sequence to the returned by the Service" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
          TaskService(Task(Either.right(seqRecommendedApp)))

        val result = process.getRecommendedAppsByPackages(likePackages)(contextSupport).value.run

        there was one(apiServices).getRecommendedAppsByPackages(likePackages, Seq.empty, limit)(requestConfig)

        result must beLike {
          case Right(response) =>
            response.seq.map(_.packageName).toSet shouldEqual seqRecommendedApp.map(_.packageName).toSet
        }

      }

    "returns a RecommendedAppsException if service returns an exception" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
          TaskService(Task(Either.left(apiException)))

        mustLeft[RecommendedAppsException](process.getRecommendedAppsByPackages(likePackages)(contextSupport))
      }

    "returns a RecommendedAppsConfigurationException if service returns an exception" in
      new RecommendationsProcessScope {

        apiServices.getRecommendedAppsByPackages(any, any, any)(any) returns
          TaskService(Task(Either.left(apiConfigException)))

        mustLeft[RecommendedAppsConfigurationException](process.getRecommendedAppsByPackages(likePackages)(contextSupport))
      }

  }

}
