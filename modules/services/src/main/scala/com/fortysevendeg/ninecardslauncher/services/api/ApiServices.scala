package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.api.models._

trait ApiServices {

  /**
   * Tries to login with the email and the device
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
   * Links the devices with the email
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
   * Creates a new user installation based on the provided params
   * @param deviceType the device type. Actually only [[com.fortysevendeg.ninecardslauncher.services.api.models.AndroidDevice]] is supported
   * @param deviceToken the token used by the device
   * @param userId the user identifier
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.InstallationResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.Installation]]
   * @throws ApiServiceException if there was an error in the request
   */
  def createInstallation(
    deviceType: Option[DeviceType],
    deviceToken: Option[String],
    userId: Option[String]): ServiceDef2[InstallationResponse, ApiServiceException]

  /**
   * Updates an existing user installation
   * @param id the installation identifier to update
   * @param deviceType the device type. Actually only [[com.fortysevendeg.ninecardslauncher.services.api.models.AndroidDevice]] is supported
   * @param deviceToken the token used by the device
   * @param userId the user identifier
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.InstallationResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.Installation]]
   * @throws ApiServiceException if there was an error in the request
   */
  def updateInstallation(
    id: String,
    deviceType: Option[DeviceType],
    deviceToken: Option[String],
    userId: Option[String]): ServiceDef2[UpdateInstallationResponse, ApiServiceException]

  /**
   * Fetches the package info from Google Play given a package name
   * @param packageName the package identifier. For example `com.fortysevendeg.ninecardslauncher`
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GooglePlayPackageResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayApp]]
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackage(
    packageName: String)(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackageResponse, ApiServiceException]

  /**
   * Fetches a list of packages information from Google Play given a list of package names. The response is similar to
   * {@link #googlePlayPackage(String)(RequestConfig) googlePlayPackage} but allow to fetch a list of packages with one operation.
   * @param packageNames a sequence of package identifiers
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GooglePlayPackagesResponse]] with the HTTP Code
   *         of the response and a sequence of [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayPackage]]
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackagesResponse, ApiServiceException]

  /**
   * Simplified version of the {@link #googlePlayPackages(Seq[String])(RequestConfig) googlePlayPackages}
   * @param packageNames a sequence of package identifiers
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GooglePlaySimplePackagesResponse]] with the HTTP Code
   *         of the response and a sequence of [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages]]
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlaySimplePackages(
    packageNames: Seq[String])(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlaySimplePackagesResponse, ApiServiceException]

  /**
   * Fetches the user configuration associated to the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GetUserConfigResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getUserConfig()(implicit requestConfig: RequestConfig): ServiceDef2[GetUserConfigResponse, ApiServiceException]

  /**
   * Creates or updates a device associated to the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @param userConfigDevice the device to store
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.SaveDeviceResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def saveDevice(
    userConfigDevice: UserConfigDevice)(implicit requestConfig: RequestConfig): ServiceDef2[SaveDeviceResponse, ApiServiceException]

  /**
   * Creates or updates geolocation information associated to the user identified by the data in
   * [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @param userConfigGeoInfo the information to be stored
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.SaveGeoInfoResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def saveGeoInfo(
    userConfigGeoInfo: UserConfigGeoInfo)(implicit requestConfig: RequestConfig): ServiceDef2[SaveGeoInfoResponse, ApiServiceException]

  /**
   * Notifies that a product has been purchased by the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @param productId the product identifier
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.CheckpointPurchaseProductResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def checkpointPurchaseProduct(
    productId: String)(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointPurchaseProductResponse, ApiServiceException]

  /**
   * Notifies that a collection has been created by the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.CheckpointCustomCollectionResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def checkpointCustomCollection()(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointCustomCollectionResponse, ApiServiceException]

  /**
   * Notifies that the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]] has joined to the application
   * by another user
   * @param otherConfigId the configuration identifier of the referal
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.CheckpointJoinedByResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def checkpointJoinedBy(
    otherConfigId: String)(implicit requestConfig: RequestConfig): ServiceDef2[CheckpointJoinedByResponse, ApiServiceException]

  /**
   * Sends about the current device indicating that is a tester user
   * @param replace a param/values map to save in the server
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.TesterResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def tester(
    replace: Map[String, String])(implicit requestConfig: RequestConfig): ServiceDef2[TesterResponse, ApiServiceException]

  /**
   * Fetches the recommended applications based on some request params
   * @param categories sequence of package ids
   * @param likePackages sequence of similar packages
   * @param excludePackages sequence of exclude packages
   * @param limit the maximum number of apps returned
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.RecommendationResponse]] with the HTTP Code
   *         of the response and the sequence of recommended [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayApp]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getRecommendedApps(
    categories: Seq[String],
    likePackages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig): ServiceDef2[RecommendationResponse, ApiServiceException]

  /**
    * Fetches the public collections based on some request params
    * @param category category of collections
    * @param collectionType type [top or latest]
    * @param offset offset of list
    * @param limit the maximum number of collection returned
    * @return the [[com.fortysevendeg.ninecardslauncher.services.api.RecommendationResponse]] with the HTTP Code
    *         of the response and the sequence of recommended [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayApp]]
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getShareCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig): ServiceDef2[SharedCollectionResponseList, ApiServiceException]
}
