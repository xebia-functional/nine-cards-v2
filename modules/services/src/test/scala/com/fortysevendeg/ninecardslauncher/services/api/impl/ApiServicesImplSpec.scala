package com.fortysevendeg.ninecardslauncher.services.api.impl

import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.api.version1.model.{GooglePlayPackage => ApiGooglePlayPackage, GooglePlayPackages => ApiGooglePlayPackages, GooglePlayRecommendation => ApiGooglePlayRecommendation, Installation => ApiInstallation, SharedCollection => ApiSharedCollection, SharedCollectionList => ApiSharedCollectionList, User => ApiUser, UserConfig => ApiUserConfig}
import com.fortysevendeg.ninecardslauncher.api.version1.services._
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.ServiceClientException
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

  implicit val requestConfig = RequestConfig(
    deviceId = Random.nextString(10),
    token = Random.nextString(10),
    marketToken = Option(Random.nextString(10)))

  val apiServicesConfig = ApiServicesConfig(
    appId = Random.nextString(10),
    appKey = Random.nextString(10),
    localization = "EN")

  val statusCode = 200

  trait ApiServicesScope
    extends Scope {

    val apiUserService = mock[ApiUserService]

    val googlePlayService = mock[ApiGooglePlayService]

    val userConfigService = mock[ApiUserConfigService]

    val apiRecommendationService = mock[ApiRecommendationService]

    val apiSharedCollectionsService = mock[ApiSharedCollectionsService]

    val apiServices = new ApiServicesImpl(
      apiServicesConfig,
      apiUserService,
      googlePlayService,
      userConfigService,
      apiRecommendationService,
      apiSharedCollectionsService)
  }

  trait ValidApiServicesImplResponses
    extends ApiServicesImplData
      with Conversions {

    self: ApiServicesScope =>

    apiUserService.login(any, any)(any, any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiUser](statusCode, Some(user))))
      }

    apiUserService.createInstallation(any, any)(any, any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiInstallation](statusCode, Some(installation))))
      }

    apiUserService.updateInstallation(any, any)(any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[Unit](statusCode, None)))
      }

    googlePlayService.getGooglePlayPackage(any, any)(any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiGooglePlayPackage](statusCode, googlePlayPackages.items.headOption)))
      }

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiGooglePlayPackages](statusCode, Some(googlePlayPackages))))
      }

    userConfigService.getUserConfig(any)(any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiUserConfig](statusCode, Some(userConfig))))
      }

    apiRecommendationService.getRecommendedApps(any, any)(any, any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiGooglePlayRecommendation](statusCode, Some(googlePlayRecommendation))))
      }

    apiSharedCollectionsService.getSharedCollectionListByCategory(any, any, any, any, any)(any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiSharedCollectionList](statusCode, Some(sharedCollectionList))))
      }

    apiSharedCollectionsService.shareCollection(any, any)(any, any) returns
      CatsService {
        Task(Xor.Right(ServiceClientResponse[ApiSharedCollection](statusCode, Some(sharedCollection))))
      }
  }

  trait ErrorApiServicesImplResponses
    extends ApiServicesImplData
      with Conversions {

    self: ApiServicesScope =>

    val exception = HttpClientException("")

    apiUserService.login(any, any)(any, any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    apiUserService.createInstallation(any, any)(any, any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    apiUserService.updateInstallation(any, any)(any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    googlePlayService.getGooglePlayPackage(any, any)(any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    userConfigService.getUserConfig(any)(any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    apiRecommendationService.getRecommendedApps(any, any)(any, any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    apiSharedCollectionsService.getSharedCollectionListByCategory(any, any, any, any, any)(any) returns
      CatsService {
        Task(Xor.Left(exception))
      }

    apiSharedCollectionsService.shareCollection(any, any)(any, any) returns
      CatsService {
        Task(Xor.Left(exception))
      }
  }

}

class ApiServicesImplSpec
  extends ApiServicesSpecification {

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.login("", GoogleDevice("", "", "", Seq.empty)).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.user shouldEqual toUser(user)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.login("", GoogleDevice("", "", "", Seq.empty)).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "createInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.createInstallation(Some(AndroidDevice), Some(""), Some("")).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.installation shouldEqual toInstallation(installation)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.createInstallation(Some(AndroidDevice), Some(""), Some("")).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "updateInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.updateInstallation("", Some(AndroidDevice), Some(""), Some("")).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.updateInstallation("", Some(AndroidDevice), Some(""), Some("")).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "googlePlayPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.googlePlayPackage("").value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            Some(response.app) shouldEqual googlePlayPackages.items.headOption.map(a => toGooglePlayApp(a.docV2))
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.googlePlayPackage("").value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "googlePlayPackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.packages shouldEqual (googlePlayPackages.items map toGooglePlayPackage)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "getUserConfig" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getUserConfig().value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getUserConfig().value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "getRecommendedApps" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getRecommendedApps(Seq.empty, Seq.empty, Seq.empty, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.seq.map(_.docid) shouldEqual googlePlayApps.map(_.docid)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getRecommendedApps(Seq.empty, Seq.empty, Seq.empty, limit).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "getSharedCollectionsByCategory" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionType, offset, limit).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.items shouldEqual response.items
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionType, offset, limit).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

  "createSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.statusCode shouldEqual statusCode
            response.newSharedCollection shouldEqual CreateSharedCollection(
              name = sharedCollection.name,
              description = sharedCollection.description,
              author = sharedCollection.author,
              packages = sharedCollection.packages,
              category = sharedCollection.category,
              shareLink = sharedCollection.shareLink,
              sharedCollectionId = sharedCollection.sharedCollectionId,
              icon = sharedCollection.icon,
              community = sharedCollection.community
            )
        }
      }

    "return an ApiServiceException with the calue the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).value.run
        result must beLike {
          case Xor.Left(e) => e.cause must beSome.which(_ shouldEqual exception)
        }
      }

  }

}
