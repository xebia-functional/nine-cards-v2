package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.types.{Misc, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, IterableApps, IterableContacts, LastCallsContact}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.persistence.{AddAppRequest, ImplicitsPersistenceServiceExceptions, PersistenceServiceException, PersistenceServices}
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
  with LastCallsDeviceProcessImpl
  with ResetProcessImpl
  with ShorcutsDeviceProcessImpl
  with WidgetsDeviceProcessImpl
  with ImplicitsDeviceException
  with ImplicitsImageExceptions
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with DeviceConversions {

  override def resetSavedItems() = super.resetSavedItems

  override def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) = super.getSavedApps(orderBy)

  override def saveInstalledApps(implicit context: ContextSupport) = super.saveInstalledApps

  override def getIterableApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      iter <- persistenceServices.fetchIterableApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]

  override def saveInstalledApps(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      installedApps <- appsService.getInstalledApplications
      googlePlayPackagesResponse <- apiServices.googlePlayPackages(installedApps map (_.packageName))(requestConfig)
      appPaths <- createBitmapsFromAppPackage(toAppPackageSeq(installedApps))
      apps = installedApps map { app =>
        val path = appPaths.find { path =>
          path.packageName.equals(app.packageName) && path.className.equals(app.className)
        } map (_.path)
        val category = googlePlayPackagesResponse.packages find(_.app.docid == app.packageName) flatMap (_.app.details.appDetails.appCategory.headOption)
        toAddAppRequest(app, (category map (NineCardCategory(_))).getOrElse(Misc), path.getOrElse(""))
      }
      _ <- addApps(apps)
    } yield ()).resolve[AppException]

  override def saveApp(packageName: String)(implicit context: ContextSupport) = super.saveApp(packageName)

  override def deleteApp(packageName: String)(implicit context: ContextSupport) = super.deleteApp(packageName)

  override def updateApp(packageName: String)(implicit context: ContextSupport) = super.updateApp(packageName)

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) = super.createBitmapsFromPackages(packages)

  override def getAvailableShortcuts(implicit context: ContextSupport) = super.getAvailableShortcuts

  override def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) = super.saveShortcutIcon(name, bitmap)

  override def getFavoriteContacts(implicit context: ContextSupport) = super.getFavoriteContacts

  override def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) = super.getContacts(filter)

  override def getContact(lookupKey: String)(implicit context: ContextSupport) = super.getContact(lookupKey)

  override def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      iter <- filter match {
        case AllContacts => contactsServices.getIterableContacts
        case FavoriteContacts => contactsServices.getIterableFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getIterableContactsWithPhone
      }
    } yield new IterableContacts(iter)).resolve[ContactException]

  override def getContact(lookupKey: String)(implicit context: ContextSupport) =
    (for {
      contact <- contactsServices.findContactByLookupKey(lookupKey)
    } yield toContact(contact)).resolve[ContactException]
  override def getWidgets(implicit context: ContextSupport) = super.getWidgets

  override def getLastCalls(implicit context: ContextSupport) = super.getLastCalls

}
