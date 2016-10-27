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
    * Tracks when the user enters in logs out
    *
    */
  def logout(): TaskService[Unit]

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
  def addToMyCollectionsFromProfile(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user shares a collection from the Publications tab
    *
    * @param collectionName name of the collection
    */
  def shareCollectionFromProfile(collectionName: String): TaskService[Unit]

  /**
    * Tracks when the user enters in the Subscriptions tab in Profile
    *
    */
  def showSubscriptionsContent(): TaskService[Unit]

  /**
    * Tracks when the user subscribes to a collection in the Subscriptions tab
    *
    * @param sharedCollectionId of the collection
    */
  def subscribeToCollection(sharedCollectionId: String): TaskService[Unit]

  /**
    * Tracks when the user unsubscribes from a collection in the Subscriptions tab
    *
    * @param sharedCollectionId of the collection
    */
  def unsubscribeFromCollection(sharedCollectionId: String): TaskService[Unit]

  /* WidgetScreen */

  /**
    * Tracks when the user adds a widget in moment
    *
    * @param packageName package name of app
    * @param className class of the widget
    * @param moment moment where it's added
    */
  def addWidgetToMoment(packageName: String, className: String, moment: MomentCategory): TaskService[Unit]


  /* WizardScreen */

  /**
    * Tracks when the user choose an account in the Wizard's start screen
    *
    */
  def chooseAccount(): TaskService[Unit]

  /**
    * Tracks when the user choose a configuration in the Wizard's configuration screen
    *
    */
  def chooseNewConfiguration(): TaskService[Unit]

  /**
    * Tracks when the user choose the current device in the Wizard's configuration screen
    *
    */
  def chooseCurrentDevice(): TaskService[Unit]

  /**
    * Tracks when the user choose the other device in the Wizard's configuration screen
    *
    */
  def chooseOtherDevices(): TaskService[Unit]

  /**
    * Tracks when the user choose all apps in the Wizard's collections screen
    *
    */
  def chooseAllApps(): TaskService[Unit]

  /**
    * Tracks when the user choose best nine apps in the Wizard's collections screen
    *
    */
  def chooseBestNineApps(): TaskService[Unit]

  /**
    * Tracks when the user choose home moment in the Wizard's moments screen
    *
    */
  def chooseHome(): TaskService[Unit]

  /**
    * Tracks when the user choose sets the wifi for home in the Wizard's moments screen
    *
    */
  def chooseHomeWifi(): TaskService[Unit]

  /**
    * Tracks when the user choose work moment in the Wizard's moments screen
    *
    */
  def chooseWork(): TaskService[Unit]

  /**
    * Tracks when the user choose sets the wifi for work in the Wizard's moments screen
    *
    */
  def chooseWorkWifi(): TaskService[Unit]

  /**
    * Tracks when the user choose study moment in the Wizard's moments screen
    *
    */
  def chooseStudy(): TaskService[Unit]

  /**
    * Tracks when the user choose sets the wifi for study in the Wizard's moments screen
    *
    */
  def chooseStudyWifi(): TaskService[Unit]

  /**
    * Tracks when the user choose music moment in the Wizard's other moments screen
    *
    */
  def chooseMusic(): TaskService[Unit]

  /**
    * Tracks when the user choose car moment in the Wizard's other moments screen
    *
    */
  def chooseCar(): TaskService[Unit]

  /**
    * Tracks when the user choose sport moment in the Wizard's other moments screen
    *
    */
  def chooseSport(): TaskService[Unit]
}
