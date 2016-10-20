package cards.nine.services.api.impl

import cards.nine.api._
import cards.nine.api.rest.client.http.HttpClientException
import cards.nine.api.rest.client.messages.ServiceClientResponse
import cards.nine.api.version2._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.test.data.SharedCollectionValues._
import cards.nine.commons.test.data.UserV1Values._
import cards.nine.commons.test.data.UserValues._
import cards.nine.commons.test.data.{ApiTestData, ApiV1TestData, SharedCollectionTestData}
import cards.nine.models.types.NineCardsCategory
import cards.nine.models.{Device, _}
import cards.nine.services.api._
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.reflect.ClassTag

trait ApiServicesSpecification
  extends Specification
  with Mockito {

  trait ApiServicesScope
    extends Scope
    with ApiServicesImplData
    with ApiTestData
    with ApiV1TestData
    with SharedCollectionTestData {

    val apiServicesConfig = ApiServicesConfig(
      appId = userV1AppId,
      appKey = userV1AppKey,
      localization = userV1Localization)

    val serviceHeader = ServiceHeader(
      requestConfig.apiKey,
      requestConfig.sessionToken,
      requestConfig.androidId)

    val serviceMarketHeader = ServiceMarketHeader(
      requestConfig.apiKey,
      requestConfig.sessionToken,
      requestConfig.androidId,
      requestConfig.marketToken)

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
            Task(Either.right(ServiceClientResponse(statusCode, Option(apiUserV1))))
          }

        val result = apiServices.loginV1(email, device).value.run
        result shouldEqual Right(loginResponseV1)

        there was one(apiServiceV1).login(===(loginV1User), any)(any, any)
      }

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
            Task(Either.right(ServiceClientResponse(statusCode, Some(userConfig))))
          }

        val result = apiServices.getUserConfigV1().value.run
        result shouldEqual Right(userV1)
      }

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
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.ApiLoginResponse](statusCode, Some(version2.ApiLoginResponse(userV1ApiKey, userV1SessionToken)))))
          }

        val result = apiServices.login(email, userV1AndroidId, userV1TokenId).value.run
        result shouldEqual Right(LoginResponse(userV1ApiKey, userV1SessionToken))

        there was one(apiService).login(===(loginRequest))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.login(email, userV1AndroidId, userV1TokenId))
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.login(any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.ApiLoginResponse](statusCode, None)))
          }

        mustLeft[ApiServiceException](apiServices.login(email, userV1AndroidId, userV1TokenId))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.login(any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.login(email, userV1AndroidId, userV1TokenId))
      }

  }

  "updateInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.installations(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.InstallationResponse](statusCode, Some(version2.InstallationResponse(userV1AndroidId, userV1DeviceToken)))))
          }

        val result = apiServices.updateInstallation(Some(userV1DeviceToken)).value.run

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
            Task(Either.right(ServiceClientResponse(statusCode, Some(version2.CategorizeResponse(Seq.empty, seqCategorizedApp)))))
          }

        val result = apiServices.googlePlayPackage(seqCategorizedApp.head.packageName).value.run
        result must beLike {
          case Right(app) =>
            Some(app) shouldEqual seqCategorizedApp.headOption.map(a => CategorizedPackage(a.packageName, a.categories.headOption.map(NineCardsCategory(_))))
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
            Task(Either.right(ServiceClientResponse[cards.nine.api.version2.CategorizeResponse](statusCode, Some(version2.CategorizeResponse(Seq.empty, seqCategorizedApp)))))
          }

        val result = apiServices.googlePlayPackages(seqCategorizedApp.map(_.packageName)).value.run
        result shouldEqual Right(seqCategorizedApp map (a => CategorizedPackage(a.packageName, a.categories.headOption.map(NineCardsCategory(_)))))

        there was one(apiService).categorize(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns a valid response with None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorize(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.googlePlayPackages(seqCategorizedApp.map(_.packageName)).value.run
        result must beLike {
          case Right(packages) =>
            packages must beEmpty
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

        val result = apiServices.googlePlayPackages(seqCategorizedApp.map(_.packageName)).value.run
        result must beLike {
          case Right(packages) =>
            packages must beEmpty
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
            Task(Either.right(ServiceClientResponse(statusCode, Some(CategorizeDetailResponse(Seq.empty, seqCategorizedAppDetail)))))
          }

        val result = apiServices.googlePlayPackagesDetail(seqCategorizedApp.map(_.packageName)).value.run
        result shouldEqual Right(categorizedDetailPackages)

        there was one(apiService).categorizeDetail(===(categorizeRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns a valid response with None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.categorizeDetail(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse[CategorizeDetailResponse](statusCode, None)))
          }

        val result = apiServices.googlePlayPackagesDetail(seqCategorizedApp.map(_.packageName)).value.run
        result must beLike {
          case Right(packages) =>
            packages must beEmpty
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
            Task(Either.right(ServiceClientResponse(statusCode, Some(recommendationsResponse))))
          }

        val result = apiServices.getRecommendedApps(userV1Category, excludedPackages, userV1Limit).value.run
        result must beLike {
          case Right(recommendedApps) =>
            recommendedApps.map(_.packageName) shouldEqual seqRecommendationApp.map(_.packageName)
        }

        there was one(apiService).recommendations(===(userV1Category), any, ===(recommendationsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getRecommendedApps(userV1Category, Seq.empty, userV1Limit))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendations(any, any, any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getRecommendedApps(userV1Category, Seq.empty, userV1Limit))
      }

  }

  "getRecommendedAppsByPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(recommendationByAppsResponse)))))

        val result = apiServices.getRecommendedAppsByPackages(userV1Packages, excludedPackages, userV1Limit).value.run
        result must beLike {
          case Right(recommendedApps) =>
            recommendedApps.map(_.packageName) shouldEqual seqRecommendationApp.map(_.packageName)
        }

        there was one(apiService).recommendationsByApps(===(recommendationsByAppsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an empty sequence if the services returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.getRecommendedAppsByPackages(userV1Packages, excludedPackages, userV1Limit).value.run
        result must beLike {
          case Right(recommendedApps) =>
            recommendedApps must beEmpty
        }

        there was one(apiService).recommendationsByApps(===(recommendationsByAppsRequest), ===(serviceMarketHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getRecommendedAppsByPackages(userV1Packages, Seq.empty, userV1Limit))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.recommendationsByApps(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getRecommendedAppsByPackages(userV1Packages, Seq.empty, userV1Limit))
      }

  }

  "getSharedCollection" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollection(any, any)(any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Option(collectionV2)))))

        val result = apiServices.getSharedCollection(sharedCollectionId).value.run
        result shouldEqual Right(sharedCollection)

        there was one(apiService).getCollection(===(sharedCollectionId), ===(serviceMarketHeader))(any)
      }

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
            Task(Either.right(ServiceClientResponse(statusCode, Some(collectionsResponse))))
          }

        val result = apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeTop, offset, userV1Limit).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual seqCollection.size
            shareCollections map (s => Option(s.sharedCollectionId)) shouldEqual (seqCollection map (_.sharedCollectionId))
        }

        there was one(apiService).topCollections(===(userV1Category), ===(offset), ===(userV1Limit), ===(serviceMarketHeader))(any)
      }

    "return an ApiServiceException when the service returns an exception for TOP apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.topCollections(any, any, any, any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeTop, offset, userV1Limit))
      }

    "return a valid response if the services returns a valid response for LATEST apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.latestCollections(any, any, any, any)(any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(collectionsResponse)))))

        val result = apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeLatest, offset, userV1Limit).value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual seqCollection.size
            shareCollections map (s => Option(s.sharedCollectionId)) shouldEqual (seqCollection map (_.sharedCollectionId))
        }

        there was one(apiService).latestCollections(===(userV1Category), ===(offset), ===(userV1Limit), ===(serviceMarketHeader))(any)
      }

    "return an ApiServiceException when the service returns an exception for LATEST apps" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.latestCollections(any, any, any, any)(any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeLatest, offset, userV1Limit))
      }

    "return an ApiServiceException for an invalid collection type" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl

        mustLeft[ApiServiceException](apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeUnknown, offset, userV1Limit))

        there was no(apiService).latestCollections(any, any, any, any)(any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.getSharedCollectionsByCategory(userV1Category, collectionTypeLatest, offset, userV1Limit))
      }

  }

  "createSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse(statusCode, Some(CreateCollectionResponse(sharedCollectionId, packageStats)))))
          }

        val result = apiServices.createSharedCollection(sharedCollectionName, author, userV1Packages, userV1Category, userV1Icon, community).value.run
        result shouldEqual Right(sharedCollectionId)

        there was one(apiService).createCollection(===(createCollectionRequest), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        val result = apiServices.createSharedCollection(sharedCollectionName, author, userV1Packages, userV1Category, userV1Icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceConfigurationException, _]]
      }

    "return an ApiServiceException when the service return None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        val result = apiServices.createSharedCollection(sharedCollectionName, author, userV1Packages, userV1Category, userV1Icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]
      }

    "return an ApiServiceException when the service return an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.createCollection(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        val result = apiServices.createSharedCollection(sharedCollectionName, author, userV1Packages, userV1Category, userV1Icon, community).value.run
        result must beAnInstanceOf[Left[ApiServiceException,  _]]
      }

  }

  "updateSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(updateCollectionResponse)))))

        val result = apiServices.updateSharedCollection(sharedCollectionId, Some(sharedCollectionName), userV1Packages).value.run
        result shouldEqual Right(sharedCollectionId)

        there was one(apiService).updateCollection(===(sharedCollectionId), ===(updateCollectionRequest), ===(serviceHeader))(any, any)
      }

    "return a valid response if the services return a valid response but don't send a update info when the name is not set" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, Some(updateCollectionResponse)))))

        val result = apiServices.updateSharedCollection(sharedCollectionId, None, userV1Packages).value.run
        result shouldEqual Right(sharedCollectionId)

        there was one(apiService).updateCollection(===(sharedCollectionId), ===(updateCollectionRequest.copy(collectionInfo = None)), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(sharedCollectionName), userV1Packages)
        }
      }

    "return an ApiServiceException when the service returns None" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns
          TaskService(Task(Either.right(ServiceClientResponse(statusCode, None))))

        mustLeft[ApiServiceException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(sharedCollectionName), userV1Packages)
        }
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.updateCollection(any, any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException] {
          apiServices.updateSharedCollection(sharedCollectionId, Some(sharedCollectionName), userV1Packages)
        }
      }

  }

  "getPublishedCollections" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.getCollections(any)(any) returns
          TaskService {
            Task(Either.right(ServiceClientResponse(statusCode, Some(collectionsResponse))))
          }

        val result = apiServices.getPublishedCollections().value.run
        result must beLike {
          case Right(shareCollections) =>
            shareCollections.size shouldEqual seqCollection.size
            shareCollections map (s => Option(s.sharedCollectionId)) shouldEqual (seqCollection map (_.sharedCollectionId))
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
            Task(Either.right(ServiceClientResponse(statusCode, Some(SubscriptionsResponse(Seq(sharedCollectionId))))))
          }

        val result = apiServices.getSubscriptions().value.run
        result shouldEqual Right(seqSubscription)

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

    "return a valid response if the services returns a valid response" in
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

    "return a valid response if the services returns a valid response" in
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
            Task(Either.right(ServiceClientResponse(statusCode, Some(rankAppsResponse))))
          }

        val result = apiServices.rankApps(seqPackagesByCategory, Some(userV1Localization)).value.run
        result must beLike {
          case Right(response) =>
            response.map(_.category) shouldEqual rankAppMap.keys
            response.map(_.packages) shouldEqual rankAppMap.values
        }

        there was one(apiService).rankApps(===(rankAppsRequest), ===(serviceHeader))(any, any)
      }

    "return an ApiServiceConfigurationException when the base url is empty" in
      new ApiServicesScope {

        apiService.baseUrl returns ""

        mustLeft[ApiServiceConfigurationException](apiServices.rankApps(seqPackagesByCategory, Some(userV1Localization)))
      }

    "return an ApiServiceException when the service returns an exception" in
      new ApiServicesScope {

        apiService.baseUrl returns baseUrl
        apiService.rankApps(any, any)(any, any) returns TaskService(Task(Either.left(exception)))

        mustLeft[ApiServiceException](apiServices.rankApps(seqPackagesByCategory, Some(userV1Localization)))
      }

  }
}
