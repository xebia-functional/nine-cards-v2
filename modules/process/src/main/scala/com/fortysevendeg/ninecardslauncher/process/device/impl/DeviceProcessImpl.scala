package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.device.models.{Contact, LastCallsContact}
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.calls.CallsServices
import com.fortysevendeg.ninecardslauncher.services.calls.models.Call
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices, ImplicitsContactsServiceExceptions}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{ImplicitsPersistenceServiceExceptions, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.ShortcutsServices
import com.fortysevendeg.ninecardslauncher.services.widgets.WidgetsServices
import rapture.core.Answer

import scalaz.concurrent.Task

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
  with AppDeviceProcessImpl
  with ShorcutDeviceProcessImpl
  with ContactsDeviceProcessImpl
  with ImplicitsDeviceException
  with ImplicitsImageExceptions
  with ImplicitsPersistenceServiceExceptions
  with ImplicitsContactsServiceExceptions
  with DeviceConversions {

  override def getSavedApps(orderBy: GetAppOrder)(implicit context: ContextSupport) = super.getSavedApps(orderBy)

  override def saveInstalledApps(implicit context: ContextSupport) = super.saveInstalledApps

  override def saveApp(packageName: String)(implicit context: ContextSupport) = super.saveApp(packageName)

  override def deleteApp(packageName: String)(implicit context: ContextSupport) = super.deleteApp(packageName)

  override def updateApp(packageName: String)(implicit context: ContextSupport) = super.updateApp(packageName)

  override def createBitmapsFromPackages(packages: Seq[String])(implicit context: ContextSupport) = super.createBitmapsFromPackages(packages)

  override def getAvailableShortcuts(implicit context: ContextSupport) = super.getAvailableShortcuts

  override def saveShortcutIcon(name: String, bitmap: Bitmap)(implicit context: ContextSupport) = super.saveShortcutIcon(name, bitmap)

  override def getFavoriteContacts(implicit context: ContextSupport) = super.getFavoriteContacts

  override def getContacts(filter: ContactsFilter = AllContacts)(implicit context: ContextSupport) = super.getContacts(filter)

  override def getContact(lookupKey: String)(implicit context: ContextSupport) = super.getContact(lookupKey)

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

}
