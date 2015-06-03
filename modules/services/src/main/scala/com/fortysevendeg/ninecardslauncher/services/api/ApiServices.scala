package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.services.Service

trait ApiServices {

  def login: Service[LoginRequest, LoginResponse]

  def linkGoogleAccount: Service[LinkGoogleAccountRequest, LoginResponse]

  def createInstallation: Service[InstallationRequest, InstallationResponse]

  def updateInstallation: Service[InstallationRequest, UpdateInstallationResponse]

  def googlePlayPackage: Service[GooglePlayPackageRequest, GooglePlayPackageResponse]

  def googlePlayPackages: Service[GooglePlayPackagesRequest, GooglePlayPackagesResponse]

  def googlePlaySimplePackages: Service[GooglePlaySimplePackagesRequest, GooglePlaySimplePackagesResponse]

  def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse]

  def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse]

  def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse]

  def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse]

  def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse]

  def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse]

  def tester: Service[TesterRequest, TesterResponse]
}
