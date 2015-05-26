package com.fortysevendeg.ninecardslauncher.modules.api.impl

import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.api.model.PackagesRequest
import com.fortysevendeg.ninecardslauncher.api.services.{ApiGooglePlayService, ApiUserConfigService, ApiUserService}
import com.fortysevendeg.ninecardslauncher.di.Module
import com.fortysevendeg.ninecardslauncher.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.modules.api._
import com.fortysevendeg.ninecardslauncher.modules.repository.RepositoryServices
import com.fortysevendeg.ninecardslauncher.modules.user.UserNotFoundException
import com.fortysevendeg.ninecardslauncher2.R

import scala.concurrent.{Future, ExecutionContext}

class ApiServicesImpl(
    resources: Resources,
    repositoryServices: RepositoryServices,
    apiUserService: ApiUserService,
    googlePlayService: ApiGooglePlayService,
    userConfigService: ApiUserConfigService)
    extends ApiServices
    with Module
    with Conversions {

  implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val HeaderAppId = "X-Appsly-Application-Id"

  val HeaderAppKey = "X-Appsly-REST-API-Key"

  val HeaderDevice = "X-Android-ID"

  val HeaderToken = "X-Appsly-Session-Token"

  val HeaderLocalization = "X-Android-Market-Localization"

  val baseHeader: Seq[(String, String)] = Seq(
    (HeaderAppId, resources.getString(R.string.api_app_id)),
    (HeaderAppKey, resources.getString(R.string.api_app_key)),
    (HeaderLocalization, resources.getString(R.string.api_localization)))

  import com.fortysevendeg.ninecardslauncher.api.reads.GooglePlayImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserConfigImplicits._
  import com.fortysevendeg.ninecardslauncher.api.reads.UserImplicits._
  
  override def login(request: LoginRequest)(implicit executionContext: ExecutionContext): Future[LoginResponse] =
    for {
      response <- apiUserService.login(fromLoginRequest(request), baseHeader)
    } yield LoginResponse(response.statusCode, response.data map toUser)

  override def linkGoogleAccount(request: LinkGoogleAccountRequest)(implicit executionContext: ExecutionContext): Future[LoginResponse] =
    auth flatMap (apiUserService.linkAuthData(fromLinkGoogleAccountRequest(request), _)) map { response =>
      LoginResponse(response.statusCode, response.data map toUser)
    }

  override def createInstallation(request: InstallationRequest)(implicit executionContext: ExecutionContext): Future[InstallationResponse] =
    for {
      response <- apiUserService.createInstallation(fromInstallationRequest(request), baseHeader)
    } yield InstallationResponse(response.statusCode, response.data map toInstallation)

  override def updateInstallation(request: InstallationRequest)(implicit executionContext: ExecutionContext): Future[UpdateInstallationResponse] =
    for {
      response <- apiUserService.updateInstallation(fromInstallationRequest(request), baseHeader)
    } yield UpdateInstallationResponse(response.statusCode)

  override def googlePlayPackage(request: GooglePlayPackageRequest)(implicit executionContext: ExecutionContext): Future[GooglePlayPackageResponse] =
    auth flatMap (googlePlayService.getGooglePlayPackage(request.packageName, _)) map { response =>
      GooglePlayPackageResponse(response.statusCode, response.data map (playApp => toGooglePlayApp(playApp.docV2)))
    }

  override def googlePlayPackages(request: GooglePlayPackagesRequest)(implicit executionContext: ExecutionContext): Future[GooglePlayPackagesResponse] =
    auth flatMap (googlePlayService.getGooglePlayPackages(PackagesRequest(request.packageNames), _)) map { response =>
      GooglePlayPackagesResponse(response.statusCode, response.data map (packages => toGooglePlayPackageSeq(packages.items)) getOrElse Seq.empty)
    }

  override def googlePlaySimplePackages(request: GooglePlaySimplePackagesRequest)(implicit executionContext: ExecutionContext): Future[GooglePlaySimplePackagesResponse] =
    auth flatMap (googlePlayService.getGooglePlaySimplePackages(PackagesRequest(request.items), _)) map { response =>
      GooglePlaySimplePackagesResponse(
        response.statusCode,
        response.data.map(playApp => toGooglePlaySimplePackages(playApp)).getOrElse(GooglePlaySimplePackages(Seq.empty, Seq.empty)))
    }

  override def getUserConfig(request: GetUserConfigRequest)(implicit executionContext: ExecutionContext): Future[GetUserConfigResponse] =
    auth flatMap userConfigService.getUserConfig map { response =>
      GetUserConfigResponse(response.statusCode, response.data map toUserConfig)
    }

  override def saveDevice(request: SaveDeviceRequest)(implicit executionContext: ExecutionContext): Future[SaveDeviceResponse] =
    auth flatMap (userConfigService.saveDevice(fromUserConfigDevice(request.userConfigDevice), _)) map { response =>
      SaveDeviceResponse(response.statusCode, response.data map toUserConfig)
    }

  override def saveGeoInfo(request: SaveGeoInfoRequest)(implicit executionContext: ExecutionContext): Future[SaveGeoInfoResponse] =
    auth flatMap (userConfigService.saveGeoInfo(fromUserConfigGeoInfo(request.userConfigGeoInfo), _)) map { response =>
      SaveGeoInfoResponse(response.statusCode, response.data map toUserConfig)
    }

  override def checkpointPurchaseProduct(request: CheckpointPurchaseProductRequest)(implicit executionContext: ExecutionContext): Future[CheckpointPurchaseProductResponse] =
    auth flatMap (userConfigService.checkpointPurchaseProduct(request.productId, _)) map { response =>
      CheckpointPurchaseProductResponse(response.statusCode, response.data map toUserConfig)
    }

  override def checkpointCustomCollection(request: CheckpointCustomCollectionRequest)(implicit executionContext: ExecutionContext): Future[CheckpointCustomCollectionResponse] =
    auth flatMap userConfigService.checkpointCustomCollection map { response =>
      CheckpointCustomCollectionResponse(response.statusCode, response.data map toUserConfig)
    }

  override def checkpointJoinedBy(request: CheckpointJoinedByRequest)(implicit executionContext: ExecutionContext): Future[CheckpointJoinedByResponse] =
    auth flatMap (userConfigService.checkpointJoinedBy(request.otherConfigId, _)) map { response =>
      CheckpointJoinedByResponse(response.statusCode, response.data map toUserConfig)
    }

  override def tester(request: TesterRequest)(implicit executionContext: ExecutionContext): Future[TesterResponse] =
    auth flatMap (userConfigService.tester(request.replace, _)) map { response =>
      TesterResponse(response.statusCode, response.data map toUserConfig)
    }

  private def auth: Future[Seq[(String, String)]] =
   for {
     token <- getSessionToken
     androidId <- repositoryServices.getAndroidId
   } yield baseHeader :+(HeaderDevice, androidId) :+(HeaderToken, token)

  private def getSessionToken: Future[String] =
    repositoryServices.getUser map (_.sessionToken getOrElse (throw UserNotFoundException()))

}