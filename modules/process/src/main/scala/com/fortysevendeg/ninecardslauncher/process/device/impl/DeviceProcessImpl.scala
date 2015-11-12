package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.commons.NineCardCategories._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact}
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call
import com.fortysevendeg.ninecardslauncher.services.contacts.models.{Contact => ServicesContact}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServices, ImplicitsPersistenceServiceExceptions, AddAppRequest, PersistenceServiceException}
import com.fortysevendeg.ninecardslauncher.services.persistence.models.App
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutsServices
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetsServices
import rapture.core.Answer

import scalaz.concurrent.Task

class DeviceProcessImpl(
  appsService: AppsServices,
  apiServices: ApiServices,
  persistenceServices: PersistenceServices,
  shortcutsServices: ShortcutsServices,
  contactsServices: ContactsServices,
  imageServices: ImageServices,
  widgetsServices: WidgetsServices,
  callsServices: CallsServices)
  extends DeviceProcess
  with ImplicitsDeviceException
  with ImplicitsImageExceptions
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with DeviceConversions {

  val apiUtils = new ApiUtils(persistenceServices)

  override def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) =
    (for {
      apps <- persistenceServices.fetchApps(toFetchAppOrder(orderBy), orderBy.ascending)
    } yield apps map toApp).resolve[AppException]

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
        toAddAppRequest(app, category.getOrElse(misc), path.getOrElse(""))
      }
      _ <- addApps(apps)
    } yield ()).resolve[AppException]

  override def saveApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsService.getApplication(packageName)
      appCategory <- getAppCategory(packageName)
      appPackagePath <- imageServices.saveAppIcon(toAppPackage(app))
      _ <- persistenceServices.addApp(toAddAppRequest(app, appCategory, appPackagePath.path))
    } yield ()).resolve[AppException]

  override def deleteApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      _ <- persistenceServices.deleteAppByPackage(packageName)
    } yield ()).resolve[AppException]

  override def updateApp(packageName: String)(implicit context: ContextSupport) =
    (for {
      app <- appsService.getApplication(packageName)
      Some(appPersistence) <- persistenceServices.findAppByPackage(packageName)
      appCategory <- getAppCategory(packageName)
      appPackagePath <- imageServices.saveAppIcon(toAppPackage(app))
      _ <- persistenceServices.updateApp(toUpdateAppRequest(appPersistence.id, app, appCategory, appPackagePath.path))
    } yield ()).resolve[AppException]

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) =
    (for {
      requestConfig <- apiUtils.getRequestConfig
      response <- apiServices.googlePlayPackages(packages)(requestConfig)
      _ <- createBitmapsFromAppWebSite(toAppWebSiteSeq(response.packages))
    } yield ()).resolve[CreateBitmapException]

  override def getAvailableShortcuts(implicit context: ContextSupport) =
    (for {
      shortcuts <- shortcutsServices.getShortcuts
    } yield toShortcutSeq(shortcuts)).resolve[ShortcutException]

  override def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) =
    (for {
      saveBitmapPath <- imageServices.saveBitmap(SaveBitmap(name, bitmap))
    } yield saveBitmapPath.path).resolve[ShortcutException]

  override def getFavoriteContacts(implicit context: ContextSupport) =
    (for {
      favoriteContacts <- contactsServices.getFavoriteContacts
      filledFavoriteContacts <- fillContacts(favoriteContacts)
    } yield toContactSeq(filledFavoriteContacts)).resolve[ContactException]

  override def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) =
    (for {
      contacts <- filter match {
        case AllContacts => contactsServices.getContacts
        case FavoriteContacts => contactsServices.getFavoriteContacts
        case ContactsWithPhoneNumber => contactsServices.getContactsWithPhone
      }
    } yield toContactSeq(contacts)).resolve[ContactException]

  override def getContact(lookupKey: String)(implicit context: ContextSupport) =
    (for {
      contact <- contactsServices.findContactByLookupKey(lookupKey)
    } yield toContact(contact)).resolve[ContactException]

  override def getWidgets(implicit context: ContextSupport) =
    (for {
      widgets <- widgetsServices.getWidgets
    } yield widgets map toWidget).resolve[WidgetException]

  override def getLastCalls(implicit context: ContextSupport) =
    (for {
      lastCalls <- callsServices.getLastCalls
      simpleGroupCalls <- simpleGroupCalls(lastCalls)
      combinedContacts <- getCombinedContacts(simpleGroupCalls)
    } yield fillCombinedContacts(combinedContacts)).resolve[CallException]

  private[this] def simpleGroupCalls(lastCalls: Seq[Call]): ServiceDef2[Seq[LastCallsContact], CallException] = Service {
    Task {
      CatchAll[CallException] {
        (lastCalls groupBy (_.number) map { case (k, v) => toSimpleLastCallsContact(k, v) }).toSeq
      }
    }
  }

  private[this] def getCombinedContacts(items: Seq[LastCallsContact]):
  ServiceDef2[Seq[(LastCallsContact, Option[Contact])], ContactsServiceException] = Service {
    val tasks = items map (item => combineContact(item).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[ContactsServiceException](list.collect { case Answer(combinedContact) => combinedContact }))
  }

  private[this] def combineContact(lastCallsContact: LastCallsContact):
  ServiceDef2[(LastCallsContact, Option[Contact]), ContactsServiceException] =
    for {
      contact <- contactsServices.fetchContactByPhoneNumber(lastCallsContact.number)
    } yield (lastCallsContact, contact map toContact)

  private[this] def fillCombinedContacts(combinedContacts: Seq[(LastCallsContact, Option[Contact])]): Seq[LastCallsContact] =
    combinedContacts map { combinedContact =>
      combinedContact._2 map { contact =>
        combinedContact._1.copy(
          lookupKey = Some(contact.lookupKey),
          photoUri = Some(contact.photoUri)
        )
      } getOrElse combinedContact._1
    }

  private[this] def getAppCategory(packageName: String)(implicit context: ContextSupport) =
    for {
      requestConfig <- apiUtils.getRequestConfig
      appCategory = apiServices.googlePlayPackage(packageName)(requestConfig).run.run match {
        case Answer(g) => g.app.details.appDetails.appCategory.headOption.getOrElse(misc)
        case _ => misc
      }
    } yield appCategory

  private[this] def addApps(items: Seq[AddAppRequest]):
  ServiceDef2[Seq[App], PersistenceServiceException] = Service {
    val tasks = items map (persistenceServices.addApp(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[PersistenceServiceException](list.collect { case Answer(app) => app }))
  }

  private[this] def createBitmapsFromAppPackage(apps: Seq[AppPackage])(implicit context: ContextSupport):
  ServiceDef2[Seq[AppPackagePath], BitmapTransformationException] = Service {
    val tasks = apps map (imageServices.saveAppIcon(_).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[BitmapTransformationException](list.collect { case Answer(app) => app }))
  }

  private[this] def createBitmapsFromAppWebSite(apps: Seq[AppWebsite])(implicit context: ContextSupport):
  ServiceDef2[Seq[AppWebsitePath], BitmapTransformationException] = Service {
    val tasks = apps map imageServices.saveAppIcon map (_.run)
    Task.gatherUnordered(tasks) map (list => CatchAll[BitmapTransformationException](list.collect { case Answer(app) => app }))
  }

  // TODO Change when ticket is finished (9C-235 - Fetch contacts from several lookup keys)
  private[this] def fillContacts(contacts: Seq[ServicesContact]) = Service {
    val tasks = contacts map (c => contactsServices.findContactByLookupKey(c.lookupKey).run)
    Task.gatherUnordered(tasks) map (list => CatchAll[ContactsServiceException](list.collect { case Answer(contact) => contact }))
  }.resolve[ContactException]

}
