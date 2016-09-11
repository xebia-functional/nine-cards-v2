package com.fortysevendeg.ninecardslauncher.services.api.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random
import scalaz.concurrent.Task

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
    androidId = Random.nextString(10))

  val apiServicesConfig = ApiServicesConfig(
    appId = Random.nextString(10),
    appKey = Random.nextString(10),
    localization = "EN")

  val statusCode = 200

  trait ApiServicesScope
    extends Scope
    with ApiServicesImplData
    with Conversions {

    val apiService = mock[version2.ApiService]

    val apiServiceV1 = mock[version1.ApiService]

    val apiServices = new ApiServicesImpl(
      apiServicesConfig,
      apiService,
      apiServiceV1)

    val exception = HttpClientException("")
  }

}

class ApiServicesImplSpec
  extends ApiServicesSpecification {

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.login(any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.LoginResponse](statusCode, Some(version2.LoginResponse(apiKey, sessionToken)))))
          }

        val result = apiServices.login(email, androidId, tokenId).value.run
        result shouldEqual Xor.Right(LoginResponse(apiKey, sessionToken))
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.login(any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.login(email, androidId, tokenId).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "loginV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiServiceV1.login(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version1.User](statusCode, Some(user))))
          }

        val result = apiServices.loginV1("", LoginV1Device("", "", "", Seq.empty)).value.run
        result shouldEqual Xor.Right(toLoginResponseV1(statusCode, user))
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiServiceV1.login(any, any)(any, any) returns TaskService {
          Task(Xor.left(exception))
        }

        val result = apiServices.loginV1("", LoginV1Device("", "", "", Seq.empty)).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "updateInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.installations(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.InstallationResponse](statusCode, Some(version2.InstallationResponse(androidId, deviceToken)))))
          }

        val result = apiServices.updateInstallation(Some("")).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.installations(any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.updateInstallation(Some("")).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "googlePlayPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.categorize(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CategorizeResponse](statusCode, Some(version2.CategorizeResponse(Seq.empty, categorizeApps)))))
          }

        val result = apiServices.googlePlayPackage(categorizeApps.head.packageName).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            Some(response.app) shouldEqual categorizeApps.headOption.map(a => CategorizedPackage(a.packageName, Some(a.category)))
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.categorize(any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.googlePlayPackage("").value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "googlePlayPackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.categorize(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CategorizeResponse](statusCode, Some(version2.CategorizeResponse(Seq.empty, categorizeApps)))))
          }

        val result = apiServices.googlePlayPackages(Seq.empty).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.packages shouldEqual (categorizeApps map (a => CategorizedPackage(a.packageName, Some(a.category))))
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.categorize(any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.googlePlayPackages(Seq.empty).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getUserConfigV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiServiceV1.getUserConfig(any)(any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version1.UserConfig](statusCode, Some(userConfig))))
          }

        val result = apiServices.getUserConfigV1().value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiServiceV1.getUserConfig(any)(any) returns TaskService {
          Task(Xor.left(exception))
        }

        val result = apiServices.getUserConfigV1().value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getRecommendedApps" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.recommendations(any, any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.RecommendationsResponse](statusCode, Some(recommendationResponse))))
          }

        val result = apiServices.getRecommendedApps(category, Seq.empty, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.seq.map(_.packageName) shouldEqual recommendationApps.map(_.packageName)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.recommendations(any, any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getRecommendedApps(category, Seq.empty, limit).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getRecommendedAppsByPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.recommendationsByApps(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.RecommendationsByAppsResponse](statusCode, Some(recommendationByAppsResponse))))
          }

        val result = apiServices.getRecommendedAppsByPackages(packages, Seq.empty, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.seq.map(_.packageName) shouldEqual recommendationApps.map(_.packageName)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.recommendationsByApps(any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getRecommendedAppsByPackages(packages, Seq.empty, limit).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getSharedCollection" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope {

        apiService.getCollection(any, any)(any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.Collection](statusCode, Some(sharedCollection))))
          }

        val result = apiServices.getSharedCollection(sharedCollectionId).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.sharedCollection shouldEqual toSharedCollection(sharedCollection)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope {

        apiService.getCollection(any, any)(any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getSharedCollection(sharedCollectionId).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getSharedCollectionsByCategory" should {

    "return a valid response if the services returns a valid response for TOP apps" in
      new ApiServicesScope {

        apiService.topCollections(any, any, any, any)(any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
          }

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.items.size shouldEqual collections.size
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service for TOP apps" in
      new ApiServicesScope {

        apiService.topCollections(any, any, any, any)(any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

    "return a valid response if the services returns a valid response for LATEST apps" in
      new ApiServicesScope {

        apiService.latestCollections(any, any, any, any)(any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
          }

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.items.size shouldEqual collections.size
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service for LATEST apps" in
      new ApiServicesScope {

        apiService.latestCollections(any, any, any, any)(any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit).value.run
        result must beAnInstanceOf[Xor.Left[ApiServiceException]]
      }

  }

  "createSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.createCollection(any, any)(any, any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CreateCollectionResponse](statusCode, Some(version2.CreateCollectionResponse(sharedCollectionId, packageStats)))))
          }

        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.sharedCollectionId shouldEqual sharedCollectionId
        }
      }

    "return an ApiServiceException with the calue the exception returned by the service" in
      new ApiServicesScope {

        apiService.createCollection(any, any)(any, any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

  "getPublishedCollections" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope {

        apiService.getCollections(any)(any) returns
          TaskService {
            Task(Xor.right(ServiceClientResponse[version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
          }

        val result = apiServices.getPublishedCollections().value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.items.size shouldEqual collections.size
        }
      }

    "return an ApiServiceException with the calue the exception returned by the service" in
      new ApiServicesScope {

        apiService.getCollections(any)(any) returns TaskService(Task(Xor.left(exception)))

        val result = apiServices.getPublishedCollections().value.run
        result must beAnInstanceOf[Xor.Left[HttpClientException]]
      }

  }

}
