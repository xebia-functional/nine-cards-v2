package com.fortysevendeg.ninecardslauncher.process.device

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.process.device.impl.DeviceProcessImpl
import com.fortysevendeg.ninecardslauncher.process.device.models.AppCategorized
import com.fortysevendeg.ninecardslauncher.process.utils.ApiUtils
import com.fortysevendeg.ninecardslauncher.services.api.models.GooglePlaySimplePackages
import com.fortysevendeg.ninecardslauncher.services.api.{GooglePlayPackagesResponse, GooglePlaySimplePackagesResponse, LoginResponse, ApiServices}
import com.fortysevendeg.ninecardslauncher.services.apps.AppsServices
import com.fortysevendeg.ninecardslauncher.services.image.{AppWebsite, AppPackagePath, AppPackage, ImageServices}
import com.fortysevendeg.ninecardslauncher.services.persistence.PersistenceServices
import org.specs2.matcher.DisjunctionMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.{-\/, \/-}
import scalaz.concurrent.Task

trait DeviceProcessSpecification
  extends Specification
  with Mockito {

  val exception = NineCardsException("")

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
      Task(\/-(applications))

    val mockApiServices = mock[ApiServices]

    val mockPersistenceServices = mock[PersistenceServices]

    mockPersistenceServices.fetchCacheCategories returns
      Task(\/-(cacheCategories))

    val mockImageServices = mock[ImageServices]

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Task(\/-(GooglePlaySimplePackagesResponse(200, GooglePlaySimplePackages(Seq.empty, Seq.empty))))

    mockApiServices.googlePlayPackages(any)(any) returns
      Task(\/-(GooglePlayPackagesResponse(200, Seq.empty)))

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Task(\/-(appPathResponses.head)),
      Task(\/-(appPathResponses(1))),
      Task(\/-(appPathResponses(2))))

    mockImageServices.saveAppIcon(any[AppWebsite])(any) returns
      Task(\/-(appWebsitePath))

    val deviceProcess = new DeviceProcessImpl(
      mockAppsServices,
      mockApiServices,
      mockPersistenceServices,
      mockImageServices) {

      override val apiUtils: ApiUtils = mock[ApiUtils]
      apiUtils.getRequestConfig(contextSupport) returns Task(\/-(requestConfig))

    }

  }

  trait ErrorAppServicesProcessScope {
    self: DeviceProcessScope =>

    mockAppsServices.getInstalledApps(contextSupport) returns
      Task(-\/(exception))

  }

  trait ErrorPersistenceServicesProcessScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.fetchCacheCategories returns
      Task(-\/(exception))

  }

  trait ErrorImageServicesProcessScope {
    self: DeviceProcessScope =>

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Task(\/-(appPathResponses.head)),
      Task(-\/(exception)),
      Task(\/-(appPathResponses(2))))

  }

  trait NoCachedDataScope {
    self: DeviceProcessScope =>

    mockPersistenceServices.addCacheCategory(any) returns
      Task(\/-(newCacheCategory))

    mockAppsServices.getInstalledApps(contextSupport) returns
      Task(\/-(applications :+ applicationNoCached))

    mockImageServices.saveAppIcon(any[AppPackage])(any) returns(
      Task(\/-(appPathResponses.head)),
      Task(\/-(appPathResponses(1))),
      Task(\/-(appPathResponses(2))),
      Task(\/-(appPackagePathNoCached)))

    mockApiServices.googlePlaySimplePackages(any)(any) returns
      Task(\/-(GooglePlaySimplePackagesResponse(200, GooglePlaySimplePackages(
        Seq.empty,
        Seq(googlePlaySimplePackageNoCached)))))

  }

  trait CreateImagesDataScope {
    self: DeviceProcessScope =>

    mockApiServices.googlePlayPackages(any)(any) returns
      Task(\/-(GooglePlayPackagesResponse(200, Seq(googlePlayPackage))))

  }

}

class DeviceProcessSpec
  extends DeviceProcessSpecification
  with DisjunctionMatchers {

  "Getting apps categorized in DeviceProcess" should {

    "returns apps categorized" in
      new DeviceProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run
        result must be_\/-[Seq[AppCategorized]].which {
          apps =>
            apps.length shouldEqual appsCategorized.length
            apps.head.packageName shouldEqual appsCategorized.head.packageName
            apps(1).packageName shouldEqual appsCategorized(1).packageName
            apps(2).packageName shouldEqual appsCategorized(2).packageName
        }
      }

    "returns apps categorized when a installed app isn't cached" in
      new DeviceProcessScope with NoCachedDataScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run
        result must be_\/-[Seq[AppCategorized]].which {
          apps =>
            val apps: Seq[AppCategorized] = appsCategorized :+ appCategorizedNoCached
            apps.length shouldEqual apps.length
            apps.head.packageName shouldEqual apps.head.packageName
            apps(1).packageName shouldEqual apps(1).packageName
            apps(2).packageName shouldEqual apps(2).packageName
            apps(3).packageName shouldEqual apps(3).packageName
        }
      }

    "returns a NineCardsException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run
        result must be_-\/[NineCardsException]
      }

    "returns a NineCardsException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run
        result must be_-\/[NineCardsException]
      }

    "returns a empty string if image service fails creating a image" in
      new DeviceProcessScope with ErrorImageServicesProcessScope {
        val result = deviceProcess.getCategorizedApps(contextSupport).run
        result must be_\/-[Seq[AppCategorized]].which {
          apps =>
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
        val result = deviceProcess.categorizeApps(contextSupport).run
        result must be_\/-[Unit]
      }

    "categorize installed apps when a installed app isn't cached" in
      new DeviceProcessScope with NoCachedDataScope {
        val result = deviceProcess.categorizeApps(contextSupport).run
        result must be_\/-[Unit]
      }

    "returns a NineCardsException if app service fails" in
      new DeviceProcessScope with ErrorAppServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run
        result must be_-\/[NineCardsException]
      }

    "returns a NineCardsException if persistence service fails" in
      new DeviceProcessScope with ErrorPersistenceServicesProcessScope {
        val result = deviceProcess.categorizeApps(contextSupport).run
        result must be_-\/[NineCardsException]
      }

  }

  "Create bitmaps for no packages installed" should {

    "when seq is empty" in
      new DeviceProcessScope {
        val result = deviceProcess.createBitmapsForNoPackagesInstalled(Seq.empty)(contextSupport).run
        result must be_\/-[Unit]
      }


    "when seq has packages" in
      new DeviceProcessScope with CreateImagesDataScope {
        val result = deviceProcess.createBitmapsForNoPackagesInstalled(Seq(packageNameForCreateImage))(contextSupport).run
        result must be_\/-[Unit]
      }

  }

}
