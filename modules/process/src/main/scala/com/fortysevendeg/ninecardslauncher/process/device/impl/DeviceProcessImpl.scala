package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
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

  override def getIterableAppsByKeyWord(keyword: String, orderBy: GetAppOrder)(implicit context: ContextSupport)  =
    (for {
      iter <- persistenceServices.fetchIterableAppsByKeyword(keyword, toFetchAppOrder(orderBy), orderBy.ascending)
    } yield new IterableApps(iter)).resolve[AppException]
  override def saveApp(packageName: String)(implicit context: ContextSupport) = super.saveApp(packageName)

  override def deleteApp(packageName: String)(implicit context: ContextSupport) = super.deleteApp(packageName)

  override def updateApp(packageName: String)(implicit context: ContextSupport) = super.updateApp(packageName)

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) = super.createBitmapsFromPackages(packages)

  override def getAvailableShortcuts(implicit context: ContextSupport) = super.getAvailableShortcuts

  override def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) = super.saveShortcutIcon(name, bitmap)

  override def getFavoriteContacts(implicit context: ContextSupport) = super.getFavoriteContacts

  override def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) = super.getContacts(filter)

  override def getIterableContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) = super.getIterableContacts(filter)

  override def getContact(lookupKey: String)(implicit context: ContextSupport) = super.getContact(lookupKey)

  override def getWidgets(implicit context: ContextSupport) = super.getWidgets

  override def getIterableContactsByKeyWord(keyword: String)(implicit context: ContextSupport)  =
    (for {
      iter <- contactsServices.getIterableContactsByKeyword(keyword)
    } yield new IterableContacts(iter)).resolve[ContactException]

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
    (combinedContacts map { combinedContact =>
      val (lastCallsContact, maybeContact) = combinedContact
      maybeContact map { contact =>
        lastCallsContact.copy(
          lookupKey = Some(contact.lookupKey),
          photoUri = Some(contact.photoUri)
        )
      } getOrElse lastCallsContact
    }).sortWith(_.lastCallDate > _.lastCallDate)

  private[this] def getAppCategory(packageName: String)(implicit context: ContextSupport) =
    for {
      requestConfig <- apiUtils.getRequestConfig
      appCategory = apiServices.googlePlayPackage(packageName)(requestConfig).run.run match {
        case Answer(g) => (g.app.details.appDetails.appCategory map (NineCardCategory(_))).headOption.getOrElse(Misc)
        case _ => Misc
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
  override def getLastCalls(implicit context: ContextSupport) = super.getLastCalls

}
