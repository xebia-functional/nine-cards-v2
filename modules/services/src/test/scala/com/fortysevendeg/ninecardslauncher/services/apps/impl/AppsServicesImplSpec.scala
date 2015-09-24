package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.R.attr
import android.content.Intent
import android.content.pm._
import android.content.res.Resources
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.apps.AppsInstalledException
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer, Result}

import scala.collection.JavaConversions._

trait AppsServicesImplSpecification
  extends Specification
  with Mockito {

  trait AppsServicesImplScope
    extends Scope
    with AppsServicesImplData {

    val androidFeedback = "com.google.android.feedback"

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleApp: Application) : ResolveInfo = {
      val sampleResolveInfo = mock[ResolveInfo]
      val mockActivityInfo = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.activityInfo = mockActivityInfo
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockApplicationInfo.packageName = sampleApp.packageName
      sampleResolveInfo
    }

    def createMockPackageInfo(sampleApp: Application): PackageInfo = {
      val samplePackageInfo = mock[PackageInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      samplePackageInfo.applicationInfo = mockApplicationInfo
      samplePackageInfo.packageName = sampleApp.packageName
      mockApplicationInfo.name = sampleApp.name
      mockApplicationInfo.className = sampleApp.className
      mockApplicationInfo.icon = sampleApp.resourceIcon
      samplePackageInfo.firstInstallTime = sampleApp.dateInstalled.asInstanceOf[Long]
      samplePackageInfo.lastUpdateTime = sampleApp.dateUpdate.asInstanceOf[Long]
      samplePackageInfo.versionCode = sampleApp.version.toInt
      samplePackageInfo
    }

    def createMockResourcesForApplication(sampleApp: Application): Resources = {
      val sampleResources = mock[Resources]
      sampleResources.getColor(attr.colorPrimary) returns sampleApp.colorPrimary.toInt
      sampleResources
    }

    val mockApps = List(createMockResolveInfo(sampleApp1), createMockResolveInfo(sampleApp2))
    val mockPackageInfo = List(createMockPackageInfo(sampleApp1), createMockPackageInfo(sampleApp2))
    val mockResources= List(createMockResourcesForApplication(sampleApp1), createMockResourcesForApplication(sampleApp2))

    val packageInfo1 = createMockPackageInfo(sampleApp1)
    val packageInfo2 = createMockPackageInfo(sampleApp2)

    packageManager.queryIntentActivities(mockIntent, 0) returns mockApps
    packageManager.getPackageInfo(sampleApp1.packageName, 0) returns packageInfo1
    packageManager.getPackageInfo(sampleApp2.packageName, 0) returns packageInfo2
    packageManager.getResourcesForApplication(packageInfo1.applicationInfo) returns createMockResourcesForApplication(sampleApp1)
    packageManager.getResourcesForApplication(packageInfo2.applicationInfo) returns createMockResourcesForApplication(sampleApp2)
    packageManager.getInstallerPackageName(sampleApp1.packageName) returns androidFeedback
    packageManager.getInstallerPackageName(sampleApp2.packageName) returns androidFeedback

    val mockAppsServicesImpl = new AppsServicesImpl {
      override def categoryLauncherIntent(): Intent = mockIntent
    }
  }

  trait AppsServicesImplErrorScope {
    self : AppsServicesImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.queryIntentActivities(mockIntent, 0) throws exception

  }

}

class AppsServicesImplSpec
  extends AppsServicesImplSpecification {

  "Apps Services" should {

    "getInstalledApplications" should {

      "returns the list of installed apps when they exist" in
        new AppsServicesImplScope {
          val result = mockAppsServicesImpl.getInstalledApplications(contextSupport).run.run
          result must beLike {
            case Answer(resultApplicationList) => resultApplicationList shouldEqual applicationList
          }
        }

      "returns an AppsInstalledException when no apps exist" in
        new AppsServicesImplScope with AppsServicesImplErrorScope {
          val result = mockAppsServicesImpl.getInstalledApplications(contextSupport).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, appsException)) => appsException must beLike {
                case e: AppsInstalledException => e.cause must beSome.which(_ shouldEqual exception)
              }
            }
          }
        }
    }

  }

}
