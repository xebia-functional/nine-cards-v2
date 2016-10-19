package cards.nine.services.api.impl

import cards.nine.api._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.Device
import cards.nine.services.api._
import cards.nine.models._
import cards.nine.api.rest.client.http.HttpClientException
import cards.nine.api.rest.client.messages.ServiceClientResponse
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.types.NineCardsCategory
import cats.syntax.either._

import scala.reflect.ClassTag
import scala.util.Random

trait ApiServicesSpecification
  extends Specification
  with Mockito {

  implicit val requestConfigV1 = RequestConfigV1(
    deviceId = Random.nextString(10),
    token = Random.nextString(10),
    marketToken = Option(Random.nextString(10)))

  implicit val requestConfig = RequestConfig(
    apiKey = Random.nextString(10),
    sessionToken = Random.nextString(10),
    androidId = Random.nextString(10),
    marketToken = Some(Random.nextString(10)))

  val apiServicesConfig = ApiServicesConfig(
    appId = Random.nextString(10),
    appKey = Random.nextString(10),
    localization = "EN")

  val serviceHeader = version2.ServiceHeader(
    requestConfig.apiKey,
    requestConfig.sessionToken,
    requestConfig.androidId)

  val serviceMarketHeader = version2.ServiceMarketHeader(
    requestConfig.apiKey,
    requestConfig.sessionToken,
    requestConfig.androidId,
    requestConfig.marketToken)

  val baseUrl = "http://mockedUrl"

  val statusCode = 200

  trait ApiServicesScope
    extends Scope
    with ApiServicesImplData {

    val apiService = mock[cards.nine.api.version2.ApiService]

    val apiServiceV1 = mock[cards.nine.api.version1.ApiService]

    val apiServices = new ApiServicesImpl(
      apiServicesConfig,
      apiService,
      apiServiceV1)

    val exception = HttpClientException("")

    def mustLeft[T <: NineCardException](service: TaskService[_])(implicit classTag: ClassTag[T]): Unit =
      service.value.run must beLike {
        case Left(e) => e must beAnInstanceOf[T]
      }
  }
}

class ApiServicesImplSpec
  extends ApiServicesSpecification {

  "loginV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.login(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version1.User](statusCode, Some(user))))
          }

        val result = apiServices.loginV1(email, Device(name, deviceId, secretToken, permissions)).value.run
        result shouldEqual Right(user)

        there was one(apiServiceV1).login(===(loginV1User), any)(any, any)
      }.pendingUntilFixed("Issue #968")

    "return an ApiServiceV1ConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns ""

        mustLeft[ApiServiceV1ConfigurationException](apiServices.loginV1("", Device("", "", "", Seq.empty)))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.login(any, any)(any, any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.loginV1("", Device("", "", "", Seq.empty)))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.login(any, any)(any, any) returns TaskService {
          Task(Either.left(exception))
        }

        mustLeft[ApiServiceException](apiServices.loginV1("", Device("", "", "", Seq.empty)))
      }

  }

  "getUserConfigV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.getUserConfig(any)(any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version1.UserConfig](statusCode, Some(userConfig))))
          }

        val result = apiServices.getUserConfigV1().value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual userConfig
        }
      }.pendingUntilFixed("Issue #968")

    "return an ApiServiceV1ConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns ""

        mustLeft[ApiServiceV1ConfigurationException](apiServices.getUserConfigV1())
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.getUserConfig(any)(any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.getUserConfigV1())
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiServiceV1.baseUrl returns baseUrl
        apiServiceV1.getUserConfig(any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getUserConfigV1())
      }

  }

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.login(any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.ApiLoginResponse](statusCode, Some(version2.ApiLoginResponse(apiKey, sessionToken)))))
          }

        val result = apiServices.login(email, androidId, tokenId).value.run
        result shouldEqual Right(LoginResponse(apiKey, sessionToken))

        there was one(apiService).login(===(loginRequest))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.login(email, androidId, tokenId))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.login(any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.ApiLoginResponse](statusCode, None)))
          }

        mustLeft[ApiServiceException](apiServices.login(email, androidId, tokenId))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.login(any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.login(email, androidId, tokenId))
      }

  }

  "updateInstallation" should {

    "call to installations with the right request" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.installations(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.InstallationResponse](statusCode, Some(version2.InstallationResponse(androidId, deviceToken)))))
          }

        val result = apiServices.updateInstallation(Some(deviceToken)).value.run

        there was one(apiService).installations(===(installationRequest), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.updateInstallation(Some("")))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.installations(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.InstallationResponse](statusCode, None)))
          }

        mustLeft[ApiServiceException](apiServices.updateInstallation(Some("")))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.installations(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.updateInstallation(Some("")))
      }

  }

  "googlePlayPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse(statusCode, Some(version2.CategorizeResponse(Seq.empty, categorizeApps)))))
          }

        val result = apiServices.googlePlayPackage(categorizeApps.head.packageName).value.run
        result must beLike {
          case Right(response) =>
            Some(response) shouldEqual categorizeApps.headOption.map(a => CategorizedPackage(a.packageName, a.categories.headOption.map(NineCardsCategory(_))))
        }

        there was one(apiService).categorize(===(categorizeOneRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.googlePlayPackage(""))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.googlePlayPackage(""))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.googlePlayPackage(""))
      }

  }

  "googlePlayPackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CategorizeResponse](statusCode, Some(version2.CategorizeResponse(Seq.empty, categorizeApps)))))
          }

        val result = apiServices.googlePlayPackages(categorizeApps.map(_.packageName)).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual (categorizeApps map (a => CategorizedPackage(a.packageName, a.categories.headOption.map(NineCardsCategory(_)))))
        }

        there was one(apiService).categorize(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns a valid response with None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.googlePlayPackages(categorizeApps.map(_.packageName)).value.run
        result must beLike {
          case Right(response) =>
            response must beEmpty
        }

        there was one(apiService).categorize(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.googlePlayPackages(Seq.empty))
      }

    "return an empty sequence when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.googlePlayPackages(categorizeApps.map(_.packageName)).value.run
        result must beLike {
          case Right(response) =>
            response must beEmpty
        }
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.googlePlayPackages(Seq.empty))
      }

  }

  "googlePlayPackagesDetail" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorizeDetail(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CategorizeDetailResponse](
              statusCode, Some(version2.CategorizeDetailResponse(Seq.empty, categorizeAppsDetail)))))
          }

        val result = apiServices.googlePlayPackagesDetail(categorizeApps.map(_.packageName)).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual categorizedDetailPackages
        }

        there was one(apiService).categorizeDetail(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns a valid response with None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorizeDetail(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CategorizeDetailResponse](statusCode, None)))
          }

        val result = apiServices.googlePlayPackagesDetail(categorizeApps.map(_.packageName)).value.run
        result must beLike {
          case Right(response) =>
            response must beEmpty
        }

        there was one(apiService).categorizeDetail(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.googlePlayPackagesDetail(Seq.empty))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorizeDetail(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.googlePlayPackagesDetail(Seq.empty))
      }

  }

  "getRecommendedApps" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendations(any, any, any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.RecommendationsResponse](statusCode, Some(recommendationResponse))))
          }

        val result = apiServices.getRecommendedApps(category, excludedPackages, limit).value.run
        result must beLike {
          case Right(response) =>
            response.map(_.packageName) shouldEqual recommendationApps.map(_.packageName)
        }

        there was one(apiService).recommendations(===(category), any, ===(recommendationsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getRecommendedApps(category, Seq.empty, limit))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendations(any, any, any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getRecommendedApps(category, Seq.empty, limit))
      }

  }

  "getRecommendedAppsByPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(recommendationByAppsResponse)))))

        val result = apiServices.getRecommendedAppsByPackages(packages, excludedPackages, limit).value.run
        result must beLike {
          case Right(response) =>
            response.map(_.packageName) shouldEqual recommendationApps.map(_.packageName)
        }

        there was one(apiService).recommendationsByApps(===(recommendationsByAppsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.getRecommendedAppsByPackages(packages, excludedPackages, limit).value.run
        result must beLike {
          case Right(response) =>
            response must beEmpty
        }

        there was one(apiService).recommendationsByApps(===(recommendationsByAppsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getRecommendedAppsByPackages(packages, Seq.empty, limit))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getRecommendedAppsByPackages(packages, Seq.empty, limit))
      }

  }

  "getSharedCollection" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollection(any, any)(any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(sharedCollection)))))

        val result = apiServices.getSharedCollection(sharedCollectionId).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual sharedCollection
        }

        there was one(apiService).getCollection(===(sharedCollectionId), ===(serviceMarketHeader))(any)
      }.pendingUntilFixed("Issue #968")

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getSharedCollection(sharedCollectionId))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollection(any, any)(any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.getSharedCollection(sharedCollectionId))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollection(any, any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSharedCollection(sharedCollectionId))
      }

  }

  "getSharedCollectionsByCategory" should {

    "return a valid response if the services returns a valid response for TOP apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.topCollections(any, any, any, any)(any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
          }

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit).value.run
        result must beLike {
          case Right(response) =>
            response.size shouldEqual collections.size
        }

        there was one(apiService).topCollections(===(category), ===(offset), ===(limit), ===(serviceMarketHeader))(any)
      }

    "return an ApiServiceException when the service returns an exception for TOP apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.topCollections(any, any, any, any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit))
      }

    "return a valid response if the services returns a valid response for LATEST apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.latestCollections(any, any, any, any)(any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(version2.CollectionsResponse(collections))))))

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit).value.run
        result must beLike {
          case Right(response) =>
            response.size shouldEqual collections.size
        }

        there was one(apiService).latestCollections(===(category), ===(offset), ===(limit), ===(serviceMarketHeader))(any)
      }

    "return an ApiServiceException when the service returns an exception for LATEST apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.latestCollections(any, any, any, any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit))
      }

    "return an ApiServiceException for an invalid collection type" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(category, collectionTypeUnknown, offset, limit))

        there was no(apiService).latestCollections(any, any, any, any)(any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit))
      }

  }

  "createSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CreateCollectionResponse](statusCode, Some(version2.CreateCollectionResponse(sharedCollectionId, packageStats)))))
          }

        val result = apiServices.createSharedCollection(name, author, packages, category, icon, community).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual sharedCollectionId
        }

        there was one(apiService).createCollection(===(createCollectionRequest), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        val result = apiServices.createSharedCollection(name, author, packages, category, icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceConfigurationException, _]]
      }

    "return an ApiServiceException when the service return None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.createSharedCollection(name, author, packages, category, icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]
      }

    "return an ApiServiceException when the service return an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        val result = apiServices.createSharedCollection(name, author, packages, category, icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]
      }

  }

  "updateSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(updateCollectionResponse)))))

        val result = apiServices.updateSharedCollection(sharedCollectionId, Some(name), packages).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual sharedCollectionId
        }

        there was one(apiService).updateCollection(===(sharedCollectionId), ===(updateCollectionRequest), ===(serviceHeader))(any, any)
      }

    "return a valid response if the services return a valid response but don't send a update info when the name is not set" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(updateCollectionResponse)))))

        val result = apiServices.updateSharedCollection(sharedCollectionId, None, packages).value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual sharedCollectionId
        }

        there was one(apiService).updateCollection(===(sharedCollectionId), ===(updateCollectionRequest.copy(collectionInfo = None)), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(name), packages)
        }
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(name), packages)
        }
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(name), packages)
        }
      }

  }

  "getPublishedCollections" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollections(any)(any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse(statusCode, Some(version2.CollectionsResponse(collections)))))
          }

        val result = apiServices.getPublishedCollections().value.run
        result must beLike {
          case Right(response) =>
            response.size shouldEqual collections.size
        }

        there was one(apiService).getCollections(===(serviceMarketHeader))(any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getPublishedCollections())
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollections(any)(any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.getPublishedCollections())
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollections(any)(any) returns TaskService(Task(Left(exception)))

        mustLeft[ApiServiceException](apiServices.getPublishedCollections())
      }

  }

  "getSubscriptions" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getSubscriptions(any)(any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse(statusCode, Some(version2.SubscriptionsResponse(Seq(sharedCollectionId))))))
          }

        val result = apiServices.getSubscriptions().value.run
        result must beLike {
          case Right(response) =>
            response shouldEqual subscriptions.subscriptions
        }

        there was one(apiService).getSubscriptions(===(serviceHeader))(any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getSubscriptions())
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getSubscriptions(any)(any) returns TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException](apiServices.getSubscriptions())
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getSubscriptions(any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSubscriptions())
      }

  }

  "subscribe" should {

    "call to subscribe" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.subscribe(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.subscribe(sharedCollectionId).value.run

        there was one(apiService).subscribe(sharedCollectionId, serviceHeader)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.subscribe(sharedCollectionId))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.subscribe(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.subscribe(sharedCollectionId))
      }

  }

  "unsubscribe" should {

    "call to unsubscribe" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.unsubscribe(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.unsubscribe(sharedCollectionId).value.run

        there was one(apiService).unsubscribe(sharedCollectionId, serviceHeader)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.unsubscribe(sharedCollectionId))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.unsubscribe(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.unsubscribe(sharedCollectionId))
      }

  }

  "rankApps" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.rankApps(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.RankAppsResponse](statusCode, Some(rankAppsResponse))))
          }

        val result = apiServices.rankApps(packagesByCategorySeq, Some(location)).value.run
        result must beLike {
          case Right(response) =>
            response.map(_.category) shouldEqual items.map(_._1)
            response.map(_.packages) shouldEqual items.map(_._2)
        }

        there was one(apiService).rankApps(===(rankAppsRequest), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.rankApps(packagesByCategorySeq, Some(location)))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.rankApps(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.rankApps(packagesByCategorySeq, Some(location)))
      }

  }
}
