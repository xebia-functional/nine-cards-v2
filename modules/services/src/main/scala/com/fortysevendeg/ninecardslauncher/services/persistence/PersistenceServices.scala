package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.services.api.models.{Installation, User => ApiUser}
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

trait PersistenceServices {

  /**
   * Obtains all the apps from the repository
   * @param orderBy indicates the field to order by
   * @param ascending indicates if it will be in ascending order or not
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true): ServiceDef2[Seq[App], PersistenceServiceException]

  /**
   * Obtains an app from the repository by the package name
   * @param packageName the package name of the app to get
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def findAppByPackage(packageName: String): ServiceDef2[Option[App], PersistenceServiceException]

  /**
   * Adds an app to the repository
   * @param request includes the necessary data to create a new app in the repository
   * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.App
   * @throws PersistenceServiceException if exist some problem obtaining the app
   */
  def addApp(request: AddAppRequest): ServiceDef2[App, PersistenceServiceException]

  /**
   * Deletes an app from the repository by the package name
   * @param packageName the package name of the app to delete
   * @return an Int if the app has been deleted correctly
   * @throws PersistenceServiceException if exist some problem deleting the app
   */
  def deleteAppByPackage(packageName: String): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Updates the data of an app from the repository
   * @param request includes the data to update the app
   * @return an Int if the app has been updated correctly
   * @throws PersistenceServiceException if exist some problem updating the app
   */
  def updateApp(request: UpdateAppRequest): ServiceDef2[Int, PersistenceServiceException]

  def addCard(request: AddCardRequest): ServiceDef2[Card, PersistenceServiceException]

  def deleteCard(request: DeleteCardRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchCardsByCollection(request: FetchCardsByCollectionRequest): ServiceDef2[Seq[Card], PersistenceServiceException]

  def fetchCards: ServiceDef2[Seq[Card], PersistenceServiceException]

  def findCardById(request: FindCardByIdRequest): ServiceDef2[Option[Card], PersistenceServiceException]

  def updateCard(request: UpdateCardRequest): ServiceDef2[Int, PersistenceServiceException]

  def addCollection(request: AddCollectionRequest): ServiceDef2[Collection, PersistenceServiceException]

  def deleteCollection(request: DeleteCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchCollections: ServiceDef2[Seq[Collection], PersistenceServiceException]

  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def findCollectionById(request: FindCollectionByIdRequest): ServiceDef2[Option[Collection], PersistenceServiceException]

  def updateCollection(request: UpdateCollectionRequest): ServiceDef2[Int, PersistenceServiceException]

  def addGeoInfo(request: AddGeoInfoRequest): ServiceDef2[GeoInfo, PersistenceServiceException]

  def deleteGeoInfo(request: DeleteGeoInfoRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest): ServiceDef2[Option[GeoInfo], PersistenceServiceException]

  def fetchGeoInfoItems: ServiceDef2[Seq[GeoInfo], PersistenceServiceException]

  def findGeoInfoById(request: FindGeoInfoByIdRequest): ServiceDef2[Option[GeoInfo], PersistenceServiceException]

  def updateGeoInfo(request: UpdateGeoInfoRequest): ServiceDef2[Int, PersistenceServiceException]

  def getUser(implicit context: ContextSupport): ServiceDef2[ApiUser, PersistenceServiceException]

  def saveUser(user: ApiUser)(implicit context: ContextSupport): ServiceDef2[Unit, PersistenceServiceException]

  def resetUser(implicit context: ContextSupport): ServiceDef2[Boolean, PersistenceServiceException]

  def getAndroidId(implicit context: ContextSupport): ServiceDef2[String, AndroidIdNotFoundException]

  def getInstallation(implicit context: ContextSupport): ServiceDef2[Installation, InstallationNotFoundException]

  def existsInstallation(implicit context: ContextSupport): ServiceDef2[Boolean, PersistenceServiceException]

  def saveInstallation(installation: Installation)(implicit context: ContextSupport): ServiceDef2[Unit, PersistenceServiceException]

  def addUser(request: AddUserRequest): ServiceDef2[User, PersistenceServiceException]

  def deleteUser(request: DeleteUserRequest): ServiceDef2[Int, PersistenceServiceException]

  def fetchUsers: ServiceDef2[Seq[User], PersistenceServiceException]

  def findUserById(request: FindUserByIdRequest): ServiceDef2[Option[User], PersistenceServiceException]

  def updateUser(request: UpdateUserRequest): ServiceDef2[Int, PersistenceServiceException]

}
