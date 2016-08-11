package com.fortysevendeg.ninecardslauncher.services.persistence

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.services.persistence.models._

trait PersistenceServices {

  /**
    * Obtains all the apps from the repository
    *
    * @param orderBy   indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchApps(orderBy: FetchAppOrder, ascending: Boolean = true): CatsService[Seq[App]]

  /**
    * Obtains iterable of apps from the repository
    *
    * @param orderBy   indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchIterableApps(orderBy: FetchAppOrder, ascending: Boolean = true): CatsService[IterableApps]

  /**
    * Obtains iterable of apps by keywords from the repository
    *
    * @param keyword   keyword for search
    * @param orderBy   indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchIterableAppsByKeyword(keyword: String, orderBy: FetchAppOrder, ascending: Boolean = true): CatsService[IterableApps]

  /**
    * Obtains all the apps by category from the repository
    *
    * @param category  category for search
    * @param orderBy   indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true): CatsService[Seq[App]]

  /**
    * Obtains iterable of apps by category from the repository
    *
    * @param category  category for search
    * @param orderBy   indicates the field to order by
    * @param ascending indicates if it will be in ascending order or not
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableApps
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchIterableAppsByCategory(category: String, orderBy: FetchAppOrder, ascending: Boolean = true): CatsService[IterableApps]

  /**
    * Returns the number of times the first letter of a app is repeated alphabetically
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchAlphabeticalAppsCounter: CatsService[Seq[DataCounter]]

  /**
    * Returns the number of times in every category alphabetically
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchCategorizedAppsCounter: CatsService[Seq[DataCounter]]

  /**
    * Returns the number of times by installation date
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DataCounter]
    * @throws PersistenceServiceException if exist some problem obtaining the apps
    */
  def fetchInstallationDateAppsCounter: CatsService[Seq[DataCounter]]

  /**
    * Obtains an app from the repository by the package name
    *
    * @param packageName the package name of the app to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def findAppByPackage(packageName: String): CatsService[Option[App]]

  /**
    * Obtains apps from the repository by the package names
    *
    * @param packageNames the package names of the apps to get
    * @return an Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.App]
    * @throws PersistenceServiceException if exist some problem obtaining the app
    */
  def fetchAppByPackages(packageNames: Seq[String]): CatsService[Seq[App]]

  /**
    * Adds an app to the repository
    *
    * @param request includes the necessary data to create a new app in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.App
    * @throws PersistenceServiceException if exist some problem creating the app
    */
  def addApp(request: AddAppRequest): CatsService[App]

  /**
    * Adds a sequence of apps to the repository
    *
    * @param request includes the necessary data to create new apps in the repository
    * @return Unit
    * @throws PersistenceServiceException if exist some problem creating apps
    */
  def addApps(request: Seq[AddAppRequest]): CatsService[Unit]

  /**
    * Deletes all apps from the repository by the where clause
    *
    * @return an Int if the apps has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the apps
    */
  def deleteAllApps(): CatsService[Int]

  /**
    * Deletes an app from the repository by the package name
    *
    * @param packageName the package name of the app to delete
    * @return an Int if the app has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the app
    */
  def deleteAppByPackage(packageName: String): CatsService[Int]

  /**
    * Updates the data of an app from the repository
    *
    * @param request includes the data to update the app
    * @return an Int if the app has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the app
    */
  def updateApp(request: UpdateAppRequest): CatsService[Int]

  /**
    * Adds a card to the repository
    *
    * @param request includes the necessary data to create a new card in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
    * @throws PersistenceServiceException if exist some problem creating the card
    */
  def addCard(request: AddCardRequest): CatsService[Card]

  /**
    * Adds a sequence of cards to the repository
    *
    * @param request includes the necessary data to create new cards in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Card
    * @throws PersistenceServiceException if exist some problem creating the card
    */
  def addCards(request: Seq[AddCardWithCollectionIdRequest]): CatsService[Seq[Card]]

  /**
    * Deletes all cards from the repository by the where clause
    *
    * @return an Int if the cards has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the cards
    */
  def deleteAllCards(): CatsService[Int]

  /**
    * Deletes a card from the repository by the card
    *
    * @param request includes the card to delete
    * @return an Int if the card has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the card
    */
  def deleteCard(request: DeleteCardRequest): CatsService[Int]

  /**
    * Deletes the cards from the repository by the collection id
    *
    * @param collectionId the id of the collection that contains the cards
    * @return an Int if the cards have been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the cards
    */
  def deleteCardsByCollection(collectionId: Int): CatsService[Int]

  /**
    * Obtains all the cards from the repository by the collection id
    *
    * @param request includes the id of the collection
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the cards
    */
  def fetchCardsByCollection(request: FetchCardsByCollectionRequest): CatsService[Seq[Card]]

  /**
    * Obtains all the cards from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the cards
    */
  def fetchCards: CatsService[Seq[Card]]

  /**
    * Obtains a card from the repository by the id
    *
    * @param request includes the id of the card to find
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Card]
    * @throws PersistenceServiceException if exist some problem obtaining the card
    */
  def findCardById(request: FindCardByIdRequest): CatsService[Option[Card]]

  /**
    * Updates the data of an card from the repository
    *
    * @param request includes the data to update the card
    * @return an Int if the card has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the card
    */
  def updateCard(request: UpdateCardRequest): CatsService[Int]

  /**
    * Bulk update of the data of some cards from the repository
    *
    * @param request includes the data to update the cards
    * @return a Seq[Int] if the cards has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the card
    */
  def updateCards(request: UpdateCardsRequest): CatsService[Seq[Int]]

  /**
    * Adds an collection to the repository
    *
    * @param request includes the necessary data to create a new collection in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection
    * @throws PersistenceServiceException if exist some problem creating the collection
    */
  def addCollection(request: AddCollectionRequest): CatsService[Collection]

  /**
    * Adds collections to the repository
    *
    * @param requests includes the necessary data to create new collections in the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem creating the collection
    */
  def addCollections(requests: Seq[AddCollectionRequest]): CatsService[Seq[Collection]]

  /**
    * Deletes all collections from the repository by the where clause
    *
    * @return an Int if the collections has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the collections
    */
  def deleteAllCollections(): CatsService[Int]

  /**
    * Deletes a collection from the repository by the collection
    *
    * @param request includes the collection to delete
    * @return an Int if the collection has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the collection
    */
  def deleteCollection(request: DeleteCollectionRequest): CatsService[Int]

  /**
    * Obtains all the collections from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collections
    */
  def fetchCollections: CatsService[Seq[Collection]]

  /**
    * Obtains the collection from the repository by the sharedCollection id
    *
    * @param request includes the id of the sharedCollection
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def fetchCollectionBySharedCollection(request: FetchCollectionBySharedCollectionRequest): CatsService[Option[Collection]]

  /**
    * Obtains the collection from the repository by the position
    *
    * @param request includes the position
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def fetchCollectionByPosition(request: FetchCollectionByPositionRequest): CatsService[Option[Collection]]

  /**
    * Obtains a collection from the repository by the id
    *
    * @param request includes the id of the collection to find
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Collection]
    * @throws PersistenceServiceException if exist some problem obtaining the collection
    */
  def findCollectionById(request: FindCollectionByIdRequest): CatsService[Option[Collection]]

  /**
    * Updates the data of an collection from the repository
    *
    * @param request includes the data to update the collection
    * @return an Int if the collection has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the collection
    */
  def updateCollection(request: UpdateCollectionRequest): CatsService[Int]

  /**
    * Bulk update of the data of some collections from the repository
    *
    * @param request includes the data to update the cards
    * @return a Seq[Int] if the cards has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the card
    */
  def updateCollections(request: UpdateCollectionsRequest): CatsService[Seq[Int]]

  /**
    * Obtains the android id from the repository
    *
    * @return an String with the android id
    * @throws AndroidIdNotFoundException if exist some problem obtaining the android id
    */
  def getAndroidId(implicit context: ContextSupport): CatsService[String]

  /**
    * Adds an user to the repository
    *
    * @param request includes the necessary data to create a new user in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.User
    * @throws PersistenceServiceException if exist some problem creating the user
    */
  def addUser(request: AddUserRequest): CatsService[User]

  /**
    * Deletes all users from the repository by the where clause
    *
    * @return an Int if the users has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the users
    */
  def deleteAllUsers(): CatsService[Int]

  /**
    * Deletes an user from the repository by the user
    *
    * @param request includes the user to delete
    * @return an Int if the user has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the user
    */
  def deleteUser(request: DeleteUserRequest): CatsService[Int]

  /**
    * Obtains all the users from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.User]
    * @throws PersistenceServiceException if exist some problem obtaining the users
    */
  def fetchUsers: CatsService[Seq[User]]

  /**
    * Obtains an user from the repository by the id
    *
    * @param request includes the user id  of the user to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.User]
    * @throws PersistenceServiceException if exist some problem obtaining the user
    */
  def findUserById(request: FindUserByIdRequest): CatsService[Option[User]]

  /**
    * Updates the data of an user from the repository
    *
    * @param request includes the data to update the user
    * @return an Int if the user has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the user
    */
  def updateUser(request: UpdateUserRequest): CatsService[Int]

  /**
    * Creates or updates dock app to the repository
    *
    * @param requests includes the necessary data to create a sequence of new dock apps in the repository
    * @throws PersistenceServiceException if exist some problem creating or updating the dock app
    */
  def createOrUpdateDockApp(requests: Seq[CreateOrUpdateDockAppRequest]): CatsService[Unit]

  /**
    * Deletes all dock apps from the repository by the where clause
    *
    * @return an Int if the dock apps has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the dock apps
    */
  def deleteAllDockApps(): CatsService[Int]

  /**
    * Deletes a dock app from the repository by the dock app
    *
    * @param request includes the dock app to delete
    * @return an Int if the dock app has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the dock app
    */
  def deleteDockApp(request: DeleteDockAppRequest): CatsService[Int]

  /**
    * Obtains all the dock apps from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp]
    * @throws PersistenceServiceException if exist some problem obtaining the dock apps
    */
  def fetchDockApps: CatsService[Seq[DockApp]]

  /**
    * Obtains iterable of dock apps from the repository
    *
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.IterableDockApps
    * @throws PersistenceServiceException if exist some problem obtaining the dock apps
    */
  def fetchIterableDockApps: CatsService[IterableDockApps]

  /**
    * Obtains a dock app from the repository by the id
    *
    * @param request includes the dock app id  of the dock app to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.DockApp]
    * @throws PersistenceServiceException if exist some problem obtaining the dock app
    */
  def findDockAppById(request: FindDockAppByIdRequest): CatsService[Option[DockApp]]

  /**
    * Adds an moment to the repository
    *
    * @param request includes the necessary data to create a new moment in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment
    * @throws PersistenceServiceException if exist some problem creating the moment
    */
  def addMoment(request: AddMomentRequest): CatsService[Moment]

  /**
    * Adds moments to the repository
    *
    * @param request includes the necessary data to create new moments in the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment]
    * @throws PersistenceServiceException if exist some problem creating the moments
    */
  def addMoments(request: Seq[AddMomentRequest]): CatsService[Seq[Moment]]

  /**
    * Deletes all moments from the repository by the where clause
    *
    * @return an Int if the moments has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the moments
    */
  def deleteAllMoments(): CatsService[Int]

  /**
    * Deletes an moment from the repository by the moment
    *
    * @param request includes the moment to delete
    * @return an Int if the moment has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the moment
    */
  def deleteMoment(request: DeleteMomentRequest): CatsService[Int]

  /**
    * Obtains all the moments from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment]
    * @throws PersistenceServiceException if exist some problem obtaining the moments
    */
  def fetchMoments: CatsService[Seq[Moment]]

  /**
    * Obtains an moment from the repository by the id
    *
    * @param request includes the moment id  of the moment to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Moment]
    * @throws PersistenceServiceException if exist some problem obtaining the moment
    */
  def findMomentById(request: FindMomentByIdRequest): CatsService[Option[Moment]]

  /**
    * Updates the data of an moment from the repository
    *
    * @param request includes the data to update the moment
    * @return an Int if the moment has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the moment
    */
  def updateMoment(request: UpdateMomentRequest): CatsService[Int]

  /**
    * Add a widget to the repository
    *
    * @param request includes the necessary data to create a new widget in the repository
    * @return the com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget
    * @throws PersistenceServiceException if exist some problem creating the widgets
    */
  def addWidget(request: AddWidgetRequest): CatsService[Widget]

  /**
    * Adds widgets to the repository
    *
    * @param request includes the necessary data to create new widgets in the repository
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget]
    * @throws PersistenceServiceException if exist some problem creating the widgets
    */
  def addWidgets(request: Seq[AddWidgetRequest]): CatsService[Seq[Widget]]

  /**
    * Deletes all widgets from the repository
    *
    * @return an Int if the widgets has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the widgets
    */
  def deleteAllWidgets(): CatsService[Int]

  /**
    * Deletes a widget from the repository
    *
    * @param request includes the widget to delete
    * @return an Int if the widget has been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the widget
    */
  def deleteWidget(request: DeleteWidgetRequest): CatsService[Int]

  /**
    * Deletes the widgets from the repository by the moment id
    *
    * @param momentId the id of the moment that contains the widgets
    * @return an Int if the widgets have been deleted correctly
    * @throws PersistenceServiceException if exist some problem deleting the widgets
    */
  def deleteWidgetsByMoment(momentId: Int): CatsService[Int]

  /**
    * Obtains all the widgets from the repository
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget]
    * @throws PersistenceServiceException if exist some problem obtaining the widgets
    */
  def fetchWidgets: CatsService[Seq[Widget]]

  /**
    * Obtains a widget from the repository by the id
    *
    * @param widgetId the widget id  of the widget to get
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget]
    * @throws PersistenceServiceException if exist some problem obtaining the widget
    */
  def findWidgetById(widgetId: Int): CatsService[Option[Widget]]

  /**
    * Obtains the widget from the repository by the appWidgetId
    *
    * @param appWidgetId the appWidgetId value
    * @return an Option[com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget]
    * @throws PersistenceServiceException if exist some problem obtaining the widget
    */
  def fetchWidgetByAppWidgetId(appWidgetId: Int): CatsService[Option[Widget]]

  /**
    * Obtains all widgets from the repository by the moment id
    *
    * @param momentId id of the moment
    * @return the Seq[com.fortysevendeg.ninecardslauncher.services.persistence.models.Widget]
    * @throws PersistenceServiceException if exist some problem obtaining the widgets
    */
  def fetchWidgetsByMoment(momentId: Int): CatsService[Seq[Widget]]

  /**
    * Updates the data of a widget from the repository
    *
    * @param request includes the data to update the widget
    * @return an Int if the widget has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the widget
    */
  def updateWidget(request: UpdateWidgetRequest): CatsService[Int]

  /**
    * Bulk update of the data of some widgets from the repository
    *
    * @param request includes the data to update the widgets
    * @return a Seq[Int] if the widgets has been updated correctly
    * @throws PersistenceServiceException if exist some problem updating the widget
    */
  def updateWidgets(request: UpdateWidgetsRequest): CatsService[Seq[Int]]

}
