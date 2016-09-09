package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppDockType
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.calls.{CallsServices, CallsServicesException}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ShortcutServicesException, ShortcutsServices}
import com.fortysevendeg.ninecardslauncher.services.widgets.{WidgetServicesException, WidgetsServices}
import com.fortysevendeg.ninecardslauncher.services.wifi.WifiServices
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait DeviceProcessSpecification
  extends Specification
  with Mockito {

  val appInstalledException = AppsInstalledException("")

  val apiServiceException = ApiServiceException("")

  val persistenceServiceException = PersistenceServiceException("")

  val bitmapTransformationException = BitmapTransformationException("")

  val shortcutServicesException = ShortcutServicesException("")

  val contactsServicesException = ContactsServiceException("")

  val fileServicesException = FileException("")

  val widgetsServicesException = WidgetServicesException("")

  val callsServicesException = CallsServicesException("")

  trait DeviceProcessScope
    extends Scope
    with DeviceProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val mockPackageManager = mock[PackageManager]
    mockPackageManager.getActivityIcon(any[ComponentName]) returns javaNull

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mockPackageManager
    contextSupport.getResources returns resources

    val mockBitmap = mock[Bitmap]

    val mockAppsServices = mock[AppsServices]

    val mockApiServices = mock[ApiServices]

    val mockShortcutsServices = mock[ShortcutsServices]

    val mockPersistenceServices = mock[PersistenceServices]

    val mockContactsServices = mock[ContactsServices]

    val mockImageServices = mock[ImageServices]

    val mockWidgetsServices = mock[WidgetsServices]

    val mockCallsServices = mock[CallsServices]

    val mockWifiServices = mock[WifiServices]

    val deviceProcess = new DeviceProcessImpl(
      mockAppsServices,
      mockApiServices,
      mockPersistenceServices,
      mockShortcutsServices,
      mockContactsServices,
      mockImageServices,
      mockWidgetsServices,
      mockCallsServices,
      mockWifiServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]

      apiUtils.getRequestConfig(contextSupport) returns TaskService(Task(Xor.right(requestConfig)))

    }

  }

}

class DeviceProcessImplSpec
  extends DeviceProcessSpecification {

  "Delete saved items" should {

    "deletes all apps, cards, collections and dockApps" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Xor.right(items)))

        val result = deviceProcess.resetSavedItems().value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns ResetException when persistence service fails deleting the apps" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Xor.Left[ResetException]]
      }

    "returns ResetException when persistence service fails deleting widgets" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Xor.Left[ResetException]]
      }

    "returns ResetException when persistence service fails deleting the collections" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCollections returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Xor.Left[ResetException]]
      }

    "returns ResetException when persistence service fails deleting the cards" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCollections returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCards returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Xor.Left[ResetException]]
      }

    "returns ResetException when persistence service fails deleting the dock apps" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllApps() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Xor.Left[ResetException]]
      }
  }

  "Get Shortcuts" should {

    "get available Shortcuts" in
      new DeviceProcessScope {

        mockShortcutsServices.getShortcuts(contextSupport) returns TaskService(Task(Xor.right(shortcuts)))
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beLike {
          case Xor.Right(r) => r.map(_.title) shouldEqual shortcuts.map(_.title)
        }
      }

    "returns ShortcutException when ShortcutsServices fails" in
      new DeviceProcessScope {

        mockShortcutsServices.getShortcuts(contextSupport) returns TaskService(Task(Xor.left(shortcutServicesException)))
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ShortcutException]]
      }

  }

  "Get Favorite Contacts" should {

    "get favorite contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.populateContactInfo(any) returns TaskService(Task(Xor.right(contacts)))

        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result shouldEqual Xor.right(deviceProcess.toContactSeq(contacts))
      }

    "returns ContactException when ContactsServices fails getting the favorite contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.left(contactsServicesException)))
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

    "returns ContactException when ContactsServices fails filling the contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.populateContactInfo(any) returns TaskService(Task(Xor.left(contactsServicesException)))
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

  }

  "getCounterForIterableContacts" should {

    "get term counters for contacts by name" in
      new DeviceProcessScope {

        mockContactsServices.getAlphabeticalCounterContacts returns TaskService(Task(Xor.right(contactsCounters)))
        val result = deviceProcess.getTermCountersForContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (contactsCounters map (_.term))
        }
        there was one(mockContactsServices).getAlphabeticalCounterContacts
      }

    "get term counters for contacts by favorite" in
      new DeviceProcessScope {

        val result = deviceProcess.getTermCountersForContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) => counters shouldEqual Seq.empty
        }
      }

    "get term counters for apps by contacts with phone number" in
      new DeviceProcessScope {

        val result = deviceProcess.getTermCountersForContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) => counters shouldEqual Seq.empty
        }
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        //        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Save shortcut icon" should {

    "get path of icon stored" in
      new DeviceProcessScope {

        val saveBitmap = SaveBitmap(bitmap = mockBitmap, bitmapResize = None)
        val saveBitmapPath = SaveBitmapPath(nameShortcut, fileNameShortcut)
        mockImageServices.saveBitmap(saveBitmap)(contextSupport) returns TaskService(Task(Xor.right(saveBitmapPath)))

        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beLike {
          case Xor.Right(path) => path shouldEqual fileNameShortcut
        }
      }

    "returns ShortcutException when ImageServices fails storing the icon" in
      new DeviceProcessScope {

        mockImageServices.saveBitmap(any[SaveBitmap])(any) returns TaskService(Task(Xor.left(fileServicesException)))
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ShortcutException]]
      }
  }

  "Get Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))

        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))

        val result = deviceProcess.getContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.getContactsWithPhone returns TaskService(Task(Xor.right(contacts)))

        val result = deviceProcess.getContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Xor.left(contactsServicesException)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Xor.right(contacts)))
        mockContactsServices.getContactsWithPhone returns TaskService(Task(Xor.right(contacts)))

        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

  }

  "Get Iterable Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContacts returns TaskService(Task(Xor.right(iterableCursorContact)))
        mockContactsServices.getIterableFavoriteContacts returns TaskService(Task(Xor.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableFavoriteContacts returns TaskService(Task(Xor.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsWithPhone returns TaskService(Task(Xor.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContacts returns TaskService(Task(Xor.left(contactsServicesException)))
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

  }

  "Get Contact" should {

    "get contact find a contact with data info filled" in
      new DeviceProcessScope {

        mockContactsServices.findContactByLookupKey(anyString) returns TaskService(Task(Xor.right(contact)))
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.lookupKey shouldEqual lookupKey
            response.info must beSome
        }
      }

    "returns ContactException when ContactsService fails getting contact" in
      new DeviceProcessScope {

        mockContactsServices.findContactByLookupKey(anyString) returns TaskService(Task(Xor.left(contactsServicesException)))
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

  }

  "Get Iterable Contacts by keyword" should {

    "get contacts by keyword" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsByKeyword(keyword) returns TaskService(Task(Xor.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsByKeyword(keyword) returns TaskService(Task(Xor.left(contactsServicesException)))
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[ContactException]]
      }

  }

  "Get Saved Apps" should {

    "get saved apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Xor.right(appsPersistence)))
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result shouldEqual Xor.Right(apps)
        there was one(mockPersistenceServices).fetchApps(OrderByName, ascending = true)
      }

    "get saved apps by update date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Xor.right(appsPersistence)))
        val result = deviceProcess.getSavedApps(GetByInstallDate)(contextSupport).value.run
        result shouldEqual Xor.Right(apps)
        there was one(mockPersistenceServices).fetchApps(OrderByInstallDate, ascending = false)
      }

    "get saved apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Xor.right(appsPersistence)))
        val result = deviceProcess.getSavedApps(GetByCategory)(contextSupport).value.run
        result shouldEqual Xor.Right(apps)
        there was one(mockPersistenceServices).fetchApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Get Iterable Saved Apps" should {

    "get iterable saved apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))

        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByName, ascending = true)
      }

    "get iterable saved apps by update date" in
      new DeviceProcessScope {
        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))

        val result = deviceProcess.getIterableApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByInstallDate, ascending = false)
      }

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.right(installationAppsCounters)))

        val result = deviceProcess.getIterableApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Get Iterable Saved Apps By Category" should {

    "get iterable saved apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByCategory(category, OrderByName, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "getTermCountersForApps" should {

    "get term counters for apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (appsCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchAlphabeticalAppsCounter
      }

    "get term counters for apps by installation date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (installationAppsCounters map (_.term))
        }
      }

    "get term counters for apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (categoryCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchCategorizedAppsCounter
      }

    "returns AppException if persistence service fails in GetByName" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

    "returns AppException if persistence service fails in GetByCategory" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))


        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

    "returns AppException if persistence service fails in GetByInstallDate" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Get Iterable Apps by keyword" should {

    "get iterable apps ordered by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true)
      }

    "get iterable apps ordered by update date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = false)
      }

    "get iterable apps ordered by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Xor.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }
  }

  "Getting and saving installed apps" should {

    "gets and saves installed apps" in
      new DeviceProcessScope {

        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService(Task(Xor.right(appsPersistence.head)))
        mockApiServices.googlePlayPackages(any)(any) returns TaskService(Task(Xor.right(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))
        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.right(applications)))

        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns a AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))
        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))

        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.right(applications)))
        mockApiServices.googlePlayPackages(any)(any) returns TaskService(Task(Xor.left(apiServiceException)))
        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService(Task(Xor.right(appsPersistence.head)))

        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.right(applications)))
        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService(Task(Xor.left(persistenceServiceException)))
        mockApiServices.googlePlayPackages(any)(any) returns TaskService(Task(Xor.right(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))

        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Getting and saving an installed app" should {

    "gets and saves an installed app" in
      new DeviceProcessScope {

        mockPersistenceServices.addApp(any[AddAppRequest]) returns(
          TaskService(Task(Xor.right(appsPersistence.head))),
          TaskService(Task(Xor.right(appsPersistence(1)))),
          TaskService(Task(Xor.right(appsPersistence(2)))))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Xor.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an AppException if app service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))
        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope {

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Xor.left(apiServiceException)))
        mockPersistenceServices.addApp(any[AddAppRequest]) returns(
          TaskService(Task(Xor.right(appsPersistence.head))),
          TaskService(Task(Xor.right(appsPersistence(1)))),
          TaskService(Task(Xor.right(appsPersistence(2)))))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Xor.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))
        mockPersistenceServices.addApp(any[AddAppRequest]) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Deleting an app" should {

    "deletes an app" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAppByPackage(any) returns TaskService(Task(Xor.right(items)))
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAppByPackage(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Updating an installed app" should {

    "gets and saves one installed app" in
      new DeviceProcessScope {

        mockPersistenceServices.updateApp(any) returns TaskService(Task(Xor.right(items)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Xor.right(appsPersistence.headOption)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Xor.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope {

        mockPersistenceServices.updateApp(any) returns TaskService(Task(Xor.right(items)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Xor.right(appsPersistence.headOption)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Xor.left(apiServiceException)))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Xor.right(applications.head)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[AppException]]
      }

  }

  "Get Widgets" should {

    "get widgets" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Xor.right(appsPersistence)))
        mockWidgetsServices.getWidgets(any) returns TaskService(Task(Xor.right(widgetsServices)))

        val result = deviceProcess.getWidgets(contextSupport).value.run
        result shouldEqual Xor.Right(appWithWidgets)
      }

    "returns WidgetException if WidgetServices fail getting the Widgets " in
      new DeviceProcessScope {

        mockWidgetsServices.getWidgets(any) returns TaskService {
          Task(Xor.left(widgetsServicesException))
        }
        val result = deviceProcess.getWidgets(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[WidgetException]]
      }

  }

  "Get Last Calls" should {

    "get last calls" in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Xor.right(callsServices)))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber1) returns TaskService(Task(Xor.right(Some(callsContacts(0)))))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber2) returns TaskService(Task(Xor.right(Some(callsContacts(1)))))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber3) returns TaskService(Task(Xor.right(Some(callsContacts(2)))))

        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result shouldEqual Xor.Right(lastCallsContacts)
      }

    "returns CallsException if CallsServices fail getting the calls " in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Xor.left(callsServicesException)))
        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[CallException]]
      }

    "returns an empty List if ContactsServices fail getting the contacts " in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Xor.right(callsServices)))
        mockContactsServices.fetchContactByPhoneNumber(any) returns TaskService(Task(Xor.left(contactsServicesException)))

        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result shouldEqual Xor.Right(Seq())
      }

  }

  "Generate Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.right(applications)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.right(dockAppSeq)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Xor.right(appsPersistence)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Xor.right(appsPersistence.headOption)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result shouldEqual Xor.Right(dockAppProcessSeq)
      }

    "returns DockAppException when AppService fails" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.left(appInstalledException)))
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }

    "returns DockAppException when PersistenceService fails fetching the apps" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.right(applications)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.right(dockAppSeq)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }

    "returns DockAppException when PersistenceService fails saving the apps" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Xor.right(applications)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Xor.right(appsPersistence)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.left(persistenceServiceException)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }
  }

  "Create Or Update Dock App" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.right(dockAppSeq)))
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }

  }

  "Save DockApps" should {

    "return the three dockApps saved" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.right(dockAppSeq)))
        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beLike {
          case Xor.Right(resultSeqDockApp) =>
            resultSeqDockApp.size shouldEqual saveDockAppRequestSeq.size
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }

  }

  "Get Dock Apps" should {

    "get dock apps stored" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchDockApps returns TaskService(Task(Xor.right(dockAppSeq)))
        val result = deviceProcess.getDockApps.value.run
        result must beLike {
          case Xor.Right(resultDockApp) =>
            resultDockApp map (_.name) shouldEqual (dockAppProcessSeq map (_.name))
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchDockApps returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.getDockApps.value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }
  }

  "Delete All Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Xor.right(dockAppsRemoved)))
        val result = deviceProcess.deleteAllDockApps().value.run
        result shouldEqual Xor.Right((): Unit)
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Xor.left(persistenceServiceException)))
        val result = deviceProcess.deleteAllDockApps().value.run
        result must beAnInstanceOf[Xor.Left[DockAppException]]
      }
  }

  "getConfiguredNetworks" should {

    "returns all networks for a valid request" in
      new DeviceProcessScope {

        mockWifiServices.getConfiguredNetworks(contextSupport) returns TaskService(Task(Xor.right(networks)))
        val result = deviceProcess.getConfiguredNetworks(contextSupport).value.run
        result shouldEqual Xor.Right(networks)
      }

  }

}
