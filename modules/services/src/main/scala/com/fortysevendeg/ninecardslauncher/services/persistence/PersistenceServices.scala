package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
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

  def getAndroidId(implicit context: ContextSupport): ServiceDef2[String, AndroidIdNotFoundException]

  /**
   * Adds an user to the repository
   * @param request includes the necessary data to create a new user in the repository
   * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.User
   * @throws PersistenceServiceException if exist some problem creating the user
   */
  def addUser(request: AddUserRequest): ServiceDef2[User, PersistenceServiceException]

  /**
   * Deletes an user from the repository by the user
   * @param request includes the user to delete
   * @return an Int if the user has been deleted correctly
   * @throws PersistenceServiceException if exist some problem deleting the user
   */
  def deleteUser(request: DeleteUserRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
   * Obtains all the users from the repository
   * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.User]
   * @throws PersistenceServiceException if exist some problem obtaining the users
   */
  def fetchUsers: ServiceDef2[Seq[User], PersistenceServiceException]

  /**
   * Obtains an user from the repository by the id
   * @param request includes the user id  of the user to get
   * @throws PersistenceServiceException if exist some problem obtaining the user
   */
  def findUserById(request: FindUserByIdRequest): ServiceDef2[Option[User], PersistenceServiceException]

  /**
   * Updates the data of an user from the repository
   * @param request includes the data to update the user
   * @return an Int if the user has been updated correctly
   * @throws PersistenceServiceException if exist some problem updating the user
   */
  def updateUser(request: UpdateUserRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Adds an dock app to the repository
    * @param request includes the necessary data to create a new dock app in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp
    * @throws PersistenceServiceException if exist some problem creating the dock app
    */
  def addDockApp(request: AddDockAppRequest): ServiceDef2[DockApp, PersistenceServiceException]

  /**
    * Deletes an dock app from the repository by the dock app
    * @param request includes the dock app to delete
    * @return an Int if the dock app has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the dock app
    */
  def deleteDockApp(request: DeleteDockAppRequest): ServiceDef2[Int, PersistenceServiceException]

  /**
    * Obtains all the dock apps from the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp]
    * @throws PersistenceServiceException if exist some problem obtaining the dock apps
    */
  def fetchDockApps: ServiceDef2[Seq[DockApp], PersistenceServiceException]

  /**
    * Obtains an dock app from the repository by the id
    * @param request includes the dock app id  of the dock app to get
    * @throws PersistenceServiceException if exist some problem obtaining the dock app
    */
  def findDockAppById(request: FindDockAppByIdRequest): ServiceDef2[Option[DockApp], PersistenceServiceException]

  /**
    * Updates the data of an dock app from the repository
    * @param request includes the data to update the dock app
    * @return an Int if the dock app has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the dock app
    */
  def updateDockApp(request: UpdateDockAppRequest): ServiceDef2[Int, PersistenceServiceException]
}
