package com.fortysevendeg.ninecardslauncher.services.api

import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.api.models._

trait ApiServices {

  /**
   * Tries to login with the email and the device against backend V1
   * @param email user email
   * @param device user device
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.LoginResponseV1]]
   * @throws ApiServiceException if the user is not found or the request throws an Exception
   */
  def loginV1(
    email: String,
    device: GoogleDevice): ServiceDef2[LoginResponseV1, ApiServiceException]

  /**
    * Tries to login with the email, the androidId and the tokenId
    * @param email user email
    * @param androidId device identifier
    * @param tokenId token id obtained in the email authentication
    * @return the [[com.fortysevendeg.ninecardslauncher.services.api.LoginResponse]]
    * @throws ApiServiceException if the user is not found or the request throws an Exception
    */
  def login(
    email: String,
    androidId: String,
    tokenId: String): ServiceDef2[LoginResponse, ApiServiceException]

  /**
   * Updates an existing user installation
   * @param deviceToken the token used for push notification
   * @param requestConfig necessary info for the headers
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.UpdateInstallationResponse]] with the HTTP Code
   *         of the response
   * @throws ApiServiceException if there was an error in the request
   */
  def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig): ServiceDef2[UpdateInstallationResponse, ApiServiceException]

  /**
   * Fetches the package info from Google Play given a package name
   * @param packageName the package identifier. For example `com.fortysevendeg.ninecardslauncher`
   * @param requestConfig necessary info for the headers
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GooglePlayPackageResponse]] with the HTTP Code
   *         of the response and a sequence of [[com.fortysevendeg.ninecardslauncher.services.api.CategorizedPackage]]
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackageResponse, ApiServiceException]

  /**
   * Fetches a list of packages information from Google Play given a list of package names. The response is similar to
   * {@link #googlePlayPackage(String)(RequestConfig) googlePlayPackage} but allow to fetch a list of packages with one operation.
   * @param packageNames a sequence of package identifiers
   * @param requestConfig necessary info for the headers
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GooglePlayPackagesResponse]] with the HTTP Code
   *         of the response and a sequence of [[com.fortysevendeg.ninecardslauncher.services.api.CategorizedPackage]]
   * @throws ApiServiceException if there was an error in the request
   */
  def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig): ServiceDef2[GooglePlayPackagesResponse, ApiServiceException]

  /**
   * Fetches the user configuration associated to the user identified by the data in [[com.fortysevendeg.ninecardslauncher.services.api.RequestConfigV1]]
    *
   * @return the [[com.fortysevendeg.ninecardslauncher.services.api.GetUserConfigResponse]] with the HTTP Code
   *         of the response and the [[com.fortysevendeg.ninecardslauncher.services.api.models.UserConfig]]
   * @throws ApiServiceException if the user doesn't exists or there was an error in the request
   */
  def getUserConfigV1()(implicit requestConfig: RequestConfigV1): ServiceDef2[GetUserConfigResponse, ApiServiceException]

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
    limit: Int)(implicit requestConfig: RequestConfigV1): ServiceDef2[RecommendationResponse, ApiServiceException]

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
    *         of the response and the sharedCollectionId
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
