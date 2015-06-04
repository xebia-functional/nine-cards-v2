package com.fortysevendeg.ninecardslauncher.modules.api

import scala.concurrent.{Future, ExecutionContext}

trait ApiServices {

  def login(request: LoginRequest)(implicit executionContext: ExecutionContext): Future[LoginResponse]

  def linkGoogleAccount(request: LinkGoogleAccountRequest)(implicit executionContext: ExecutionContext): Future[LoginResponse]

  def createInstallation(request: InstallationRequest)(implicit executionContext: ExecutionContext): Future[InstallationResponse]

  def updateInstallation(request: InstallationRequest)(implicit executionContext: ExecutionContext): Future[UpdateInstallationResponse]

  def googlePlayPackage(request: GooglePlayPackageRequest)(implicit executionContext: ExecutionContext): Future[GooglePlayPackageResponse]

  def googlePlayPackages(request: GooglePlayPackagesRequest)(implicit executionContext: ExecutionContext): Future[GooglePlayPackagesResponse]

  def googlePlaySimplePackages(request: GooglePlaySimplePackagesRequest)(implicit executionContext: ExecutionContext): Future[GooglePlaySimplePackagesResponse]

  def getUserConfig(request: GetUserConfigRequest)(implicit executionContext: ExecutionContext): Future[GetUserConfigResponse]

  def saveDevice(request: SaveDeviceRequest)(implicit executionContext: ExecutionContext): Future[SaveDeviceResponse]

  def saveGeoInfo(request: SaveGeoInfoRequest)(implicit executionContext: ExecutionContext): Future[SaveGeoInfoResponse]

  def checkpointPurchaseProduct(request: CheckpointPurchaseProductRequest)(implicit executionContext: ExecutionContext): Future[CheckpointPurchaseProductResponse]

  def checkpointCustomCollection(request: CheckpointCustomCollectionRequest)(implicit executionContext: ExecutionContext): Future[CheckpointCustomCollectionResponse]

  def checkpointJoinedBy(request: CheckpointJoinedByRequest)(implicit executionContext: ExecutionContext): Future[CheckpointJoinedByResponse]

  def tester(request: TesterRequest)(implicit executionContext: ExecutionContext): Future[TesterResponse]
}
