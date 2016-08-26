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

    mockAppsServices.getInstalledApplications(contextSupport) returns
      TaskService(Task(Xor.right(applications)))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      TaskService(Task(Xor.right(applications.head)))

    mockAppsServices.getDefaultApps(contextSupport) returns
      TaskService(Task(Xor.right(applications)))

    val mockApiServices = mock[ApiServices]

    mockApiServices.googlePlayPackages(any)(any) returns
      TaskService(Task(Xor.right(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))

    mockApiServices.googlePlayPackage(any)(any) returns
      TaskService(Task(Xor.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))

    val mockShortcutsServices = mock[ShortcutsServices]

    mockShortcutsServices.getShortcuts(contextSupport) returns
      TaskService(Task(Xor.right(shortcuts)))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.deleteAllApps() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCollections() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCards() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllDockApps() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.fetchApps(any, any) returns
      TaskService(Task(Xor.right(appsPersistence)))

    mockPersistenceServices.fetchIterableApps(any, any) returns
      TaskService(Task(Xor.right(iterableCursorApps)))

    mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns
      TaskService(Task(Xor.right(iterableCursorApps)))

    mockPersistenceServices.fetchAlphabeticalAppsCounter returns
      TaskService(Task(Xor.right(appsCounters)))

    mockPersistenceServices.fetchCategorizedAppsCounter returns
      TaskService(Task(Xor.right(categoryCounters)))

    mockPersistenceServices.fetchInstallationDateAppsCounter returns
      TaskService(Task(Xor.right(installationAppsCounters)))

    mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns
      TaskService(Task(Xor.right(iterableCursorApps)))

    mockPersistenceServices.addApp(any[AddAppRequest]) returns(
      TaskService(Task(Xor.right(appsPersistence.head))),
      TaskService(Task(Xor.right(appsPersistence(1)))),
      TaskService(Task(Xor.right(appsPersistence(2)))))

    mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns
      TaskService(Task(Xor.right(appsPersistence.head)))

    mockPersistenceServices.deleteAppByPackage(any) returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.findAppByPackage(any) returns
      TaskService(Task(Xor.right(appsPersistence.headOption)))

    mockPersistenceServices.fetchAppByPackages(any) returns
      TaskService(Task(Xor.right(appsPersistence)))

    mockPersistenceServices.updateApp(any) returns
      TaskService(Task(Xor.right(items)))

    val mockContactsServices = mock[ContactsServices]

    mockContactsServices.getContacts returns
      TaskService(Task(Xor.right(contacts)))

    mockContactsServices.getFavoriteContacts returns
      TaskService(Task(Xor.right(contacts)))

    mockContactsServices.getContactsWithPhone returns
      TaskService(Task(Xor.right(contacts)))

    mockContactsServices.getIterableContacts returns
      TaskService(Task(Xor.right(iterableCursorContact)))

    mockContactsServices.getAlphabeticalCounterContacts returns
      TaskService(Task(Xor.right(contactsCounters)))

    mockContactsServices.getIterableFavoriteContacts returns
      TaskService(Task(Xor.right(iterableCursorContact)))

    mockContactsServices.getIterableContactsWithPhone returns
      TaskService(Task(Xor.right(iterableCursorContact)))

    mockContactsServices.getIterableContactsByKeyword(keyword) returns
      TaskService(Task(Xor.right(iterableCursorContact)))

    mockContactsServices.findContactByLookupKey("lookupKey 1") returns
      TaskService(Task(Xor.right(contacts.head)))
    mockContactsServices.findContactByLookupKey("lookupKey 2") returns
      TaskService(Task(Xor.right(contacts(1))))
    mockContactsServices.findContactByLookupKey("lookupKey 3") returns
      TaskService(Task(Xor.right(contacts(2))))

    val mockImageServices = mock[ImageServices]

    val mockWidgetsServices = mock[WidgetsServices]

    mockWidgetsServices.getWidgets(any) returns
      TaskService(Task(Xor.right(widgetsServices)))

    val mockCallsServices = mock[CallsServices]

    mockCallsServices.getLastCalls returns
      TaskService(Task(Xor.right(callsServices)))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber1) returns
      TaskService(Task(Xor.right(Some(callsContacts(0)))))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber2) returns
      TaskService(Task(Xor.right(Some(callsContacts(1)))))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber3) returns
      TaskService(Task(Xor.right(Some(callsContacts(2)))))

    val mockWifiServices = mock[WifiServices]

    mockWifiServices.getConfiguredNetworks(contextSupport) returns
      TaskService(Task(Xor.right(networks)))

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
      apiUtils.getRequestConfig(contextSupport) returns
        TaskService(Task(Xor.right(requestConfig)))

    }

  }

  trait ErrorAppServicesProcessScope {
    self: DeviceProcessScope =>

    mockAppsServices.getInstalledApplications(contextSupport) returns TaskService {
      Task(Xor.left(appInstalledException))
    }

    mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService {
      Task(Xor.left(appInstalledException))
    }

    mockAppsServices.getDefaultApps(contextSupport) returns TaskService {
      Task(Xor.left(appInstalledException))
    }

  }

  trait ErrorApiServicesProcessScope {
    self: DeviceProcessScope =>

    mockApiServices.googlePlayPackages(any)(any) returns TaskService {
      Task(Xor.left(apiServiceException))
    }

    mockApiServices.googlePlayPackage(any)(any) returns TaskService {
      Task(Xor.left(apiServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteAppsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }
  }

  trait ErrorPersistenceServicesDeleteWidgetsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllWidgets() returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteCollectionsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCollections returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteCardsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCollections returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCards returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteDockAppsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCollections() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllCards() returns
      TaskService(Task(Xor.right(items)))

    mockPersistenceServices.deleteAllDockApps() returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }
  }

  trait ErrorPersistenceServicesProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchApps(any, any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableApps(any, any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.addApp(any[AddAppRequest]) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.deleteAppByPackage(any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.findAppByPackage(any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.updateApp(any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

  }

  trait ShortcutsErrorScope {
    self: DeviceProcessScope =>

    mockShortcutsServices.getShortcuts(contextSupport) returns TaskService {
      Task(Xor.left(shortcutServicesException))
    }

  }

  trait FavoriteContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getFavoriteContacts returns TaskService {
      Task(Xor.left(contactsServicesException))
    }

  }

  trait FilledFavoriteContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getFavoriteContacts returns
      TaskService(Task(Xor.right(contacts)))

    mockContactsServices.findContactByLookupKey(any) returns TaskService {
      Task(Xor.left(contactsServicesException))
    }

  }

  trait SaveShortcutScope {
    self: DeviceProcessScope =>

    val saveBitmap = SaveBitmap(bitmap = mockBitmap, bitmapResize = None)

    val saveBitmapPath = SaveBitmapPath(nameShortcut, fileNameShortcut)

    mockImageServices.saveBitmap(saveBitmap)(contextSupport) returns
      TaskService(Task(Xor.right(saveBitmapPath)))
  }

  trait SaveShortcutErrorScope {
    self: DeviceProcessScope =>

    mockImageServices.saveBitmap(any[SaveBitmap])(any) returns TaskService {
      Task(Xor.left(fileServicesException))
    }
  }

  trait FindContactScope {
    self: DeviceProcessScope =>

    mockContactsServices.findContactByLookupKey(anyString) returns
      TaskService(Task(Xor.right(contact)))
  }

  trait ContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getContacts returns TaskService {
      Task(Xor.left(contactsServicesException))
    }

    mockContactsServices.getIterableContacts returns TaskService {
      Task(Xor.left(contactsServicesException))
    }

    mockContactsServices.findContactByLookupKey(anyString) returns TaskService {
      Task(Xor.left(contactsServicesException))
    }

    mockContactsServices.getIterableContactsByKeyword(keyword) returns TaskService {
      Task(Xor.left(contactsServicesException))
    }
  }

  trait WidgetsErrorScope {
    self: DeviceProcessScope =>

    mockWidgetsServices.getWidgets(any) returns TaskService {
      Task(Xor.left(widgetsServicesException))
    }
  }

  trait CallsErrorScope {
    self: DeviceProcessScope =>

    mockCallsServices.getLastCalls returns TaskService {
      Task(Xor.left(callsServicesException))
    }
  }

  trait CallsContactsErrorScope {
    self: DeviceProcessScope =>

    mockCallsServices.getLastCalls returns
      TaskService(Task(Xor.right(callsServices)))

    mockContactsServices.fetchContactByPhoneNumber(any) returns TaskService {
      Task(Xor.left(contactsServicesException))
    }
  }

  trait DockAppsScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.createOrUpdateDockApp(any) returns
      TaskService(Task(Xor.right(dockAppSeq)))

    mockPersistenceServices.fetchDockApps returns
      TaskService(Task(Xor.right(dockAppSeq)))

    mockPersistenceServices.deleteAllDockApps() returns
      TaskService(Task(Xor.right(dockAppsRemoved)))
  }

  trait DockAppsFindErrorScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchAppByPackages(any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.createOrUpdateDockApp(any) returns
      TaskService(Task(Xor.right(dockAppSeq)))
  }

  trait DockAppsErrorScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.fetchDockApps returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }

    mockPersistenceServices.deleteAllDockApps() returns TaskService {
      Task(Xor.left(persistenceServiceException))
    }
  }

}

class DeviceProcessImplSpec
  extends DeviceProcessSpecification {

  "Delete saved items" should {

    "deletes all apps, cards, collections and dockApps" in
      new DeviceProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Right(result) =>
            result shouldEqual ((): Unit)
        }
      }

    "returns ResetException when persistence service fails deleting the apps" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteAppsProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ResetException]
          }
      }

    "returns ResetException when persistence service fails deleting widgets" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteWidgetsProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ResetException]
          }
      }

    "returns ResetException when persistence service fails deleting the collections" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteCollectionsProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ResetException]
          }
      }

    "returns ResetException when persistence service fails deleting the cards" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteCardsProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ResetException]
          }
      }

    "returns ResetException when persistence service fails deleting the dock apps" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteDockAppsProcessScope {
        val result = deviceProcess.resetSavedItems().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ResetException]
          }
      }

  }

  "Get Shortcuts" should {

    "get available Shortcuts" in
      new DeviceProcessScope {
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beLike {
          case Xor.Right(r) => r.map(_.title) shouldEqual shortcuts.map(_.title)
        }
      }

    "returns ShortcutException when ShortcutsServices fails" in
      new DeviceProcessScope with ShortcutsErrorScope {
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ShortcutException]
          }
      }

  }

  "Get Favorite Contacts" should {

    "get favorite contacts" in
      new DeviceProcessScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beLike {
          // TODO - This is a workaround and need to be fixed in ticket 9C-284
          case Xor.Right(r) => r.map(_.name).sorted shouldEqual contacts.map(_.name).sorted
        }
      }

    "returns ContactException when ContactsServices fails getting the favorite contacts" in
      new DeviceProcessScope with FavoriteContactsErrorScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ContactException]
          }
      }

    "returns an empty list if ContactsServices fails filling the contacts" in
      new DeviceProcessScope with FilledFavoriteContactsErrorScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultContacts) =>
            resultContacts shouldEqual Seq()
        }
      }

  }

  "getCounterForIterableContacts" should {

    "get term counters for contacts by name" in
      new DeviceProcessScope {
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
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Save shortcut icon" should {

    "get path of icon stored" in
      new DeviceProcessScope with SaveShortcutScope {
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beLike {
          case Xor.Right(path) => path shouldEqual fileNameShortcut
        }
      }

    "returns ShortcutException when ImageServices fails storing the icon" in
      new DeviceProcessScope with SaveShortcutErrorScope {
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ShortcutException]
          }
      }
  }

  "Get Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ContactException]
          }
      }

  }

  "Get Iterable Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ContactException]
          }
      }

  }

  "Get Contact" should {

    "get contact find a contact with data info filled" in
      new DeviceProcessScope with FindContactScope {
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beLike {
          case Xor.Right(response) =>
            response.lookupKey shouldEqual lookupKey
            response.info must beSome
        }
      }

    "returns ContactException when ContactsService fails getting contact" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ContactException]
          }
      }

  }

  "Get Iterable Contacts by keyword" should {

    "get contacts by keyword" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[ContactException]
          }
      }

  }

  "Get Saved Apps" should {

    "get saved apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByName, ascending = true)
      }

    "get saved apps by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByInstallDate, ascending = false)
      }

    "get saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Get Iterable Saved Apps" should {

    "get iterable saved apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByName, ascending = true)
      }

    "get iterable saved apps by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByInstallDate, ascending = false)
      }

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Get Iterable Saved Apps By Category" should {

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByCategory(category, OrderByName, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "getTermCountersForApps" should {

    "get term counters for apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (appsCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchAlphabeticalAppsCounter
      }

    "get term counters for apps by installation date" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (installationAppsCounters map (_.term))
        }
      }

    "get term counters for apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(counters) =>
            counters map (_.term) shouldEqual (categoryCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchCategorizedAppsCounter
      }

    "returns AppException if persistence service fails in GetByName" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

    "returns AppException if persistence service fails in GetByCategory" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e  must beAnInstanceOf[AppException]
          }
      }

    "returns AppException if persistence service fails in GetByInstallDate" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Get Iterable Apps by keyword" should {

    "get iterable apps ordered by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true)
      }

    "get iterable apps ordered by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = false)
      }

    "get iterable apps ordered by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByCategory)(contextSupport).value.run
        result must beLike {
          case Xor.Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }
  }

  "Getting and saving installed apps" should {

    "gets and saves installed apps" in
      new DeviceProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns a AppException if persistence service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Getting and saving an installed app" should {

    "gets and saves an installed app" in
      new DeviceProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Deleting an app" should {

    "deletes an app" in
      new DeviceProcessScope {
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Updating an installed app" should {

    "gets and saves one installed app" in
      new DeviceProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[AppException]
          }
      }

  }

  "Get Widgets" should {

    "get widgets" in
      new DeviceProcessScope {
        val result = deviceProcess.getWidgets(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultWidgets) =>
            resultWidgets shouldEqual appWithWidgets
        }
      }

    "returns WidgetException if WidgetServices fail getting the Widgets " in
      new DeviceProcessScope with WidgetsErrorScope {
        val result = deviceProcess.getWidgets(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[WidgetException]
          }
      }

  }

  "Get Last Calls" should {

    "get last calls" in
      new DeviceProcessScope {
        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultLastCalls) =>
            resultLastCalls shouldEqual lastCallsContacts
        }
      }

    "returns CallsException if CallsServices fail getting the calls " in
      new DeviceProcessScope with CallsErrorScope {
        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[CallException]
          }
      }

    "returns an empty List if ContactsServices fail getting the contacts " in
      new DeviceProcessScope with CallsContactsErrorScope {
        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultApps) =>
            resultApps shouldEqual Seq()
        }
      }

  }

  "Generate Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultDockApp) =>
            resultDockApp shouldEqual dockAppProcessSeq
        }
      }

    "returns DockAppException when AppService fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }

    "returns DockAppException when PersistenceService fails fetching the apps" in
      new DeviceProcessScope with DockAppsFindErrorScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }

    "returns DockAppException when PersistenceService fails saving the apps" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }
  }

  "Create Or Update Dock App" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result must beLike {
          case Xor.Right(resultDockApp) =>
            resultDockApp shouldEqual ((): Unit)
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }

  }

  "Save DockApps" should {

    "return the three dockApps saved" in
      new DeviceProcessScope with DockAppsScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Xor.right(dockAppSeq)))

        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beLike {
          case Xor.Right(resultSeqDockApp) =>
            resultSeqDockApp.size shouldEqual saveDockAppRequestSeq.size
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {

        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }

  }

  "Get Dock Apps" should {

    "get dock apps stored" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.getDockApps.value.run
        result must beLike {
          case Xor.Right(resultDockApp) =>
            resultDockApp map (_.name) shouldEqual (dockAppProcessSeq map (_.name))
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.getDockApps.value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }
  }

  "Delete All Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.deleteAllDockApps().value.run
        result must beLike {
          case Xor.Right(resultDockApp) =>
            resultDockApp shouldEqual ((): Unit)
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.deleteAllDockApps().value.run
        result must beLike {
          case Xor.Left(e) => e must beAnInstanceOf[DockAppException]
          }
      }
  }

  "getConfiguredNetworks" should {

    "returns all networks for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.getConfiguredNetworks(contextSupport).value.run
        result must beLike {
          case Xor.Right(r) =>
            r shouldEqual networks
        }
      }

  }

}
