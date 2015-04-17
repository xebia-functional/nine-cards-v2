package com.fortysevendeg.ninecardslauncher.modules.api

import com.fortysevendeg.ninecardslauncher.commons.Service

trait ApiServices {

  def login: Service[LoginRequest, LoginResponse]

  def linkGoogleAccount: Service[LinkGoogleAccountRequest, LoginResponse]

  def createInstallation: Service[InstallationRequest, InstallationResponse]

  def updateInstallation: Service[InstallationRequest, UpdateInstallationResponse]

  def getUserConfig: Service[GetUserConfigRequest, GetUserConfigResponse]

  def saveDevice: Service[SaveDeviceRequest, SaveDeviceResponse]

  def saveGeoInfo: Service[SaveGeoInfoRequest, SaveGeoInfoResponse]

  def checkpointPurchaseProduct: Service[CheckpointPurchaseProductRequest, CheckpointPurchaseProductResponse]

  def checkpointCustomCollection: Service[CheckpointCustomCollectionRequest, CheckpointCustomCollectionResponse]

  def checkpointJoinedBy: Service[CheckpointJoinedByRequest, CheckpointJoinedByResponse]

  def tester: Service[TesterRequest, TesterResponse]
}

trait ApiServicesComponent {
  val apiServices: ApiServices
}