package cards.nine.services.api

import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models._

trait ApiServices {

  /**
    * Tries to login with the email and the device against backend V1
    *
    * @param email user email
    * @param device user device
    * @return the [[cards.nine.models.LoginResponseV1]]
    * @throws ApiServiceV1ConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user is not found or the request throws an Exception
    */
  def loginV1(
    email: String,
    device: LoginV1Device): TaskService[LoginResponseV1]

  /**
    * Fetches the user configuration associated to the user identified by the data in [[cards.nine.models.RequestConfigV1]]
    *
    * @return the [[cards.nine.models.UserV1]] with the [[UserV1]]
    * @throws ApiServiceV1ConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getUserConfigV1()(implicit requestConfig: RequestConfigV1): TaskService[UserV1]

  /**
    * Tries to login with the email, the androidId and the tokenId
    *
    * @param email user email
    * @param androidId device identifier
    * @param tokenId token id obtained in the email authentication
    * @return the [[cards.nine.models.LoginResponse]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user is not found or the request throws an Exception
    */
  def login(
    email: String,
    androidId: String,
    tokenId: String): TaskService[LoginResponse]

  /**
    * Updates an existing user installation
    *
    * @param deviceToken the token used for push notification
    * @param requestConfig necessary info for the headers
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if there was an error in the request
    */
  def updateInstallation(deviceToken: Option[String])(implicit requestConfig: RequestConfig): TaskService[Unit]

  /**
    * Fetches the package info from Google Play given a package name
    *
    * @param packageName the package identifier. For example `com.fortysevendeg.ninecardslauncher`
    * @param requestConfig necessary info for the headers
    * @return the [[cards.nine.models.CategorizedPackage]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if there was an error in the request
    */
  def googlePlayPackage(packageName: String)(implicit requestConfig: RequestConfig): TaskService[CategorizedPackage]

  /**
    * Fetches a list of packages information from Google Play given a list of package names. The response is similar to
    * {@link #googlePlayPackage(String)(RequestConfig) googlePlayPackage} but allow to fetch a list of packages with one operation.
    *
    * @param packageNames a sequence of package identifiers
    * @param requestConfig necessary info for the headers
    * @return the sequence of [[cards.nine.models.CategorizedPackage]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if there was an error in the request
    */
  def googlePlayPackages(packageNames: Seq[String])(implicit requestConfig: RequestConfig): TaskService[Seq[CategorizedPackage]]

  /**
    * Fetches a list of packages information from Google Play given a list of package names.
    * Differs from googlePlayPackages by providing more information
    *
    * @param packageNames a sequence of package identifiers
    * @param requestConfig necessary info for the headers
    * @return the sequence of [[cards.nine.models.CategorizedDetailPackage]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if there was an error in the request
    */
  def googlePlayPackagesDetail(packageNames: Seq[String])(implicit requestConfig: RequestConfig): TaskService[Seq[CategorizedDetailPackage]]

  /**
    * Fetches the recommended applications based on a category
    *
    * @param category the category
    * @param excludePackages sequence of exclude packages
    * @param limit the maximum number of apps returned
    * @return the Seq[[cards.nine.models.RecommendationApp]] of recommended apps
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getRecommendedApps(
    category: String,
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[Seq[RecommendationApp]]

  /**
    * Fetches the recommended applications based on other packages
    *
    * @param packages the liked packages
    * @param excludePackages sequence of exclude packages
    * @param limit the maximum number of apps returned
    * @return the Seq[[cards.nine.models.RecommendationApp]] of recommended apps
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getRecommendedAppsByPackages(
    packages: Seq[String],
    excludePackages: Seq[String],
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[Seq[RecommendationApp]]

  /**
    * Fetches the public collection
    *
    * @param sharedCollectionId the public collection id
    * @return the TaskService containing a SharedCollection with the
    *         collection or ApiServiceException if the user doesn't exists or there was an error in the request
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSharedCollection(
    sharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[SharedCollection]

  /**
    * Fetches the public collections based on some request params
    *
    * @param category category of collections
    * @param collectionType type [top or latest]
    * @param offset offset of list
    * @param limit the maximum number of collection returned
    * @return the [[cards.nine.models.SharedCollection]] with the sequence of recommended collections
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSharedCollectionsByCategory(
    category: String,
    collectionType: String,
    offset: Int,
    limit: Int)(implicit requestConfig: RequestConfig): TaskService[Seq[SharedCollection]]

  /**
    * Fetches the published collections
    *
    * @return the [[cards.nine.models.SharedCollection]] with the sequence of recommended collections
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getPublishedCollections()(implicit requestConfig: RequestConfig): TaskService[Seq[SharedCollection]]

  /**
    * Persists a new shared collection
    *
    * @param name The name of the collection
    * @param author The original author of the collection
    * @param packages The list of packages in the collection
    * @param icon The collection's icon
    * @param community A flag for whether this is a community collection
    * @return a String with the sharedCollectionId
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the service is unable to create the shared collection
    */
  def createSharedCollection(
    name: String,
    author: String,
    packages: Seq[String],
    category: String,
    icon: String,
    community: Boolean)(implicit requestConfig: RequestConfig): TaskService[String]

  /**
    * Updates an existing  shared collection
    *
    * @param sharedCollectionId The collection identifier
    * @param name The name of the collection
    * @param packages The list of packages in the collection
    * @return a String with the sharedCollectionId
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the service is unable to create the shared collection
    */
  def updateSharedCollection(
    sharedCollectionId: String,
    name: Option[String],
    packages: Seq[String])(implicit requestConfig: RequestConfig): TaskService[String]

  /**
    * Fetches the subscriptions
    *
    * @return the Seq[String] with the subscriptions
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def getSubscriptions()(implicit requestConfig: RequestConfig): TaskService[Seq[String]]

  /**
    * Subscribes to a public collection
 *
    * @param originalSharedCollectionId the public id of the collection to subscribe on
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def subscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[Unit]

  /**
    * Unsubscribes from a public collection
    *
    * @param originalSharedCollectionId the public id of the collection to unsubscribe from
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def unsubscribe(
    originalSharedCollectionId: String)(implicit requestConfig: RequestConfig): TaskService[Unit]

  /**
    * Rank the packages by importance inside their category
    *
    * @param packagesByCategorySeq a Sequence with the packages of the apps to rank ordered by its category
    * @param location the current country location of the device if it can be obtained
    * @return the Seq[[cards.nine.models.RankApps]]
    * @throws ApiServiceConfigurationException if the configuration is not valid or can't be found
    * @throws ApiServiceException if the user doesn't exists or there was an error in the request
    */
  def rankApps(
    packagesByCategorySeq: Seq[PackagesByCategory],
    location: Option[String])(implicit requestConfig: RequestConfig): TaskService[Seq[RankApps]]

}
