package com.fortysevendeg.ninecardslauncher.process.device

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService.CatsService
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.DockType
import com.fortysevendeg.ninecardslauncher.process.device.models._

trait DeviceProcess {

  /**
    * Delete all apps, cards, collections and dockApps from the repository
    * @throws ResetException if exist some problem deleting the apps, cards, collections and dockApps
    */
  def resetSavedItems(): CatsService[Unit]

  /**
   * Get the saved apps from the database
   * @param orderBy indicates the order to fetch the apps
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.App]
   * @throws AppException if exist some problem to get the apps
   */
  def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport): CatsService[Seq[App]]

  /**
    * Get iterable of saved apps from the database
    * @param orderBy indicates the order to fetch the apps
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps contains
    *         information about the app
    * @throws AppException if exist some problem to get the apps
    */
  def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport): CatsService[IterableApps]

  /**
    * Get iterable by category of saved apps from the database
    * @param category indicates the category
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps contains
    *         information about the app
    * @throws AppException if exist some problem to get the apps
    */
  def getIterableAppsByCategory(category: String)(implicit context: ContextSupport): CatsService[IterableApps]

  /**
    * Returns the number of times the first letter of an app is repeated alphabetically filtered by parameter
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.TermCounter] contains
    *         information about the times is repeated an apps
    * @throws AppException if exist some problem to get the contacts
    */
  def getTermCountersForApps(orderBy: GetAppOrder)(implicit context: ContextSupport): CatsService[Seq[TermCounter]]

  /**
    * Get the iterable apps by keyword.
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps contains
    *         information about the app
    * @throws AppException if exist some problem to get the contacts
    */
  def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport): CatsService[IterableApps]

  /**
   * Get the available applications that contain shortcuts creating Intents and Drawables necessaries for UI actions
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut] contains
   *         information about shortcut with the Intents and Drawables for UI actions
   * @throws ShortcutException if exist some problem to get the shortcuts in the cell phone
   */
  def getAvailableShortcuts(implicit context: ContextSupport): CatsService[Seq[Shortcut]]

  /**
   * Save shortcut icon from bitmap
   * @return the String contains the path where the icon was stored
   * @throws ShortcutException if exist some problem storing icon
   */
  def saveShortcutIcon(bitmap: Bitmap, iconResize: Option[IconResize] = None)(implicit context: ContextSupport): CatsService[String]

  /**
   * Get the favorite contacts if they exist and fill all their data
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Contact] contains
   *         information about the contact including its ContactInfo (if it exists)
   * @throws ContactException if exist some problem to get the favorite contacts
   */
  def getFavoriteContacts(implicit context: ContextSupport): CatsService[Seq[Contact]]

  /**
   * Get the contacts by filter selected sorted without data. The filters are: all contacts, favorite contacts
   * and contacts with phone number
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Contact] contains
   *         information about the contact
   * @throws ContactException if exist some problem to get the contacts
   */
  def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): CatsService[Seq[Contact]]

  /**
    * Returns the number of times the first letter of a contact is repeated alphabetically filtered by parameter
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.TermCounter] contains
    *         information about the times is repeated a contacts
    * @throws ContactException if exist some problem to get the contacts
    */
  def getTermCountersForContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): CatsService[Seq[TermCounter]]

  /**
    * Get the iterable contacts by filter selected sorted without data. The filters are: all contacts, favorite contacts
    * and contacts with phone number
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts contains
    *         information about the contact
    * @throws ContactException if exist some problem to get the contacts
    */
  def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): CatsService[IterableContacts]

  /**
   * Get the contact and fill all their data
   * @return the com.fortysevendeg.ninecardslauncher.process.device.models.Contact contains
   *         information about the contact
   * @throws ContactException if exist some problem to get the contacts
   */
  def getContact(lookupKey: String)(implicit context: ContextSupport): CatsService[Contact]

  /**
    * Get the iterable contacts by keyword.
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts contains
    *         information about the contact
    * @throws ContactException if exist some problem to get the contacts
    */
  def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport): CatsService[IterableContacts]

  /**
   * Get the installed apps and store them in the repository
   * @throws AppException if exist some problem to get the apps or storing them
   */
  def saveInstalledApps(implicit context: ContextSupport): CatsService[Unit]

  /**
   * Get an installed app and store it in the repository
   * @param packageName the packageName of the app to save
   * @throws AppException if exist some problem to get the app or storing it
   */
  def saveApp(packageName: String)(implicit context: ContextSupport): CatsService[Unit]

  /**
   * Delete an app from the repository
   * @param packageName the packageName of the app to delete
   * @throws AppException if exist some problem deleting the app
   */
  def deleteApp(packageName: String)(implicit context: ContextSupport): CatsService[Unit]

  /**
   * Get the contact and fill all their data
   * @param packageName the packageName of the app to update
   * @throws AppException if exist some problem to get the app or updating it
   */
  def updateApp(packageName: String)(implicit context: ContextSupport): CatsService[Unit]

  /**
   * Get the widgets available on the phone
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.AppsWithWidgets]
   * @throws WidgetException if exist some problem to get the widgets
   */
  def getWidgets(implicit context: ContextSupport): CatsService[Seq[AppsWithWidgets]]

  /**
    * Get the last calls available on the phone
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Call]
    * @throws CallException if exist some problem to get the last calls
    */
  def getLastCalls(implicit context: ContextSupport): CatsService[Seq[LastCallsContact]]

  /**
    * Generate the docks apps available for user
    * @param size of the dock apps needed
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.DockApp]
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def generateDockApps(size: Int)(implicit context: ContextSupport): CatsService[Seq[DockApp]]

  /**
    * Create or update a dock app
    * @param name name of dock app
    * @param dockType dock type
    * @param intent action
    * @param imagePath image
    * @param position new position
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def createOrUpdateDockApp(name: String, dockType: DockType, intent: NineCardIntent, imagePath: String, position: Int): CatsService[Unit]

  /**
    * Creates DockApps from some already formed and given DockApps
    *
    * @param items the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.DockApp] of DockApps
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.SaveDockAppRequest]
    * @throws DockAppException if there was an error creating the moments' collections
    */
  def saveDockApps(items: Seq[SaveDockAppRequest]): CatsService[Seq[DockApp]]

  /**
    * Get the docks apps available for user
    *
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.DockApp]
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def getDockApps: CatsService[Seq[DockApp]]

  /**
    * Delete all dock apps in database
    *
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def deleteAllDockApps(): CatsService[Unit]

  /**
    * Get all configured networks sorted by name
    *
    * @return Seq[String] that contains all SSIDs
    * @throws WidgetException if exist some problem to get the widgets
    */
  def getConfiguredNetworks(implicit context: ContextSupport): CatsService[Seq[String]]

}
