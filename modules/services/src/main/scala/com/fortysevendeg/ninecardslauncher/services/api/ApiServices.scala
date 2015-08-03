package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.api.models.{GoogleDevice, UserConfigDevice, UserConfigGeoInfo}

trait ApiServices {

  def login(
    email: String,
    device: GoogleDevice): ServiceDef2[LoginResponse, ApiServiceException]

  def linkGoogleAccount(
    email: String,
    devices: Seq[GoogleDevice])(implicit requestConfig: RequestConfig): ServiceDef2[LoginResponse, ApiServiceException]

  def createInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]): ServiceDef2[InstallationResponse, ApiServiceException]

  def updateInstallation(
    id: Option[String],
    deviceType: Option[String],
    deviceToken: Option[String],
    userId: Option[String]): ServiceDef2[UpdateInstallationResponse, ApiServiceException]

  def googlePlayPackage(
    packageName: String)(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackageResponse, ApiServiceException]

  def googlePlayPackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackagesResponse, ApiServiceException]

  def googlePlaySimplePackages(
    items: Seq[String])(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlaySimplePackagesResponse, ApiServiceException]

  def getUserConfig()(implicit requestConfig: RequestConfig): ServiceDef2[GetUserConfigResponse, ApiServiceException]

  def saveDevice(
    userConfigDevice: UserConfigDevice)(implicit requestConfig: RequestConfig): ServiceDef2[SaveDeviceResponse, ApiServiceException]

  def saveGeoInfo(
    userConfigGeoInfo: UserConfigGeoInfo)(implicit requestConfig: RequestConfig): ServiceDef2[SaveGeoInfoResponse, ApiServiceException]

  def checkpointPurchaseProduct(
    productId: String)(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointPurchaseProductResponse, ApiServiceException]

  def checkpointCustomCollection()(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointCustomCollectionResponse, ApiServiceException]

  def checkpointJoinedBy(
    otherConfigId: String)(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointJoinedByResponse, ApiServiceException]

  def tester(
    replace: Map[String, String])(implicit requestConfig: RequestConfig): ServiceDef2[TesterResponse, ApiServiceException]
}
