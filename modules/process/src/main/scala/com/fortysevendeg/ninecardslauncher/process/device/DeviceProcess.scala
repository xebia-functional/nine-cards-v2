package com.fortysevendeg.ninecardslauncher.process.device

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.device.models._

trait DeviceProcess {

  /**
    * Delete all apps, cards, collections and dockApps from the repository
    * @throws ResetException if exist some problem deleting the apps, cards, collections and dockApps
    */
  def resetSavedItems(): ServiceDef2[Unit, ResetException]

  /**
   * Get the saved apps from the database
   * @param orderBy indicates the order to fetch the apps
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.App]
   * @throws AppException if exist some problem to get the apps
   */
  def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport): ServiceDef2[Seq[App], AppException]

  /**
    * Get iterable of saved apps from the database
    * @param orderBy indicates the order to fetch the apps
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps contains
    *         information about the app
    * @throws AppException if exist some problem to get the apps
    */
  def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport): ServiceDef2[IterableApps, AppException]

  /**
    * Returns the number of times the first letter of an app is repeated alphabetically filtered by parameter
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.TermCounter] contains
    *         information about the times is repeated an apps
    * @throws AppException if exist some problem to get the contacts
    */
  def getTermCountersForApps(orderBy: GetAppOrder)(implicit context: ContextSupport): ServiceDef2[Seq[TermCounter], AppException]

  /**
    * Get the iterable apps by keyword.
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableApps contains
    *         information about the app
    * @throws AppException if exist some problem to get the contacts
    */
  def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport): ServiceDef2[IterableApps, AppException]

  /**
   * Create the bitmaps from a sequence of packages
   * @throws CreateBitmapException if exist some problem creating the bitmaps
   */
  def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport): ServiceDef2[Unit, CreateBitmapException]

  /**
   * Get the available applications that contain shortcuts creating Intents and Drawables necessaries for UI actions
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut] contains
   *         information about shortcut with the Intents and Drawables for UI actions
   * @throws ShortcutException if exist some problem to get the shortcuts in the cell phone
   */
  def getAvailableShortcuts(implicit context: ContextSupport): ServiceDef2[Seq[Shortcut], ShortcutException]

  /**
   * Save shortcut icon from bitmap
   * @return the String contains the path where the icon was stored
   * @throws ShortcutException if exist some problem storing icon
   */
  def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport): ServiceDef2[String, ShortcutException]

  /**
   * Get the favorite contacts if they exist and fill all their data
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Contact] contains
   *         information about the contact including its ContactInfo (if it exists)
   * @throws ContactException if exist some problem to get the favorite contacts
   */
  def getFavoriteContacts(implicit context: ContextSupport): ServiceDef2[Seq[Contact], ContactException]

  /**
   * Get the contacts by filter selected sorted without data. The filters are: all contacts, favorite contacts
   * and contacts with phone number
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Contact] contains
   *         information about the contact
   * @throws ContactException if exist some problem to get the contacts
   */
  def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): ServiceDef2[Seq[Contact], ContactException]

  /**
    * Returns the number of times the first letter of a contact is repeated alphabetically filtered by parameter
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.TermCounter] contains
    *         information about the times is repeated a contacts
    * @throws ContactException if exist some problem to get the contacts
    */
  def getTermCountersForContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): ServiceDef2[Seq[TermCounter], ContactException]

  /**
    * Get the iterable contacts by filter selected sorted without data. The filters are: all contacts, favorite contacts
    * and contacts with phone number
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts contains
    *         information about the contact
    * @throws ContactException if exist some problem to get the contacts
    */
  def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport): ServiceDef2[IterableContacts, ContactException]

  /**
   * Get the contact and fill all their data
   * @return the com.fortysevendeg.ninecardslauncher.process.device.models.Contact contains
   *         information about the contact
   * @throws ContactException if exist some problem to get the contacts
   */
  def getContact(lookupKey: String)(implicit context: ContextSupport): ServiceDef2[Contact, ContactException]

  /**
    * Get the iterable contacts by keyword.
    * @return the com.fortysevendeg.ninecardslauncher.process.device.models.IterableContacts contains
    *         information about the contact
    * @throws ContactException if exist some problem to get the contacts
    */
  def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport): ServiceDef2[IterableContacts, ContactException]

  /**
   * Get the installed apps and store them in the repository
   * @throws AppException if exist some problem to get the apps or storing them
   */
  def saveInstalledApps(implicit context: ContextSupport): ServiceDef2[Unit, AppException]

  /**
   * Get an installed app and store it in the repository
   * @param packageName the packageName of the app to save
   * @throws AppException if exist some problem to get the app or storing it
   */
  def saveApp(packageName: String)(implicit context: ContextSupport): ServiceDef2[Unit, AppException]

  /**
   * Delete an app from the repository
   * @param packageName the packageName of the app to delete
   * @throws AppException if exist some problem deleting the app
   */
  def deleteApp(packageName: String)(implicit context: ContextSupport): ServiceDef2[Unit, AppException]

  /**
   * Get the contact and fill all their data
   * @param packageName the packageName of the app to update
   * @throws AppException if exist some problem to get the app or updating it
   */
  def updateApp(packageName: String)(implicit context: ContextSupport): ServiceDef2[Unit, AppException]

  /**
   * Get the widgets available on the phone
   * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Widget]
   * @throws WidgetException if exist some problem to get the widgets
   */
  def getWidgets(implicit context: ContextSupport): ServiceDef2[Seq[Widget], WidgetException]

  /**
    * Get the last calls available on the phone
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.Call]
    * @throws CallException if exist some problem to get the last calls
    */
  def getLastCalls(implicit context: ContextSupport): ServiceDef2[Seq[LastCallsContact], CallException]

  /**
    * Get an installed app and store it in the repository
    * @param packageName the packageName of the dock app to save
    * @param intent the NineCardIntent of the dock app
    * @param imagePath the path of the image of the dock app
    * @param position the position in the dock
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def saveDockApp(packageName:String, intent: NineCardIntent, imagePath: String, position: Int): ServiceDef2[Unit, DockAppException]

  /**
    * Get the docks apps available for user
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.DockApp]
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def getDockApps: ServiceDef2[Seq[DockApp], DockAppException]

  /**
    * Delete all dock apps in database
    * @return the Seq[com.fortysevendeg.ninecardslauncher.process.device.models.DockApp]
    * @throws DockAppException if exist some problem to get the app or storing it
    */
  def deleteAllDockApps: ServiceDef2[Unit, DockAppException]

}
