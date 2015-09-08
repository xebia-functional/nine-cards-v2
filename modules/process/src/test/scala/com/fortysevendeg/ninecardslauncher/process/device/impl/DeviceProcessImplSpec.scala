package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.device.{ContactException, ShortcutException, AppCategorizationException}
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServices, GooglePlayPackagesResponse, GooglePlaySimplePackagesResponse}
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.contacts.{ContactsServiceException, ContactsServices}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import com.fortysevendeg.ninecardslauncher.services.shortcuts.{ShortcutServicesException, ShortcutsServices}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait DeviceProcessSpecification
  extends Specification
  with Mockito {

  val appInstalledException = AppsInstalledException("")

  val persistenceServiceException = PersistenceServiceException("")

  val bitmapTransformationException = BitmapTransformationExceptionImpl("")

  val shortcutServicesException = ShortcutServicesException("")

  val contactsServicesException = ContactsServiceException("")

  val fileServicesException = FileExceptionImpl("")

  trait DeviceProcessScope
    extends Scope
    with DeviceProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val mockPackageManager = mock[PackageManager]
    mockPackageManager.getActivityIcon(any[ComponentName]) returns null

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mockPackageManager
    contextSupport.getResources returns resources

    val mockBitmap = mock[Bitmap]

    val mockAppsServices = mock[AppsServices]

    mockAppsServices.getInstalledApps(contextSupport) returns
      Service(Task(Result.answer(applications)))

    val mockApiServices = mock[ApiServices]

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Service(Task(Result.answer(GooglePlaySimplePackagesResponse(statusCodeOk, GooglePlaySimplePackages(Seq.empty, Seq.empty)))))

    mockApiServices.googlePlayPackages(any)(any) returns
      Service(Task(Result.answer(GooglePlayPackagesResponse(statusCodeOk, Seq.empty))))

    val mockShortcutsServices = mock[ShortcutsServices]

    mockShortcutsServices.getShortcuts(contextSupport) returns
      Service(Task(Result.answer(shortcuts)))

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.fetchCacheCategories returns
      Service(Task(Result.answer(cacheCategories)))

    val mockContactsServices = mock[ContactsServices]

    mockContactsServices.getContacts returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.getFavoriteContacts returns
      Service(Task(Result.answer(contacts)))

    mockContactsServices.findContactByLookupKey("lookupKey 1") returns
      Service(Task(Result.answer(contacts.head)))
    mockContactsServices.findContactByLookupKey("lookupKey 2") returns
      Service(Task(Result.answer(contacts(1))))
    mockContactsServices.findContactByLookupKey("lookupKey 3") returns
      Service(Task(Result.answer(contacts(2))))

    val mockImageServices = mock[ImageServices]

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Service(Task(Result.answer(appPathResponses.head))),
      Service(Task(Result.answer(appPathResponses(1)))),
      Service(Task(Result.answer(appPathResponses(2)))))

    mockImageServices.saveAppIcon(any[AppWebsite])(any) returns
      Service(Task(Result.answer(appWebsitePath)))

    val deviceProcess = new DeviceProcessImpl(
      mockAppsServices,
      mockApiServices,
      mockPersistenceServices,
      mockShortcutsServices,
      mockContactsServices,
      mockImageServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        Service(Task(Result.answer(requestConfig)))

    }

  }

  trait ErrorAppServicesProcessScope {
    self: DeviceProcessScope =>

    mockAppsServices.getInstalledApps(contextSupport) returns Service {
      Task(Errata(appInstalledException))
    }

  }

  trait ErrorPersistenceServicesProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchCacheCategories returns Service {
      Task(Errata(persistenceServiceException))
    }

  }

  trait ErrorImageServicesProcessScope {
    self: DeviceProcessScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)
      with FileException
      with BitmapTransformationException

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Service(Task(Result.answer(appPathResponses.head))),
      Service(Task(Errata(CustomException("")))),
      Service(Task(Result.answer(appPathResponses(2)))))

  }

  trait NoCachedDataScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.addCacheCategory(any) returns
      Service(Task(Result.answer(newCacheCategory)))

    mockAppsServices.getInstalledApps(contextSupport) returns
      Service(Task(Result.answer(applications :+ applicationNoCached)))

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Service(Task(Result.answer(appPathResponses.head))),
      Service(Task(Result.answer(appPathResponses(1)))),
      Service(Task(Result.answer(appPathResponses(2)))),
      Service(Task(Result.answer(appPackagePathNoCached))))

    mockApiServices.googlePlaySimplePackages(any)(any) returns Service {
      Task(
        Result.answer(
          GooglePlaySimplePackagesResponse(
            statusCodeOk,
            GooglePlaySimplePackages(
              Seq.empty,
              Seq(googlePlaySimplePackageNoCached))
          )
        )
      )
    }

  }

  trait CreateImagesDataScope {
    self: DeviceProcessScope =>

    mockApiServices.googlePlayPackages(any)(any) returns Service {
      Task(Result.answer(GooglePlayPackagesResponse(statusCodeOk, Seq(googlePlayPackage))))
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

    val saveBitmap = SaveBitmap(nameShortcut, mockBitmap)

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

    mockContactsServices.findContactByLookupKey(anyString) returns Service {
      Task(Errata(contactsServicesException))
    }
  }

}

class DeviceProcessImplSpec
  extends DeviceProcessSpecification {

  "Getting apps categorized in DeviceProcess" should {

    "returns apps categorized" in
      new DeviceProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Answer(apps) =>
            apps.length shouldEqual appsCategorized.length
            apps.map(_.packageName) shouldEqual appsCategorized.map(_.packageName)
        }
      }

    "returns apps categorized when a installed app isn't cached" in
      new DeviceProcessScope with NoCachedDataScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Answer(apps) =>
            val appsCategorizedAndNoCached: Seq[AppCategorized] = appsCategorized :+ appCategorizedNoCached
            apps.length shouldEqual apps.length
            apps.map(_.packageName) shouldEqual appsCategorizedAndNoCached.map(_.packageName)
        }
      }

    "returns a AppCategorizationException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppCategorizationException]
          }
        }
      }

    "returns a AppCategorizationException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppCategorizationException]
          }
        }
      }

    "returns a empty string if image service fails creating a image" in
      new DeviceProcessScope with ErrorImageServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Answer(apps) =>
            apps.length shouldEqual appsCategorized.length
            apps.map(_.packageName) shouldEqual appsCategorized.map(_.packageName)

            apps(0).imagePath shouldEqual appsCategorized(0).imagePath
            apps(1).imagePath shouldEqual None
            apps(2).imagePath shouldEqual appsCategorized(2).imagePath
        }
      }

  }

  "Categorize in DeviceProcess" should {

    "categorize installed apps" in
      new DeviceProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }

    "categorize installed apps when a installed app isn't cached" in
      new DeviceProcessScope with NoCachedDataScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }

    "returns a AppCategorizationException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppCategorizationException]
          }
        }
      }

    "returns a AppCategorizationException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[AppCategorizationException]
          }
        }
      }

  }

  "Create bitmaps for no packages installed" should {

    "when seq is empty" in
      new DeviceProcessScope {
        val result = deviceProcess.createBitmapsFromPackages(Seq.empty)(contextSupport).run.run
        result must beLike {
          case Answer(r) => r shouldEqual (())
        }
      }


    "when seq has packages" in
      new DeviceProcessScope with CreateImagesDataScope {
        val result = deviceProcess.createBitmapsFromPackages(Seq(packageNameForCreateImage))(contextSupport).run.run
        result must beLike {
          case Answer(r) => r shouldEqual (())
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
          case Answer(r) => r.map(_.name) shouldEqual contacts.map(_.name)
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

  "Save shortcut icon" should {

    "get path of icon stored" in
      new DeviceProcessScope with SaveShortcutScope {
        val result = deviceProcess.saveShortcutIcon(nameShortcut, mockBitmap)(contextSupport).run.run
        result must beLike {
          case Answer(path) => path shouldEqual fileNameShortcut
        }
      }

    "returns ShortcutException when ImageServices fails storing the icon" in
      new DeviceProcessScope with SaveShortcutErrorScope {
        val result = deviceProcess.saveShortcutIcon(nameShortcut, mockBitmap)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[ShortcutException]
          }
        }
      }
  }

  "Get Contacts Sorted By Name" should {

    "get contacts sorted" in
      new DeviceProcessScope {
        val result = deviceProcess.getContacts(contextSupport).run.run
        result must beLike {
          case Answer(response) => response.map(_.name) shouldEqual contacts.map(_.name)
        }
      }

    "returns ContactException when ContactsService fails getting contacts" in
      new DeviceProcessScope with ContactsErrorScope {
        val result = deviceProcess.getContacts(contextSupport).run.run
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

}
