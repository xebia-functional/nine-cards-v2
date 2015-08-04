package com.fortysevendeg.ninecardslauncher.process.device.impl

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.process.device.AppCategorizationException
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.api.{ApiServices, GooglePlayPackagesResponse, GooglePlaySimplePackagesResponse}
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import com.fortysevendeg.ninecardslauncher.services.apps.{AppsInstalledException, AppsServices}
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.persistence.models.CacheCategory
import com.fortysevendeg.ninecardslauncher.services.persistence.{PersistenceServiceException, PersistenceServices}
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait DeviceProcessSpecification
  extends Specification
  with Mockito {

  val appCategorizationException = AppCategorizationException("")

  val appInstalledException = AppsInstalledException("")

  val persistenceServiceException = PersistenceServiceException("")

  val bitmapTransformationException = BitmapTransformationException("")

  trait DeviceProcessScope
    extends Scope
    with DeviceProcessData {

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getResources returns resources

    val mockAppsServices = mock[AppsServices]

    mockAppsServices.getInstalledApps(contextSupport) returns
      Service(Task(Result.answer(applications)))

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.fetchCacheCategories returns
      Service(Task(Result.answer(cacheCategories)))

    val mockImageServices = mock[ImageServices]

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Service(Task(Result.answer(GooglePlaySimplePackagesResponse(200, GooglePlaySimplePackages(Seq.empty, Seq.empty)))))

    mockApiServices.googlePlayPackages(any)(any) returns
      Service(Task(Result.answer(GooglePlayPackagesResponse(200, Seq.empty))))

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
      mockImageServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns
        Service(Task(Result.answer(requestConfig)))

    }

  }

  trait ErrorAppServicesProcessScope {
    self: DeviceProcessScope =>

    val serviceAppInstalledException = Service(
      Task(
        Result.errata[Seq[Application], AppsInstalledException](appInstalledException)
      )
    )

    mockAppsServices.getInstalledApps(contextSupport) returns
      serviceAppInstalledException

  }

  trait ErrorPersistenceServicesProcessScope {
    self: DeviceProcessScope =>

    val serviceAppInstalledException = Service(
      Task(
        Result.errata[Seq[CacheCategory], PersistenceServiceException](persistenceServiceException)
      )
    )

    mockPersistenceServices.fetchCacheCategories returns
      serviceAppInstalledException

  }

  trait ErrorImageServicesProcessScope {
    self: DeviceProcessScope =>

//    case class CustomException(message: String, cause: Option[Throwable] = None)
//      extends IOException(message)
//      with BitmapTransformationException
//
//    val serviceBitmapTransformationException = Service(
//      Task(
//        Errata(CustomException(""))
//      )
//    )
//
//    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
//      Service(Task(Result.answer(appPathResponses.head))),
//      serviceBitmapTransformationException,
//      Service(Task(Result.answer(appPathResponses(2)))))

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

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Service(Task(Result.answer(GooglePlaySimplePackagesResponse(200, GooglePlaySimplePackages(
        Seq.empty,
        Seq(googlePlaySimplePackageNoCached))))))

  }

  trait CreateImagesDataScope {
    self: DeviceProcessScope =>

    mockApiServices.googlePlayPackages(any)(any) returns
      Service(Task(Result.answer(GooglePlayPackagesResponse(200, Seq(googlePlayPackage)))))

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
            apps.head.packageName shouldEqual appsCategorized.head.packageName
            apps(1).packageName shouldEqual appsCategorized(1).packageName
            apps(2).packageName shouldEqual appsCategorized(2).packageName
        }
      }

    "returns apps categorized when a installed app isn't cached" in
      new DeviceProcessScope with NoCachedDataScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Answer(apps) =>
            val appsCategorizedAndNoCached: Seq[AppCategorized] = appsCategorized :+ appCategorizedNoCached
            apps.length shouldEqual apps.length
            apps.head.packageName shouldEqual appsCategorizedAndNoCached.head.packageName
            apps(1).packageName shouldEqual appsCategorizedAndNoCached(1).packageName
            apps(2).packageName shouldEqual appsCategorizedAndNoCached(2).packageName
            apps(3).packageName shouldEqual appsCategorizedAndNoCached(3).packageName
        }
      }

    "returns a NineCardsException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual appInstalledException
          }
        }
      }

    "returns a NineCardsException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual persistenceServiceException
          }
        }
      }

    "returns a empty string if image service fails creating a image" in
      new DeviceProcessScope with ErrorImageServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run.run
        result must beLike {
          case Answer(apps) =>
            apps.length shouldEqual appsCategorized.length
            apps.head.packageName shouldEqual appsCategorized.head.packageName
            apps(1).packageName shouldEqual appsCategorized(1).packageName
            apps(2).packageName shouldEqual appsCategorized(2).packageName

            apps.head.imagePath shouldEqual appsCategorized.head.imagePath
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

    "returns a NineCardsException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual appInstalledException
          }
        }
      }

    "returns a NineCardsException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual appCategorizationException
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

}
