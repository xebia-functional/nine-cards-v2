package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.api.models.{GoogleDevice, UserConfigDevice, UserConfigGeoInfo}

trait ApiServices {

  /**
   * Try to login with the email and the device
   * @param email user email
   * @param device user device
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.LoginResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.User]]
   * @throws ApiServiceException if the user is not found or the request throws an Exception
   */
  def login(
    email: String,
    device: GoogleDevice): ServiceDef2[LoginResponse, ApiServiceException]

  /**
   * Link the devices with the email
   * @param email user email
   * @param devices user devices
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.LoginResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.User]]
   * @throws ApiServiceException if the user is not found or the request throws an Exception
   */
  def linkGoogleAccount(
    email: String,
    devices: Seq[GoogleDevice])(implicit requestConfig: RequestConfig): ServiceDef2[LoginResponse, ApiServiceException]

  /**
   * Creates a new User installation based on the provided params
   * @param deviceType the device type
   * @param deviceToken the token used by the device
   * @param userId the user identifier
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.LoginResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.Installation]]
   * @throws ApiServiceException if there was an error in the request
   */
  def createInstallation(
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
