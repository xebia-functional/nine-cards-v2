package cards.nine.process.device.impl

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.services.TaskService
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.models.{Application, BitmapPath}
import cards.nine.models.types.{AppDockType, Misc}
import cards.nine.process.device._
import cards.nine.process.utils.ApiUtils
import cards.nine.services.api._
import cards.nine.services.apps.{AppsInstalledException, AppsServices}
import cards.nine.services.calls.{CallsServices, CallsServicesException}
import cards.nine.services.contacts.{ContactsServiceException, ContactsServices}
import cards.nine.services.image._
import cards.nine.services.persistence._
import cards.nine.services.shortcuts.{ShortcutServicesException, ShortcutsServices}
import cards.nine.services.widgets.{WidgetServicesException, WidgetsServices}
import cards.nine.services.wifi.WifiServices
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

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

      apiUtils.getRequestConfig(contextSupport) returns TaskService(Task(Either.right(requestConfig)))

    }

  }

}

class DeviceProcessImplSpec
  extends DeviceProcessSpecification {

  "Delete saved items" should {

    "deletes all apps, cards, collections and dockApps" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Either.right(items)))

        val result = deviceProcess.resetSavedItems().value.run
        result shouldEqual Right((): Unit)
      }

    "returns ResetException when persistence service fails deleting widgets" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Left[ResetException, _]]
      }

    "returns ResetException when persistence service fails deleting the collections" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCollections returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Left[ResetException, _]]
      }

    "returns ResetException when persistence service fails deleting the cards" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCollections returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCards returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Left[ResetException, _]]
      }

    "returns ResetException when persistence service fails deleting the dock apps" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllWidgets() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCollections() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllCards() returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.resetSavedItems().value.run
        result must beAnInstanceOf[Left[ResetException, _]]
      }
  }

  "Get Shortcuts" should {

    "get available Shortcuts" in
      new DeviceProcessScope {

        mockShortcutsServices.getShortcuts(contextSupport) returns TaskService(Task(Either.right(shortcuts)))
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beLike {
          case Right(r) => r.map(_.title) shouldEqual shortcuts.map(_.title)
        }
      }

    "returns ShortcutException when ShortcutsServices fails" in
      new DeviceProcessScope {

        mockShortcutsServices.getShortcuts(contextSupport) returns TaskService(Task(Either.left(shortcutServicesException)))
        val result = deviceProcess.getAvailableShortcuts(contextSupport).value.run
        result must beAnInstanceOf[Left[ShortcutException, _]]
      }

  }

  "Get Favorite Contacts" should {

    "get favorite contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.populateContactInfo(any) returns TaskService(Task(Either.right(contacts)))

        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result shouldEqual Right(contacts)
      }

    "returns ContactException when ContactsServices fails getting the favorite contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.left(contactsServicesException)))
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

    "returns ContactException when ContactsServices fails filling the contacts" in
      new DeviceProcessScope {

        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.populateContactInfo(any) returns TaskService(Task(Either.left(contactsServicesException)))
        val result = deviceProcess.getFavoriteContacts(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

  }

  "getCounterForIterableContacts" should {

    "get term counters for contacts by name" in
      new DeviceProcessScope {

        mockContactsServices.getAlphabeticalCounterContacts returns TaskService(Task(Either.right(contactsCounters)))
        val result = deviceProcess.getTermCountersForContacts()(contextSupport).value.run
        result must beLike {
          case Right(counters) =>
            counters map (_.term) shouldEqual (contactsCounters map (_.term))
        }
        there was one(mockContactsServices).getAlphabeticalCounterContacts
      }

    "get term counters for contacts by favorite" in
      new DeviceProcessScope {

        val result = deviceProcess.getTermCountersForContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Right(counters) => counters shouldEqual Seq.empty
        }
      }

    "get term counters for apps by contacts with phone number" in
      new DeviceProcessScope {

        val result = deviceProcess.getTermCountersForContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Right(counters) => counters shouldEqual Seq.empty
        }
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        //        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Save shortcut icon" should {

    "get path of icon stored" in
      new DeviceProcessScope {

        val bitmapPath = BitmapPath(nameShortcut, fileNameShortcut)
        mockImageServices.saveBitmap(mockBitmap, None, None)(contextSupport) returns TaskService(Task(Either.right(bitmapPath)))

        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beLike {
          case Right(path) => path shouldEqual fileNameShortcut
        }
      }

    "returns ShortcutException when ImageServices fails storing the icon" in
      new DeviceProcessScope {

        mockImageServices.saveBitmap(any, any, any)(any) returns TaskService(Task(Either.left(fileServicesException)))
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).value.run
        result must beAnInstanceOf[Left[ShortcutException, _]]
      }
  }

  "Get Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))

        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beLike {
          case Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))

        val result = deviceProcess.getContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.getContactsWithPhone returns TaskService(Task(Either.right(contacts)))

        val result = deviceProcess.getContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Right(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getContacts returns TaskService(Task(Either.left(contactsServicesException)))
        mockContactsServices.getFavoriteContacts returns TaskService(Task(Either.right(contacts)))
        mockContactsServices.getContactsWithPhone returns TaskService(Task(Either.right(contacts)))

        val result = deviceProcess.getContacts()(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

  }

  "Get Iterable Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContacts returns TaskService(Task(Either.right(iterableCursorContact)))
        mockContactsServices.getIterableFavoriteContacts returns TaskService(Task(Either.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beLike {
          case Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableFavoriteContacts returns TaskService(Task(Either.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts(FavoriteContacts)(contextSupport).value.run
        result must beLike {
          case Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsWithPhone returns TaskService(Task(Either.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContacts(ContactsWithPhoneNumber)(contextSupport).value.run
        result must beLike {
          case Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContacts returns TaskService(Task(Either.left(contactsServicesException)))
        val result = deviceProcess.getIterableContacts()(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

  }

  "Get Contact" should {

    "get contact find a contact with data info filled" in
      new DeviceProcessScope {

        mockContactsServices.findContactByLookupKey(anyString) returns TaskService(Task(Either.right(contact)))
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beLike {
          case Right(response) =>
            response.lookupKey shouldEqual lookupKey
            response.info must beSome
        }
      }

    "returns ContactException when ContactsService fails getting contact" in
      new DeviceProcessScope {

        mockContactsServices.findContactByLookupKey(anyString) returns TaskService(Task(Either.left(contactsServicesException)))
        val result = deviceProcess.getContact(lookupKey)(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

  }

  "Get Iterable Contacts by keyword" should {

    "get contacts by keyword" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsByKeyword(keyword) returns TaskService(Task(Either.right(iterableCursorContact)))
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beLike {
          case Right(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope {

        mockContactsServices.getIterableContactsByKeyword(keyword) returns TaskService(Task(Either.left(contactsServicesException)))
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).value.run
        result must beAnInstanceOf[Left[ContactException, _]]
      }

  }

  "Get Saved Apps" should {

    "get saved apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(applicationSeq)))
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result shouldEqual Right(applicationDataSeq)
        there was one(mockPersistenceServices).fetchApps(OrderByName, ascending = true)
      }

    "get saved apps by update date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(applicationSeq)))
        val result = deviceProcess.getSavedApps(GetByInstallDate)(contextSupport).value.run
        result shouldEqual Right(applicationDataSeq)
        there was one(mockPersistenceServices).fetchApps(OrderByInstallDate, ascending = false)
      }

    "get saved apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(applicationSeq)))
        val result = deviceProcess.getSavedApps(GetByCategory)(contextSupport).value.run
        result shouldEqual Right(applicationDataSeq)
        there was one(mockPersistenceServices).fetchApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Get Iterable Saved Apps" should {

    "get iterable saved apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Either.right(iterableCursorApps)))

        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByName, ascending = true)
      }

    "get iterable saved apps by update date" in
      new DeviceProcessScope {
        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Either.right(iterableCursorApps)))

        val result = deviceProcess.getIterableApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByInstallDate, ascending = false)
      }

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.right(installationAppsCounters)))

        val result = deviceProcess.getIterableApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableApps(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Get Iterable Saved Apps By Category" should {

    "get iterable saved apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByCategory(category, OrderByName, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "getTermCountersForApps" should {

    "get term counters for apps by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beLike {
          case Right(counters) =>
            counters map (_.term) shouldEqual (appsCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchAlphabeticalAppsCounter
      }

    "get term counters for apps by installation date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Right(counters) =>
            counters map (_.term) shouldEqual (installationAppsCounters map (_.term))
        }
      }

    "get term counters for apps by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.right(appsCounters)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.right(categoryCounters)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.right(installationAppsCounters)))

        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beLike {
          case Right(counters) =>
            counters map (_.term) shouldEqual (categoryCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchCategorizedAppsCounter
      }

    "returns AppException if persistence service fails in GetByName" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

    "returns AppException if persistence service fails in GetByCategory" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))


        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

    "returns AppException if persistence service fails in GetByInstallDate" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchAlphabeticalAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchCategorizedAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))
        mockPersistenceServices.fetchInstallationDateAppsCounter returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Get Iterable Apps by keyword" should {

    "get iterable apps ordered by name" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true)
      }

    "get iterable apps ordered by update date" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByInstallDate)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = false)
      }

    "get iterable apps ordered by category" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Either.right(iterableCursorApps)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByCategory)(contextSupport).value.run
        result must beLike {
          case Right(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope {

        mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }
  }

  "Synchronize installed apps" should {

    "gets and saves installed apps" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(any) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockPersistenceServices.fetchApps(any, any) returns TaskService.right(Seq.empty)
        mockApiServices.googlePlayPackages(any)(any) returns TaskService(Task(Either.right(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))
        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService(Task(Either.right(applicationSeq.head)))

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "don't call to api services if all applications are in the database" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(any) returns TaskService.right(applicationDataSeq)
        mockPersistenceServices.fetchApps(any, any) returns TaskService.right(applicationSeq)

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result shouldEqual Right((): Unit)

        there was no(mockApiServices).googlePlayPackages(any)(any)
        there was no(mockPersistenceServices).addApps(any[Seq[AddAppRequest]])
      }

    "delete the duplicated apps" in
      new DeviceProcessScope {

        val app1 = applicationSeq.head.copy(id = 1, packageName = packageName1, className = className1)
        val app2 = applicationSeq.head.copy(id = 2, packageName = packageName1, className = className2)
        val app3 = applicationSeq.head.copy(id = 3, packageName = packageName3, className = className3)
        val app4 = applicationSeq.head.copy(id = 4, packageName = packageName1, className = className1)

        mockAppsServices.getInstalledApplications(any) returns TaskService.right(Seq(app1.toData, app2.toData, app3.toData))
        mockPersistenceServices.fetchApps(any, any) returns TaskService.right(Seq(app1, app2, app3, app4))
        mockPersistenceServices.deleteAppsByIds(any) returns TaskService.right(1)

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockPersistenceServices).deleteAppsByIds(Seq(app4.id))
        there was no(mockApiServices).googlePlayPackages(any)(any)
        there was no(mockPersistenceServices).addApps(any[Seq[AddAppRequest]])
      }

    "call to api services only for those apps with different packageName" in
      new DeviceProcessScope {

        val app1 = applicationSeq.head.copy(packageName = packageName1, className = className1)
        val app2 = applicationSeq.head.copy(packageName = packageName2, className = className2)

        val app1Request = AddAppRequest(
          name = app1.name,
          packageName = app1.packageName,
          className = app1.className,
          category = app1.category.name,
          dateInstalled = app1.dateInstalled,
          dateUpdate = app1.dateUpdate,
          version = app1.version,
          installedFromGooglePlay = app1.installedFromGooglePlay)

        mockAppsServices.getInstalledApplications(any) returns TaskService.right(Seq(app1.toData, app2.toData))
        mockPersistenceServices.fetchApps(any, any) returns TaskService.right(Seq(app2))
        mockApiServices.googlePlayPackages(any)(any) returns TaskService.right(GooglePlayPackagesResponse(
          statusCodeOk,
          Seq(CategorizedPackage(app1.packageName, Some(app1.category.name)))))
        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService.right(applicationSeq.head)

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result shouldEqual Right((): Unit)

        there was one(mockApiServices).googlePlayPackages(===(Seq(app1.packageName)))(any)
        there was one(mockPersistenceServices).addApps(Seq(app1Request))
      }

    "returns a AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(any) returns TaskService(Task(Either.left(appInstalledException)))
        mockAppsServices.getApplication(any)(any) returns TaskService(Task(Either.left(appInstalledException)))
        mockAppsServices.getDefaultApps(any) returns TaskService(Task(Either.left(appInstalledException)))

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(any) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockApiServices.googlePlayPackages(any)(any) returns TaskService(Task(Either.left(apiServiceException)))
        mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns TaskService(Task(Either.right(applicationSeq.head)))
        mockPersistenceServices.fetchApps(any, any) returns TaskService.right(Seq.empty)

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(any) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.synchronizeInstalledApps(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Getting and saving an installed app" should {

    "gets and saves an installed app" in
      new DeviceProcessScope {

        mockPersistenceServices.addApp(any[AddAppRequest]) returns(
          TaskService(Task(Either.right(applicationSeq.head))),
          TaskService(Task(Either.right(applicationSeq(1)))),
          TaskService(Task(Either.right(applicationSeq(2)))))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Either.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result shouldEqual Right(applicationDataSeq.head)
      }

    "returns an AppException if app service fails" in
      new DeviceProcessScope {

        mockAppsServices.getInstalledApplications(contextSupport) returns TaskService(Task(Either.left(appInstalledException)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.left(appInstalledException)))
        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Either.left(appInstalledException)))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

    "returns an app with Misc category if api service fails" in
      new DeviceProcessScope {

        val appsPersistenceFailed = applicationSeq map (_.copy(category = Misc))
        val appExpected = applicationDataSeq.head.copy(category = Misc)

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Either.left(apiServiceException)))
        mockPersistenceServices.addApp(any[AddAppRequest]) returns(
          TaskService(Task(Either.right(appsPersistenceFailed.head))),
          TaskService(Task(Either.right(appsPersistenceFailed(1)))),
          TaskService(Task(Either.right(appsPersistenceFailed(2)))))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run

        result shouldEqual Right(appExpected)
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Either.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))
        mockPersistenceServices.addApp(any[AddAppRequest]) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.saveApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Deleting an app" should {

    "deletes an app" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAppByPackage(any) returns TaskService(Task(Either.right(items)))
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAppByPackage(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Updating an installed app" should {

    "gets and saves one installed app" in
      new DeviceProcessScope {

        mockPersistenceServices.updateApp(any) returns TaskService(Task(Either.right(items)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Either.right(applicationSeq.headOption)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Either.right(GooglePlayPackageResponse(statusCodeOk, categorizedPackage))))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope {

        mockPersistenceServices.updateApp(any) returns TaskService(Task(Either.right(items)))
        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Either.right(applicationSeq.headOption)))
        mockApiServices.googlePlayPackage(any)(any) returns TaskService(Task(Either.left(apiServiceException)))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result shouldEqual Right((): Unit)
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope {

        mockAppsServices.getApplication(packageName1)(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq.head)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.updateApp(packageName1)(contextSupport).value.run
        result must beAnInstanceOf[Left[AppException, _]]
      }

  }

  "Get Widgets" should {

    "get widgets" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchApps(any, any) returns TaskService(Task(Either.right(applicationSeq)))
        mockWidgetsServices.getWidgets(any) returns TaskService(Task(Either.right(widgetsServices)))

        val result = deviceProcess.getWidgets(contextSupport).value.run
        result shouldEqual Right(appWithWidgets)
      }

    "returns WidgetException if WidgetServices fail getting the Widgets " in
      new DeviceProcessScope {

        mockWidgetsServices.getWidgets(any) returns TaskService {
          Task(Either.left(widgetsServicesException))
        }
        val result = deviceProcess.getWidgets(contextSupport).value.run
        result must beAnInstanceOf[Left[WidgetException, _]]
      }

  }

  "Get Last Calls" should {

    "get last calls" in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Either.right(calls)))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber1) returns TaskService(Task(Either.right(Some(callsContacts.head))))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber2) returns TaskService(Task(Either.right(Some(callsContacts(1)))))
        mockContactsServices.fetchContactByPhoneNumber(phoneNumber3) returns TaskService(Task(Either.right(Some(callsContacts(2)))))

        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result shouldEqual Right(lastCallsContacts)
      }

    "returns CallsException if CallsServices fail getting the calls " in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Either.left(callsServicesException)))
        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result must beAnInstanceOf[Left[CallException, _]]
      }

    "returns an empty List if ContactsServices fail getting the contacts " in
      new DeviceProcessScope {

        mockCallsServices.getLastCalls returns TaskService(Task(Either.right(calls)))
        mockContactsServices.fetchContactByPhoneNumber(any) returns TaskService(Task(Either.left(contactsServicesException)))

        val result = deviceProcess.getLastCalls(contextSupport).value.run
        result shouldEqual Right(Seq())
      }

  }

  "Generate Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.right(dockAppSeq)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Either.right(applicationSeq)))
        mockPersistenceServices.findAppByPackage(any) returns TaskService(Task(Either.right(applicationSeq.headOption)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result shouldEqual Right(dockAppProcessSeq)
      }

    "returns DockAppException when AppService fails" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Either.left(appInstalledException)))
        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }

    "returns DockAppException when PersistenceService fails fetching the apps" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.right(dockAppSeq)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }

    "returns DockAppException when PersistenceService fails saving the apps" in
      new DeviceProcessScope {

        mockAppsServices.getDefaultApps(contextSupport) returns TaskService(Task(Either.right(applicationDataSeq)))
        mockPersistenceServices.fetchAppByPackages(any) returns TaskService(Task(Either.right(applicationSeq)))
        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.left(persistenceServiceException)))

        val result = deviceProcess.generateDockApps(size)(contextSupport).value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }
  }

  "Create Or Update Dock App" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.right(dockAppSeq)))
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result shouldEqual Right((): Unit)
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }

  }

  "Save DockApps" should {

    "return the three dockApps saved" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.right(dockAppSeq)))
        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beLike {
          case Right(resultSeqDockApp) =>
            resultSeqDockApp.size shouldEqual saveDockAppRequestSeq.size
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }

  }

  "Get Dock Apps" should {

    "get dock apps stored" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchDockApps returns TaskService(Task(Either.right(dockAppSeq)))
        val result = deviceProcess.getDockApps.value.run
        result must beLike {
          case Right(resultDockApp) =>
            resultDockApp map (_.name) shouldEqual (dockAppProcessSeq map (_.name))
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.fetchDockApps returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.getDockApps.value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }
  }

  "Delete All Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Either.right(dockAppsRemoved)))
        val result = deviceProcess.deleteAllDockApps().value.run
        result shouldEqual Right((): Unit)
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope {

        mockPersistenceServices.deleteAllDockApps() returns TaskService(Task(Either.left(persistenceServiceException)))
        val result = deviceProcess.deleteAllDockApps().value.run
        result must beAnInstanceOf[Left[DockAppException, _]]
      }
  }

  "getConfiguredNetworks" should {

    "returns all networks for a valid request" in
      new DeviceProcessScope {

        mockWifiServices.getConfiguredNetworks(contextSupport) returns TaskService(Task(Either.right(networks)))
        val result = deviceProcess.getConfiguredNetworks(contextSupport).value.run
        result shouldEqual Right(networks)
      }

  }

}
