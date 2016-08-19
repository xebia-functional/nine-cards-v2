package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.version1.model.{GooglePlayRecommendation => ApiGooglePlayRecommendation, Installation => ApiInstallation, SharedCollection => ApiSharedCollection, User => ApiUser, UserConfig => ApiUserConfig}
import com.fortysevendeg.ninecardslauncher.api._
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models._
import com.fortysevendeg.rest.client.ServiceClientException
import com.fortysevendeg.rest.client.http.HttpClientException
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

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
    extends Scope {

    val apiService = mock[version2.ApiService]

    val apiUserService = mock[version1.services.ApiUserService]

    val googlePlayService = mock[version1.services.ApiGooglePlayService]

    val userConfigService = mock[version1.services.ApiUserConfigService]

    val apiRecommendationService = mock[version1.services.ApiRecommendationService]

    val apiSharedCollectionsService = mock[version1.services.ApiSharedCollectionsService]

    val apiServices = new ApiServicesImpl(
      apiServicesConfig,
      apiService,
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

    apiService.login(any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[version2.LoginResponse](statusCode, Some(version2.LoginResponse(apiKey, sessionToken)))))
      }

    apiService.installations(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[version2.InstallationResponse](statusCode, Some(version2.InstallationResponse(androidId, deviceToken)))))
      }

    apiService.categorize(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[version2.CategorizeResponse](statusCode, Some(version2.CategorizeResponse(Seq.empty, categorizeApps)))))
      }

    apiUserService.login(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[ApiUser](statusCode, Some(user))))
      }

    apiUserService.createInstallation(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[ApiInstallation](statusCode, Some(installation))))
      }

    apiUserService.updateInstallation(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[Unit](statusCode, None)))
      }

    userConfigService.getUserConfig(any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[ApiUserConfig](statusCode, Some(userConfig))))
      }

    apiRecommendationService.getRecommendedApps(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[ApiGooglePlayRecommendation](statusCode, Some(googlePlayRecommendation))))
      }

    apiService.latestCollections(any, any, any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
      }


    apiService.topCollections(any, any, any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[version2.CollectionsResponse](statusCode, Some(version2.CollectionsResponse(collections)))))
      }

    apiSharedCollectionsService.shareCollection(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[ApiSharedCollection](statusCode, Some(sharedCollection))))
     }
  }

  trait ErrorApiServicesImplResponses
    extends ApiServicesImplData
    with Conversions {

    self: ApiServicesScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)
      with ServiceClientException
      with HttpClientException

    val exception = CustomException("")

    apiService.login(any)(any, any) returns Service(Task(Errata(exception)))

    apiService.installations(any, any)(any, any) returns Service(Task(Errata(exception)))

    apiService.categorize(any, any)(any, any) returns Service(Task(Errata(exception)))

    apiService.categorize(any, any)(any, any) returns Service(Task(Errata(exception)))

    apiService.latestCollections(any, any, any, any)(any) returns Service(Task(Errata(exception)))

    apiService.topCollections(any, any, any, any)(any) returns Service(Task(Errata(exception)))

    apiUserService.login(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiUserService.createInstallation(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiUserService.updateInstallation(any, any)(any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.getUserConfig(any)(any) returns Service {
      Task(Errata(exception))
    }

    apiRecommendationService.getRecommendedApps(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiSharedCollectionsService.shareCollection(any, any)(any, any) returns
      Service {
        Task(Errata(exception))
      }
  }

}

class ApiServicesImplSpec
  extends ApiServicesSpecification {

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.login(email, androidId, tokenId).run.run
        result must beLike {
          case Answer(response) =>
            response shouldEqual LoginResponse(apiKey, sessionToken)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.login(email, androidId, tokenId).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "loginV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.loginV1("", GoogleDevice("", "", "", Seq.empty)).run.run
        result must beLike {
          case Answer(response) =>
            response shouldEqual toLoginResponseV1(statusCode, user)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.loginV1("", GoogleDevice("", "", "", Seq.empty)).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "updateInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.updateInstallation(Some("")).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.updateInstallation(Some("")).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "googlePlayPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.googlePlayPackage(categorizeApps.head.packageName).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            Some(response.app) shouldEqual categorizeApps.headOption.map(a => CategorizedPackage(a.packageName, Some(a.category)))
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.googlePlayPackage("").run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "googlePlayPackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.packages shouldEqual (categorizeApps map (a => CategorizedPackage(a.packageName, Some(a.category))))
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "getUserConfigV1" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getUserConfigV1().run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getUserConfigV1().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "getRecommendedApps" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getRecommendedApps(Seq.empty, Seq.empty, Seq.empty, limit).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.seq.map(_.docid) shouldEqual googlePlayApps.map(_.docid)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getRecommendedApps(Seq.empty, Seq.empty, Seq.empty, limit).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "getSharedCollectionsByCategory" should {

    "return a valid response if the services returns a valid response for TOP apps" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.items.size shouldEqual collections.size
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service for TOP apps" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeTop, offset, limit).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

    "return a valid response if the services returns a valid response for LATEST apps" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.items.size shouldEqual collections.size
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service for LATEST apps" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getSharedCollectionsByCategory(category, collectionTypeLatest, offset, limit).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beAnInstanceOf[ApiServiceException]
          }
        }
      }

  }

  "createSharedCollection" should {

    "return a valid response if the services return a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.newSharedCollection shouldEqual CreateSharedCollection(
              name = sharedCollection.name,
              description = sharedCollection.description,
              author = sharedCollection.author,
              packages = sharedCollection.packages,
              category = sharedCollection.category,
              sharedCollectionId = sharedCollection.sharedCollectionId,
              icon = sharedCollection.icon,
              community = sharedCollection.community
            )
        }
      }

    "return an ApiServiceException with the calue the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.createSharedCollection(name, description, author, packages, category, icon, community).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

}
