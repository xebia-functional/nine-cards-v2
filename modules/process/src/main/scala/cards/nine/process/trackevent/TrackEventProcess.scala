package cards.nine.process.trackevent

import cards.nine.commons.services.TaskService._
import cards.nine.models.types.{Category, MomentCategory}

trait TrackEventProcess {

  /* CollectionDetailScreen */

  /**
    * Tracks when the user opens an application from collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Tracks when the user adds an app to collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def addAppToCollection(packageName: String, category: Category): TaskService[Unit]

  /**
    * Tracks when the user removes an application in collection
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def removeFromCollection(packageName: String, category: Category): TaskService[Unit]

  /* LauncherScreen */

  /**
    * Tracks when the user opens an application from app drawer
    *
    * @param packageName package name of app
    * @param category category of event
    */
  def openAppFromAppDrawer(packageName: String, category: Category): TaskService[Unit]

  /* ProfileScreen */

  /**
    * Tracks when the user enters in the Accounts tab in Profile
    *
    */
  def showAccountsContent(): TaskService[Unit]

  /**
    * Tracks when the user copies a configuration
    *
    */
  def copyConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user synchronizes the current configuration
    *
    */
  def synchronizeConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user synchronizes the configuration name
    *
    */
  def changeConfigurationName(): TaskService[Unit]

  /**
    * Tracks when the user deletes a configuration
    *
    */
  def deleteConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user enters in the Publications tab in Profile
    *
    */
  def showPublicationsContent(): TaskService[Unit]

  /**
    * Tracks when the user adds a collection from the Publications tab
    *
    * @param collectionName name of the collection
    */
  def addToMyCollectionsFromProfile(collectionName: String) : TaskService[Unit]

  /**
    * Tracks when the user shares a collection from the Publications tab
    *
    * @param collectionName name of the collection
    */
  def shareCollectionFromProfile(collectionName: String) : TaskService[Unit]

  /* WidgetScreen */

  /**
    * Tracks when the user adds a widget in moment
    *
    * @param packageName package name of app
    * @param className class of the widget
    * @param moment moment where it's added
    */
  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory): TaskService[Unit]
}
