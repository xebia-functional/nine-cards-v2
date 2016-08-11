package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.commons.types.AppDockType
import com.fortysevendeg.ninecardslauncher.process.device._
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api._
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.calls.{CallsServices, CallsServicesException}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence._
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ShortcutServicesException, ShortcutsServices}
import com.fortysevendeg.ninecardslauncher.services.widgets.{WidgetServicesException, WidgetsServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait DeviceProcessSpecification
  extends Specification
  with Mockito {

  val appInstalledException = AppsInstalledException("")

  val apiServiceException = ApiServiceException("")

  val persistenceServiceException = PersistenceServiceException("")

  val bitmapTransformationException = BitmapTransformationExceptionImpl("")

  val shortcutServicesException = ShortcutServicesException("")

  val contactsServicesException = ContactsServiceException("")

  val fileServicesException = FileExceptionImpl("")

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
      Service(Task(Result.answer(applications)))

    mockAppsServices.getApplication(packageName1)(contextSupport) returns
      Service(Task(Result.answer(applications.head)))

    mockAppsServices.getDefaultApps(contextSupport) returns
      Service(Task(Result.answer(applications)))

    val mockApiServices = mock[ApiServices]

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Service(Task(Result.answer(GooglePlaySimplePackagesResponse(statusCodeOk, GooglePlaySimplePackages(Seq.empty, Seq.empty)))))

    mockApiServices.googlePlayPackages(any)(any) returns
      Service(Task(Result.answer(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))

    mockApiServices.googlePlayPackage(any)(any) returns
      Service(Task(Result.answer(GooglePlayPackageResponse(statusCodeOk, googlePlayPackage.app))))

    val mockShortcutsServices = mock[ShortcutsServices]

    mockShortcutsServices.getShortcuts(contextSupport) returns
      Service(Task(Result.answer(shortcuts)))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.deleteAllApps() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCollections() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCards() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllDockApps() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.fetchApps(any, any) returns
      Service(Task(Result.answer(appsPersistence)))

    mockPersistenceServices.fetchIterableApps(any, any) returns
      Service(Task(Result.answer(iterableCursorApps)))

    mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns
      Service(Task(Result.answer(iterableCursorApps)))

    mockPersistenceServices.fetchAlphabeticalAppsCounter returns
      Service(Task(Result.answer(appsCounters)))

    mockPersistenceServices.fetchCategorizedAppsCounter returns
      Service(Task(Result.answer(categoryCounters)))

    mockPersistenceServices.fetchInstallationDateAppsCounter returns
      Service(Task(Result.answer(installationAppsCounters)))

    mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns
      Service(Task(Result.answer(iterableCursorApps)))

    mockPersistenceServices.addApp(any[AddAppRequest]) returns(
      Service(Task(Result.answer(appsPersistence.head))),
      Service(Task(Result.answer(appsPersistence(1)))),
      Service(Task(Result.answer(appsPersistence(2)))))

    mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns
      Service(Task(Result.answer(appsPersistence.head)))

    mockPersistenceServices.deleteAppByPackage(any) returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.findAppByPackage(any) returns
      Service(Task(Result.answer(appsPersistence.headOption)))

    mockPersistenceServices.fetchAppByPackages(any) returns
      Service(Task(Result.answer(appsPersistence)))

    mockPersistenceServices.updateApp(any) returns
      Service(Task(Result.answer(items)))

    val mockContactsServices = mock[ContactsServices]

    mockContactsServices.getContacts returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.getFavoriteContacts returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.getContactsWithPhone returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.getIterableContacts returns
      Service(Task(Result.answer(iterableCursorContact)))

    mockContactsServices.getAlphabeticalCounterContacts returns
      Service(Task(Result.answer(contactsCounters)))

    mockContactsServices.getIterableFavoriteContacts returns
      Service(Task(Result.answer(iterableCursorContact)))

    mockContactsServices.getIterableContactsWithPhone returns
      Service(Task(Result.answer(iterableCursorContact)))

    mockContactsServices.getIterableContactsByKeyword(keyword) returns
      Service(Task(Result.answer(iterableCursorContact)))

    mockContactsServices.findContactByLookupKey("lookupKey 1") returns
      Service(Task(Result.answer(contacts.head)))
    mockContactsServices.findContactByLookupKey("lookupKey 2") returns
      Service(Task(Result.answer(contacts(1))))
    mockContactsServices.findContactByLookupKey("lookupKey 3") returns
      Service(Task(Result.answer(contacts(2))))

    val mockImageServices = mock[ImageServices]

    val mockWidgetsServices = mock[WidgetsServices]

    mockWidgetsServices.getWidgets(any) returns
      Service(Task(Result.answer(widgetsServices)))

    val mockCallsServices = mock[CallsServices]

    mockCallsServices.getLastCalls returns
      Service(Task(Result.answer(callsServices)))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber1) returns
      Service(Task(Result.answer(Some(callsContacts(0)))))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber2) returns
      Service(Task(Result.answer(Some(callsContacts(1)))))

    mockContactsServices.fetchContactByPhoneNumber(phoneNumber3) returns
      Service(Task(Result.answer(Some(callsContacts(2)))))

    val deviceProcess = new DeviceProcessImpl(
      mockAppsServices,
      mockApiServices,
      mockPersistenceServices,
      mockShortcutsServices,
      mockContactsServices,
      mockImageServices,
      mockWidgetsServices,
      mockCallsServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        Service(Task(Result.answer(requestConfig)))

    }

  }

  trait ErrorAppServicesProcessScope {
    self: DeviceProcessScope =>

    mockAppsServices.getInstalledApplications(contextSupport) returns Service {
      Task(Errata(appInstalledException))
    }

    mockAppsServices.getApplication(packageName1)(contextSupport) returns Service {
      Task(Errata(appInstalledException))
    }

    mockAppsServices.getDefaultApps(contextSupport) returns Service {
      Task(Errata(appInstalledException))
    }

  }

  trait ErrorApiServicesProcessScope {
    self: DeviceProcessScope =>

    mockApiServices.googlePlayPackages(any)(any) returns Service {
      Task(Errata(apiServiceException))
    }

    mockApiServices.googlePlayPackage(any)(any) returns Service {
      Task(Errata(apiServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteAppsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns Service {
      Task(Errata(persistenceServiceException))
    }
  }

  trait ErrorPersistenceServicesDeleteWidgetsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllWidgets() returns Service {
      Task(Errata(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteCollectionsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCollections returns Service {
      Task(Errata(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteCardsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCollections returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCards returns Service {
      Task(Errata(persistenceServiceException))
    }

  }

  trait ErrorPersistenceServicesDeleteDockAppsProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.deleteAllApps() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllWidgets() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCollections() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllCards() returns
      Service(Task(Result.answer(items)))

    mockPersistenceServices.deleteAllDockApps() returns Service {
      Task(Errata(persistenceServiceException))
    }
  }

  trait ErrorPersistenceServicesProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchApps(any, any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableApps(any, any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableAppsByCategory(any, any, any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchAlphabeticalAppsCounter returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchCategorizedAppsCounter returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchInstallationDateAppsCounter returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchIterableAppsByKeyword(any, any, any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.addApp(any[AddAppRequest]) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.addApps(any[Seq[AddAppRequest]]) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.deleteAppByPackage(any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.findAppByPackage(any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.updateApp(any) returns Service {
      Task(Errata(persistenceServiceException))
    }

  }

  trait ShortcutsErrorScope {
    self: DeviceProcessScope =>

    mockShortcutsServices.getShortcuts(contextSupport) returns Service {
      Task(Errata(shortcutServicesException))
    }

  }

  trait FavoriteContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getFavoriteContacts returns Service {
      Task(Errata(contactsServicesException))
    }

  }

  trait FilledFavoriteContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getFavoriteContacts returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.findContactByLookupKey(any) returns Service {
      Task(Errata(contactsServicesException))
    }

  }

  trait SaveShortcutScope {
    self: DeviceProcessScope =>

    val saveBitmap = SaveBitmap(bitmap = mockBitmap, bitmapResize = None)

    val saveBitmapPath = SaveBitmapPath(nameShortcut, fileNameShortcut)

    mockImageServices.saveBitmap(saveBitmap)(contextSupport) returns
      Service(Task(Result.answer(saveBitmapPath)))
  }

  trait SaveShortcutErrorScope {
    self: DeviceProcessScope =>

    mockImageServices.saveBitmap(any[SaveBitmap])(any) returns Service {
      Task(Errata(fileServicesException))
    }
  }

  trait FindContactScope {
    self: DeviceProcessScope =>

    mockContactsServices.findContactByLookupKey(anyString) returns
      Service(Task(Result.answer(contact)))
  }

  trait ContactsErrorScope {
    self: DeviceProcessScope =>

    mockContactsServices.getContacts returns Service {
      Task(Errata(contactsServicesException))
    }

    mockContactsServices.getIterableContacts returns Service {
      Task(Errata(contactsServicesException))
    }

    mockContactsServices.findContactByLookupKey(anyString) returns Service {
      Task(Errata(contactsServicesException))
    }

    mockContactsServices.getIterableContactsByKeyword(keyword) returns Service {
      Task(Errata(contactsServicesException))
    }
  }

  trait WidgetsErrorScope {
    self: DeviceProcessScope =>

    mockWidgetsServices.getWidgets(any) returns Service {
      Task(Errata(widgetsServicesException))
    }
  }

  trait CallsErrorScope {
    self: DeviceProcessScope =>

    mockCallsServices.getLastCalls returns Service {
      Task(Errata(callsServicesException))
    }
  }

  trait CallsContactsErrorScope {
    self: DeviceProcessScope =>

    mockCallsServices.getLastCalls returns
      Service(Task(Result.answer(callsServices)))

    mockContactsServices.fetchContactByPhoneNumber(any) returns Service {
      Task(Errata(contactsServicesException))
    }
  }

  trait DockAppsScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.createOrUpdateDockApp(any) returns
      Service(Task(Result.answer(dockAppSeq)))

    mockPersistenceServices.fetchDockApps returns
      Service(Task(Result.answer(dockAppSeq)))

    mockPersistenceServices.deleteAllDockApps() returns
      Service(Task(Result.answer(dockAppsRemoved)))
  }

  trait DockAppsFindErrorScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchAppByPackages(any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.createOrUpdateDockApp(any) returns
      Service(Task(Result.answer(dockAppSeq)))
  }

  trait DockAppsErrorScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.createOrUpdateDockApp(any) returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.fetchDockApps returns Service {
      Task(Errata(persistenceServiceException))
    }

    mockPersistenceServices.deleteAllDockApps() returns Service {
      Task(Errata(persistenceServiceException))
    }
  }

}

class DeviceProcessImplSpec
  extends DeviceProcessSpecification {

  "Delete saved items" should {

    "deletes all apps, cards, collections and dockApps" in
      new DeviceProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Answer(result) =>
            result shouldEqual ((): Unit)
        }
      }

    "returns ResetException when persistence service fails deleting the apps" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteAppsProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ResetException]
          }
        }
      }

    "returns ResetException when persistence service fails deleting widgets" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteWidgetsProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ResetException]
          }
        }
      }

    "returns ResetException when persistence service fails deleting the collections" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteCollectionsProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ResetException]
          }
        }
      }

    "returns ResetException when persistence service fails deleting the cards" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteCardsProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ResetException]
          }
        }
      }

    "returns ResetException when persistence service fails deleting the dock apps" in
      new DeviceProcessScope with ErrorPersistenceServicesDeleteDockAppsProcessScope {
        val result = deviceProcess.resetSavedItems().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ResetException]
          }
        }
      }

  }

  "Get Shortcuts" should {

    "get available Shortcuts" in
      new DeviceProcessScope {
        val result = deviceProcess.getAvailableShortcuts(contextSupport).run.run
        result must beLike {
          case Answer(r) => r.map(_.title) shouldEqual shortcuts.map(_.title)
        }
      }

    "returns ShortcutException when ShortcutsServices fails" in
      new DeviceProcessScope with ShortcutsErrorScope {
        val result = deviceProcess.getAvailableShortcuts(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ShortcutException]
          }
        }
      }

  }

  "Get Favorite Contacts" should {

    "get favorite contacts" in
      new DeviceProcessScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).run.run
        result must beLike {
          // TODO - This is a workaround and need to be fixed in ticket 9C-284
          case Answer(r) => r.map(_.name).sorted shouldEqual contacts.map(_.name).sorted
        }
      }

    "returns ContactException when ContactsServices fails getting the favorite contacts" in
      new DeviceProcessScope with FavoriteContactsErrorScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ContactException]
          }
        }
      }

    "returns an empty list if ContactsServices fails filling the contacts" in
      new DeviceProcessScope with FilledFavoriteContactsErrorScope {
        val result = deviceProcess.getFavoriteContacts(contextSupport).run.run
        result must beLike {
          case Answer(resultContacts) =>
            resultContacts shouldEqual Seq()
        }
      }

  }

  "getCounterForIterableContacts" should {

    "get term counters for contacts by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForContacts()(contextSupport).run.run
        result must beLike {
          case Answer(counters) =>
            counters map (_.term) shouldEqual (contactsCounters map (_.term))
        }
        there was one(mockContactsServices).getAlphabeticalCounterContacts
      }

    "get term counters for contacts by favorite" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForContacts(FavoriteContacts)(contextSupport).run.run
        result must beLike {
          case Answer(counters) => counters shouldEqual Seq.empty
        }
      }

    "get term counters for apps by contacts with phone number" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForContacts(ContactsWithPhoneNumber)(contextSupport).run.run
        result must beLike {
          case Answer(counters) => counters shouldEqual Seq.empty
        }
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Save shortcut icon" should {

    "get path of icon stored" in
      new DeviceProcessScope with SaveShortcutScope {
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).run.run
        result must beLike {
          case Answer(path) => path shouldEqual fileNameShortcut
        }
      }

    "returns ShortcutException when ImageServices fails storing the icon" in
      new DeviceProcessScope with SaveShortcutErrorScope {
        val result = deviceProcess.saveShortcutIcon(mockBitmap)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ShortcutException]
          }
        }
      }
  }

  "Get Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts()(contextSupport).run.run
        result must beLike {
          case Answer(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts(FavoriteContacts)(contextSupport).run.run
        result must beLike {
          case Answer(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts(ContactsWithPhoneNumber)(contextSupport).run.run
        result must beLike {
          case Answer(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getContacts()(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ContactException]
          }
        }
      }

  }

  "Get Iterable Contacts Sorted By Name" should {

    "get all contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts()(contextSupport).run.run
        result must beLike {
          case Answer(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get favorite contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts(FavoriteContacts)(contextSupport).run.run
        result must beLike {
          case Answer(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "get contacts with phone number sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContacts(ContactsWithPhoneNumber)(contextSupport).run.run
        result must beLike {
          case Answer(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getIterableContacts()(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ContactException]
          }
        }
      }

  }

  "Get Contact" should {

    "get contact find a contact with data info filled" in
      new DeviceProcessScope with FindContactScope {
        val result = deviceProcess.getContact(lookupKey)(contextSupport).run.run
        result must beLike {
          case Answer(response) =>
            response.lookupKey shouldEqual lookupKey
            response.info must beSome
        }
      }

    "returns ContactException when ContactsService fails getting contact" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getContact(lookupKey)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ContactException]
          }
        }
      }

  }

  "Get Iterable Contacts by keyword" should {

    "get contacts by keyword" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).run.run
        result must beLike {
          case Answer(iter) => iter.moveToPosition(0) shouldEqual iterableContact.moveToPosition(0)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getIterableContactsByKeyWord(keyword)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ContactException]
          }
        }
      }

  }

  "Get Saved Apps" should {

    "get saved apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByName, ascending = true)
      }

    "get saved apps by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByInstallDate)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByInstallDate, ascending = false)
      }

    "get saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getSavedApps(GetByCategory)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual apps
        }
        there was one(mockPersistenceServices).fetchApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getSavedApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Get Iterable Saved Apps" should {

    "get iterable saved apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByName, ascending = true)
      }

    "get iterable saved apps by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByInstallDate)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByInstallDate, ascending = false)
      }

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableApps(GetByCategory)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableApps(OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Get Iterable Saved Apps By Category" should {

    "get iterable saved apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByCategory(category, OrderByName, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableAppsByCategory(category)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "getTermCountersForApps" should {

    "get term counters for apps by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Answer(counters) =>
            counters map (_.term) shouldEqual (appsCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchAlphabeticalAppsCounter
      }

    "get term counters for apps by installation date" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).run.run
        result must beLike {
          case Answer(counters) =>
            counters map (_.term) shouldEqual (installationAppsCounters map (_.term))
        }
      }

    "get term counters for apps by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).run.run
        result must beLike {
          case Answer(counters) =>
            counters map (_.term) shouldEqual (categoryCounters map (_.term))
        }
        there was one(mockPersistenceServices).fetchCategorizedAppsCounter
      }

    "returns AppException if persistence service fails in GetByName" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

    "returns AppException if persistence service fails in GetByCategory" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByCategory)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

    "returns AppException if persistence service fails in GetByInstallDate" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getTermCountersForApps(GetByInstallDate)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Get Iterable Apps by keyword" should {

    "get iterable apps ordered by name" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByName, ascending = true)
      }

    "get iterable apps ordered by update date" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByInstallDate)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByInstallDate, ascending = false)
      }

    "get iterable apps ordered by category" in
      new DeviceProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByCategory)(contextSupport).run.run
        result must beLike {
          case Answer(iter) =>
            iter.moveToPosition(0) shouldEqual iterableApps.moveToPosition(0)
        }
        there was one(mockPersistenceServices).fetchIterableAppsByKeyword(keyword, OrderByCategory, ascending = true)
      }

    "returns AppException if persistence service fails " in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getIterableAppsByKeyWord(keyword, GetByName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }
  }

  "Getting and saving installed apps" should {

    "gets and saves installed apps" in
      new DeviceProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns a AppException if persistence service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.saveInstalledApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Getting and saving an installed app" should {

    "gets and saves an installed app" in
      new DeviceProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.saveApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Deleting an app" should {

    "deletes an app" in
      new DeviceProcessScope {
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.deleteApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Updating an installed app" should {

    "gets and saves one installed app" in
      new DeviceProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an empty Answer if api service fails" in
      new DeviceProcessScope with ErrorApiServicesProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual ((): Unit)
        }
      }

    "returns an AppException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.updateApp(packageName1)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppException]
          }
        }
      }

  }

  "Get Widgets" should {

    "get widgets" in
      new DeviceProcessScope {
        val result = deviceProcess.getWidgets(contextSupport).run.run
        result must beLike {
          case Answer(resultWidgets) =>
            resultWidgets shouldEqual appWithWidgets
        }
      }

    "returns WidgetException if WidgetServices fail getting the Widgets " in
      new DeviceProcessScope with WidgetsErrorScope {
        val result = deviceProcess.getWidgets(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[WidgetException]
          }
        }
      }

  }

  "Get Last Calls" should {

    "get last calls" in
      new DeviceProcessScope {
        val result = deviceProcess.getLastCalls(contextSupport).run.run
        result must beLike {
          case Answer(resultLastCalls) =>
            resultLastCalls shouldEqual lastCallsContacts
        }
      }

    "returns CallsException if CallsServices fail getting the calls " in
      new DeviceProcessScope with CallsErrorScope {
        val result = deviceProcess.getLastCalls(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[CallException]
          }
        }
      }

    "returns an empty List if ContactsServices fail getting the contacts " in
      new DeviceProcessScope with CallsContactsErrorScope {
        val result = deviceProcess.getLastCalls(contextSupport).run.run
        result must beLike {
          case Answer(resultApps) =>
            resultApps shouldEqual Seq()
        }
      }

  }

  "Generate Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).run.run
        result must beLike {
          case Answer(resultDockApp) =>
            resultDockApp shouldEqual dockAppProcessSeq
        }
      }

    "returns DockAppException when AppService fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }

    "returns DockAppException when PersistenceService fails fetching the apps" in
      new DeviceProcessScope with DockAppsFindErrorScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }

    "returns DockAppException when PersistenceService fails saving the apps" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.generateDockApps(size)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }
  }

  "Create Or Update Dock App" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).run.run
        result must beLike {
          case Answer(resultDockApp) =>
            resultDockApp shouldEqual ((): Unit)
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.createOrUpdateDockApp(name1, AppDockType, intent, imagePath1, 0).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }

  }

  "Save DockApps" should {

    "return the three dockApps saved" in
      new DeviceProcessScope with DockAppsScope {

        mockPersistenceServices.createOrUpdateDockApp(any) returns Service(Task(Result.answer(dockAppSeq)))

        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).run.run
        result must beLike {
          case Answer(resultSeqDockApp) =>
            resultSeqDockApp.size shouldEqual saveDockAppRequestSeq.size
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {

        val result = deviceProcess.saveDockApps(saveDockAppRequestSeq).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppException]
          }
        }
      }

  }

  "Get Dock Apps" should {

    "get dock apps stored" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.getDockApps.run.run
        result must beLike {
          case Answer(resultDockApp) =>
            resultDockApp map (_.name) shouldEqual (dockAppProcessSeq map (_.name))
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.getDockApps.run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }
  }

  "Delete All Dock Apps" should {

    "returns a empty answer for a valid request" in
      new DeviceProcessScope with DockAppsScope {
        val result = deviceProcess.deleteAllDockApps().run.run
        result must beLike {
          case Answer(resultDockApp) =>
            resultDockApp shouldEqual ((): Unit)
        }
      }

    "returns DockAppException when PersistenceService fails" in
      new DeviceProcessScope with DockAppsErrorScope {
        val result = deviceProcess.deleteAllDockApps().run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[DockAppExceptionImpl]
          }
        }
      }
  }

}
