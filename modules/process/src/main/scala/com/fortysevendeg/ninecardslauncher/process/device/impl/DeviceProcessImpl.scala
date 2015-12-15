package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutsServices
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetsServices

class DeviceProcessImpl(
  val appsService: AppsServices,
  val apiServices: ApiServices,
  val persistenceServices: PersistenceServices,
  val shortcutsServices: ShortcutsServices,
  val contactsServices: ContactsServices,
  val imageServices: ImageServices,
  val widgetsServices: WidgetsServices,
  val callsServices: CallsServices)
  extends DeviceProcess
  with DeviceProcessDependencies
  with AppsDeviceProcessImpl
  with ContactsDeviceProcessImpl
  with DockAppsDeviceProcessImpl
  with LastCallsDeviceProcessImpl
  with ResetProcessImpl
  with ShorcutsDeviceProcessImpl
  with WidgetsDeviceProcessImpl
  with ImplicitsDeviceException
  with ImplicitsImageExceptions
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with DeviceConversions {

  override def resetSavedItems() = super.resetSavedItems()

  override def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) = super.getSavedApps(orderBy)

  override def saveInstalledApps(implicit context: ContextSupport) = super.saveInstalledApps

  override def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport) = super.getIterableApps(orderBy)

  override def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport) =
    super.getIterableAppsByKeyWord(keyword, orderBy)

  override def saveApp(packageName: String)(implicit context: ContextSupport) = super.saveApp(packageName)

  override def deleteApp(packageName: String)(implicit context: ContextSupport) = super.deleteApp(packageName)

  override def updateApp(packageName: String)(implicit context: ContextSupport) = super.updateApp(packageName)

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) =
    super.createBitmapsFromPackages(packages)

  override def getAvailableShortcuts(implicit context: ContextSupport) = super.getAvailableShortcuts

  override def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) =
    super.saveShortcutIcon(name, bitmap)

  override def getFavoriteContacts(implicit context: ContextSupport) = super.getFavoriteContacts

  override def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    super.getContacts(filter)

  override def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    super.getIterableContacts(filter)

  override def getContact(lookupKey: String)(implicit context: ContextSupport) = super.getContact(lookupKey)

  override def getWidgets(implicit context: ContextSupport) = super.getWidgets

  override def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport) =
    super.getIterableContactsByKeyWord(keyword)

  override def getLastCalls(implicit context: ContextSupport) = super.getLastCalls

  override def saveDockApp(packageName: String, intent: NineCardIntent, imagePath: String, position: Int)(implicit context: ContextSupport) =
    super.saveDockApp(packageName, intent, imagePath, position)

}
