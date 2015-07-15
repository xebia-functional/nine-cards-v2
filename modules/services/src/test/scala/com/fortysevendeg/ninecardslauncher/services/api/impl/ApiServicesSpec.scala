package com.fortysevendeg.ninecardslauncher.services.api.impl

import com.fortysevendeg.ninecardslauncher.api.{model => apiModel}
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.api.{models => serviceModel, _}
import com.fortysevendeg.ninecardslauncher.api.services.{ApiUserConfigService, ApiGooglePlayService, ApiUserService}
import com.fortysevendeg.rest.client.messages.ServiceClientResponse
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Random
import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

trait ApiServicesSpecification
  extends Specification
  with DisjunctionMatchers
  with Mockito {

  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._

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

  trait ValidApiServicesResponses
    extends ApiServicesData
    with Conversions {

    self: ApiServicesScope =>

    val user = generateUser

    val installation = generateInstallation

    val googlePlayPackages = generateGooglePlayPackages

    val googlePlaySimplePackages = generateGooglePlaySimplePackages

    val userConfig = generateUserConfig

    apiUserService.login(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.User](statusCode, Some(user))))

    apiUserService.linkAuthData(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.User](statusCode, Some(user))))

    apiUserService.createInstallation(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.Installation](statusCode, Some(installation))))

    apiUserService.updateInstallation(any, any)(any) returns
      Task(\/-(ServiceClientResponse[Unit](statusCode, None)))

    googlePlayService.getGooglePlayPackage(any, any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.GooglePlayPackage](statusCode, googlePlayPackages.items.headOption)))

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.GooglePlayPackages](statusCode, Some(googlePlayPackages))))

    googlePlayService.getGooglePlaySimplePackages(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.GooglePlaySimplePackages](statusCode, Some(googlePlaySimplePackages))))

    userConfigService.getUserConfig(any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.saveDevice(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.saveGeoInfo(any, any)(any, any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.checkpointPurchaseProduct(any, any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.checkpointCustomCollection(any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.checkpointJoinedBy(any, any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

    userConfigService.tester(any, any)(any) returns
      Task(\/-(ServiceClientResponse[apiModel.UserConfig](statusCode, Some(userConfig))))

  }

  trait ErrorApiServicesResponses
    extends ApiServicesData
    with Conversions {

    self: ApiServicesScope =>

    val exception = NineCardsException("")

    apiUserService.login(any, any)(any, any) returns Task(-\/(exception))

    apiUserService.linkAuthData(any, any)(any, any) returns Task(-\/(exception))

    apiUserService.createInstallation(any, any)(any, any) returns Task(-\/(exception))

    apiUserService.updateInstallation(any, any)(any) returns Task(-\/(exception))

    googlePlayService.getGooglePlayPackage(any, any)(any) returns Task(-\/(exception))

    googlePlayService.getGooglePlayPackages(any, any)(any, any) returns Task(-\/(exception))

    googlePlayService.getGooglePlaySimplePackages(any, any)(any, any) returns Task(-\/(exception))

    userConfigService.getUserConfig(any)(any) returns Task(-\/(exception))

    userConfigService.saveDevice(any, any)(any, any) returns Task(-\/(exception))

    userConfigService.saveGeoInfo(any, any)(any, any) returns Task(-\/(exception))

    userConfigService.checkpointPurchaseProduct(any, any)(any) returns Task(-\/(exception))

    userConfigService.checkpointCustomCollection(any)(any) returns Task(-\/(exception))

    userConfigService.checkpointJoinedBy(any, any)(any) returns Task(-\/(exception))

    userConfigService.tester(any, any)(any) returns Task(-\/(exception))
  }

}

class ApiServicesSpec
  extends ApiServicesSpecification {

  "login" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.login("", serviceModel.GoogleDevice("", "", "", Seq.empty)).run
        result must be_\/-[LoginResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.user shouldEqual toUser(user)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.login("", serviceModel.GoogleDevice("", "", "", Seq.empty)).run
        result must be_-\/[NineCardsException]
      }

  }

  "linkGoogleAccount" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.linkGoogleAccount("", Seq(serviceModel.GoogleDevice("", "", "", Seq.empty))).run
        result must be_\/-[LoginResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.user shouldEqual toUser(user)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.linkGoogleAccount("", Seq(serviceModel.GoogleDevice("", "", "", Seq.empty))).run
        result must be_-\/[NineCardsException]
      }

  }

  "createInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.createInstallation(Some(""), Some(""), Some(""), Some("")).run
        result must be_\/-[InstallationResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.installation shouldEqual toInstallation(installation)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.createInstallation(Some(""), Some(""), Some(""), Some("")).run
        result must be_-\/[NineCardsException]
      }

  }

  "updateInstallation" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.updateInstallation(Some(""), Some(""), Some(""), Some("")).run
        result must be_\/-[UpdateInstallationResponse].which { response =>
          response.statusCode shouldEqual statusCode
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.updateInstallation(Some(""), Some(""), Some(""), Some("")).run
        result must be_-\/[NineCardsException]
      }

  }

  "googlePlayPackage" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.googlePlayPackage("").run
        result must be_\/-[GooglePlayPackageResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.app shouldEqual toGooglePlayApp(googlePlayPackages.items.head.docV2)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.googlePlayPackage("").run
        result must be_-\/[NineCardsException]
      }

  }

  "googlePlayPackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).run
        result must be_\/-[GooglePlayPackagesResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.packages shouldEqual (googlePlayPackages.items map toGooglePlayPackage)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.googlePlayPackages(Seq.empty).run
        result must be_-\/[NineCardsException]
      }

  }

  "googlePlaySimplePackages" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.googlePlaySimplePackages(Seq.empty).run
        result must be_\/-[GooglePlaySimplePackagesResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.apps shouldEqual toGooglePlaySimplePackages(googlePlaySimplePackages)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.googlePlaySimplePackages(Seq.empty).run
        result must be_-\/[NineCardsException]
      }

  }

  "getUserConfig" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.getUserConfig().run
        result must be_\/-[GetUserConfigResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.getUserConfig().run
        result must be_-\/[NineCardsException]
      }

  }

  "saveDevice" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.saveDevice(serviceModel.UserConfigDevice("", "", Seq.empty)).run
        result must be_\/-[SaveDeviceResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.saveDevice(serviceModel.UserConfigDevice("", "", Seq.empty)).run
        result must be_-\/[NineCardsException]
      }

  }

  "saveGeoInfo" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.saveGeoInfo(serviceModel.UserConfigGeoInfo(None, None, None, None)).run
        result must be_\/-[SaveGeoInfoResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.saveGeoInfo(serviceModel.UserConfigGeoInfo(None, None, None, None)).run
        result must be_-\/[NineCardsException]
      }

  }

  "checkpointPurchaseProduct" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.checkpointPurchaseProduct("").run
        result must be_\/-[CheckpointPurchaseProductResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.checkpointPurchaseProduct("").run
        result must be_-\/[NineCardsException]
      }

  }

  "checkpointCustomCollection" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.checkpointCustomCollection().run
        result must be_\/-[CheckpointCustomCollectionResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.checkpointCustomCollection().run
        result must be_-\/[NineCardsException]
      }

  }

  "checkpointJoinedBy" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.checkpointJoinedBy("").run
        result must be_\/-[CheckpointJoinedByResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.checkpointJoinedBy("").run
        result must be_-\/[NineCardsException]
      }

  }

  "tester" should {

    "return a valid response if the services returns a valid response" in
      new ApiServicesScope with ValidApiServicesResponses {
        val result = apiServices.tester(Map.empty).run
        result must be_\/-[TesterResponse].which { response =>
          response.statusCode shouldEqual statusCode
          response.userConfig shouldEqual toUserConfig(userConfig)
        }
      }

    "return a NineCardsException if the services returns a exception" in
      new ApiServicesScope with ErrorApiServicesResponses {
        val result = apiServices.tester(Map.empty).run
        result must be_-\/[NineCardsException]
      }

  }

}
