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
   * Fetches the user configuration associated to the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfig]]
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GetUserConfigResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getUserConfig()(implicit requestConfig: RequestConfig): ServiceDef2[GetUserConfigResponse, ApiServiceException]

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
    * @return the [[com.fortysevendeg.ninecardslauncher.services.api.SharedCollectionResponseList]] with the HTTP Code
    *         of the response and the sequence of recommended [[com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlayApp]]
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig): ServiceDef2[SharedCollectionResponseList, ApiServiceException]

  /**
    * Persists a new shared collection
    * @param name The name of the collection
    * @param description The user's description of the collection
    * @param author The original author of the collection
    * @param packages The list of packages in the collection
    * @param icon The collection's icon
    * @param community A flag for whether this is a community collection
    * @return the [[com.fortysevendeg.ninecardslauncher.services.api.CreateSharedCollectionResponse]] with the HTTP Code
    *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.CreateSharedCollection]]
    * @throws ApiServiceException if the service is unable to create the shared collection
    */
  def createSharedCollection(
    name: String,
    description: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig): ServiceDef2[CreateSharedCollectionResponse, ApiServiceException]
}
