package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiUserConfigService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.api.{model => apiModel}
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.api.models.AndroidDevice
import com.fortysevendeg.ninecardslauncher.services.api.{models => serviceModel, _}
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

  implicit val requestConfig = RequestConfig(
    deviceId = Random.nextString(10),
    token = Random.nextString(10))

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

    val apiServices = new ApiServicesImpl(apiServicesConfig, apiUserService, googlePlayService, userConfigService)

  }

  trait ValidApiServicesImplResponses
    extends ApiServicesImplData
    with Conversions {

    self: ApiServicesScope =>

    val user = generateUser

    val installation = generateInstallation

    val googlePlayPackages = generateGooglePlayPackages

    val googlePlaySimplePackages = generateGooglePlaySimplePackages

    val userConfig = generateUserConfig

    apiUserService.login(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.User](statusCode, Some(user))))
      }

    apiUserService.linkAuthData(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.User](statusCode, Some(user))))
      }

    apiUserService.createInstallation(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.Installation](statusCode, Some(installation))))
      }

    apiUserService.updateInstallation(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[Unit](statusCode, None)))
      }

    googlePlayService.getGooglePlayPackage(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.GooglePlayPackage](statusCode, googlePlayPackages.items.headOption)))
      }

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.GooglePlayPackages](statusCode, Some(googlePlayPackages))))
      }

    googlePlayService.getGooglePlaySimplePackages(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.GooglePlaySimplePackages](statusCode, Some(googlePlaySimplePackages))))
      }

    userConfigService.getUserConfig(any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.saveDevice(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.saveGeoInfo(any, any)(any, any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.checkpointPurchaseProduct(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.checkpointCustomCollection(any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.checkpointJoinedBy(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
      }

    userConfigService.tester(any, any)(any) returns
      Service {
        Task(Answer(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))
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

    apiUserService.login(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiUserService.linkAuthData(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiUserService.createInstallation(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    apiUserService.updateInstallation(any, any)(any) returns Service {
      Task(Errata(exception))
    }

    googlePlayService.getGooglePlayPackage(any, any)(any) returns Service {
      Task(Errata(exception))
    }

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    googlePlayService.getGooglePlaySimplePackages(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.getUserConfig(any)(any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.saveDevice(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.saveGeoInfo(any, any)(any, any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.checkpointPurchaseProduct(any, any)(any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.checkpointCustomCollection(any)(any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.checkpointJoinedBy(any, any)(any) returns Service {
      Task(Errata(exception))
    }

    userConfigService.tester(any, any)(any) returns Service {
      Task(Errata(exception))
    }
  }

}

class ApiServicesImplSpec
  extends ApiServicesSpecification {

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.login("", serviceModel.GoogleDevice("", "", "", Seq.empty)).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.user shouldEqual toUser(user)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.login("", serviceModel.GoogleDevice("", "", "", Seq.empty)).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "linkGoogleAccount" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.linkGoogleAccount("", Seq(serviceModel.GoogleDevice("", "", "", Seq.empty))).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.user shouldEqual toUser(user)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.linkGoogleAccount("", Seq(serviceModel.GoogleDevice("", "", "", Seq.empty))).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "createInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.createInstallation(Some(AndroidDevice), Some(""), Some("")).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.installation shouldEqual toInstallation(installation)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.createInstallation(Some(AndroidDevice), Some(""), Some("")).run.run
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
        val result = apiServices.updateInstallation("", Some(AndroidDevice), Some(""), Some("")).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.updateInstallation("", Some(AndroidDevice), Some(""), Some("")).run.run
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
        val result = apiServices.googlePlayPackage("").run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.app shouldEqual toGooglePlayApp(googlePlayPackages.items.head.docV2)
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
            response.packages shouldEqual (googlePlayPackages.items map toGooglePlayPackage)
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

  "googlePlaySimplePackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.googlePlaySimplePackages(Seq.empty).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.apps shouldEqual toGooglePlaySimplePackages(googlePlaySimplePackages)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.googlePlaySimplePackages(Seq.empty).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "getUserConfig" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.getUserConfig().run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.getUserConfig().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "saveDevice" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.saveDevice(serviceModel.UserConfigDevice("", "", Seq.empty)).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.saveDevice(serviceModel.UserConfigDevice("", "", Seq.empty)).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "saveGeoInfo" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.saveGeoInfo(serviceModel.UserConfigGeoInfo(None, None, None, None)).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.saveGeoInfo(serviceModel.UserConfigGeoInfo(None, None, None, None)).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "checkpointPurchaseProduct" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.checkpointPurchaseProduct("").run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.checkpointPurchaseProduct("").run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "checkpointCustomCollection" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.checkpointCustomCollection().run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.checkpointCustomCollection().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "checkpointJoinedBy" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.checkpointJoinedBy("").run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.checkpointJoinedBy("").run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, apiException)) => apiException must beLike {
              case e: ApiServiceException => e.cause must beSome.which(_ shouldEqual exception)
            }
          }
        }
      }

  }

  "tester" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesImplResponses {
        val result = apiServices.tester(Map.empty).run.run
        result must beLike {
          case Answer(response) =>
            response.statusCode shouldEqual statusCode
            response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return an ApiServiceException with the cause the exception returned by the service" in
      new ApiServicesScope with ErrorApiServicesImplResponses {
        val result = apiServices.tester(Map.empty).run.run
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
