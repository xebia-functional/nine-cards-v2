package com.fortysevendeg.ninecardslauncher.services.persistence.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.repository.repositories._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.persistence.conversions.Conversions

class PersistenceServicesImpl(
  val appRepository: AppRepository,
  val cardRepository: CardRepository,
  val collectionRepository: CollectionRepository,
  val dockAppRepository: DockAppRepository,
  val geoInfoRepository: GeoInfoRepository,
  val userRepository: UserRepository)
  extends PersistenceServices
  with Conversions
  with PersistenceDependencies
  with AppPersistenceServicesImpl
  with CardPersistenceServicesImpl
  with CollectionPersistenceServicesImpl
  with DockAppPersistenceServicesImpl
  with GeoInfoPersistenceServicesImpl
  with UserPersistenceServicesImpl
  with AndroidPersistenceServicesImpl
  with ImplicitsPersistenceServiceExceptions {

  override def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true) = super.fetchApps(orderBy, ascending)

  override def findAppByPackage(packageName: String) = super.findAppByPackage(packageName)

  override def addApp(request: AddAppRequest) = super.addApp(request)

  override def deleteAllApps() = super.deleteAllApps()

  override def deleteAppByPackage(packageName: String) = super.deleteAppByPackage(packageName)

  override def updateApp(request: UpdateAppRequest) = super.updateApp(request)

  override def fetchIterableApps(orderBy: FetchAppOrder, ascending: Boolean = true) = super.fetchIterableApps(orderBy, ascending)

  override def fetchIterableAppsByKeyword(keyword: String, orderBy: FetchAppOrder, ascending: Boolean = true) =
    super.fetchIterableAppsByKeyword(keyword, orderBy, ascending)

  override def fetchAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true) =
    super.fetchAppsByCategory(category, orderBy, ascending)

  override def fetchIterableAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true) =
    super.fetchIterableAppsByCategory(category, orderBy, ascending)

  override def fetchAlphabeticalAppsCounter = super.fetchAlphabeticalAppsCounter

  override def addCard(request: AddCardRequest) = super.addCard(request)

  override def deleteAllCards() = super.deleteAllCards()

  override def deleteCard(request: DeleteCardRequest) = super.deleteCard(request)

  override def deleteCardsByCollection(collectionId: Int) = super.deleteCardsByCollection(collectionId)

  override def fetchCardsByCollection(request: FetchCardsByCollectionRequest) = super.fetchCardsByCollection(request)

  override def fetchCards = super.fetchCards

  override def findCardById(request: FindCardByIdRequest) = super.findCardById(request)

  override def updateCard(request: UpdateCardRequest) = super.updateCard(request)

  override def addCollection(request: AddCollectionRequest) = super.addCollection(request)

  override def deleteAllCollections() = super.deleteAllCollections()

  override def deleteCollection(request: DeleteCollectionRequest) = super.deleteCollection(request)

  override def fetchCollections = super.fetchCollections

  override def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest) = super.fetchCollectionBySharedCollection(request)

  override def fetchCollectionByPosition(request: FetchCollectionByPositionRequest) = super.fetchCollectionByPosition(request)

  override def findCollectionById(request: FindCollectionByIdRequest) = super.findCollectionById(request)

  override def updateCollection(request: UpdateCollectionRequest) = super.updateCollection(request)

  override def addGeoInfo(request: AddGeoInfoRequest) = super.addGeoInfo(request)

  override def deleteAllGeoInfoItems() = super.deleteAllGeoInfoItems()

  override def deleteGeoInfo(request: DeleteGeoInfoRequest) = super.deleteGeoInfo(request)

  override def fetchGeoInfoByConstrain(request: FetchGeoInfoByConstrainRequest) = super.fetchGeoInfoByConstrain(request)

  override def fetchGeoInfoItems = super.fetchGeoInfoItems

  override def findGeoInfoById(request: FindGeoInfoByIdRequest) = super.findGeoInfoById(request)

  override def updateGeoInfo(request: UpdateGeoInfoRequest) = super.updateGeoInfo(request)

  override def addUser(request: AddUserRequest) = super.addUser(request)

  override def deleteAllUsers() = super.deleteAllUsers()

  override def deleteUser(request: DeleteUserRequest) = super.deleteUser(request)

  override def fetchUsers = super.fetchUsers

  override def findUserById(request: FindUserByIdRequest) = super.findUserById(request)

  override def updateUser(request: UpdateUserRequest) = super.updateUser(request)

  override def addDockApp(request: AddDockAppRequest) = super.addDockApp(request)

  override def deleteAllDockApps() = super.deleteAllDockApps()

  override def deleteDockApp(request: DeleteDockAppRequest) = super.deleteDockApp(request)

  override def fetchDockApps = super.fetchDockApps

  override def fetchIterableDockApps = super.fetchIterableDockApps

  override def findDockAppById(request: FindDockAppByIdRequest) = super.findDockAppById(request)

  override def updateDockApp(request: UpdateDockAppRequest) = super.updateDockApp(request)

  override def getAndroidId(implicit context: ContextSupport) = super.getAndroidId
}
