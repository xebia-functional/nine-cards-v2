package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.content.Intent
import android.content.pm._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.apps.AppsInstalledException
import com.fortysevendeg.ninecardslauncher.services.apps.models.Application
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}

import scala.collection.JavaConversions._

trait AppsServicesImplSpecification
  extends Specification
  with Mockito {

  trait AppsServicesImplScope
    extends Scope
    with AppsServicesImplData {

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleApp: Application) : ResolveInfo = {
      val sampleResolveInfo = mock[ResolveInfo]
      val mockActivityInfo = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.activityInfo = mockActivityInfo
      sampleResolveInfo.activityInfo.icon = sampleApp.resourceIcon
      mockActivityInfo.name = sampleApp.className
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockApplicationInfo.packageName = sampleApp.packageName
      sampleResolveInfo.loadLabel(packageManager) returns sampleApp.name
      sampleResolveInfo
    }

    def createMockPackageInfo(sampleApp: Application): PackageInfo = {
      val samplePackageInfo = mock[PackageInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      samplePackageInfo.applicationInfo = mockApplicationInfo
      samplePackageInfo.packageName = sampleApp.packageName
      mockApplicationInfo.name = sampleApp.name
      mockApplicationInfo.className = sampleApp.className
      samplePackageInfo.firstInstallTime = sampleApp.dateInstalled
      samplePackageInfo.lastUpdateTime = sampleApp.dateUpdate
      samplePackageInfo.versionCode = sampleApp.version.toInt
      samplePackageInfo
    }

    val mockApps = List(createMockResolveInfo(sampleApp1), createMockResolveInfo(sampleApp2))
    val mockPackageInfo = List(createMockPackageInfo(sampleApp1), createMockPackageInfo(sampleApp2))

    val packageInfo1 = createMockPackageInfo(sampleApp1)
    val packageInfo2 = createMockPackageInfo(sampleApp2)

    packageManager.queryIntentActivities(mockIntent, 0) returns mockApps
    packageManager.getPackageInfo(sampleApp1.packageName, 0) returns packageInfo1
    packageManager.getPackageInfo(sampleApp2.packageName, 0) returns packageInfo2
    packageManager.getInstallerPackageName(sampleApp1.packageName) returns androidFeedback
    packageManager.getInstallerPackageName(sampleApp2.packageName) returns androidFeedback
    packageManager.getLaunchIntentForPackage(sampleApp1.packageName) returns mockIntent
    packageManager.resolveActivity(mockIntent, 0) returns mockApps.head

    val mockAppsServicesImpl = new AppsServicesImpl {
      override def mainIntentByCategory(category: String): Intent = mockIntent

      override def phoneIntent(): Intent = mockIntent

      override def cameraIntent(): Intent = mockIntent
    }
  }

  trait AppsServicesImplErrorScope {
    self : AppsServicesImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.queryIntentActivities(mockIntent, 0) throws exception

  }

  trait AppsServicesImplInvalidPackageNameErrorScope {
    self : AppsServicesImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.getLaunchIntentForPackage(invalidPackageName) throws exception

  }

  trait AppsServicesImplResolveActivityErrorScope {
    self : AppsServicesImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.resolveActivity(mockIntent, 0) throws exception
  }

  trait AppsServicesImplPackageInfoErrorScope {
    self : AppsServicesImplScope =>

    case class CustomException(message: String, cause: Option[Throwable] = None)
      extends RuntimeException(message)

    val exception = CustomException("")

    packageManager.getPackageInfo(sampleApp1.packageName, 0) throws exception
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

    "getApplication" should {

      "returns the installed app when a valid packageName is provided" in
        new AppsServicesImplScope {
          val result = mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).run.run
          result must beLike {
            case Answer(resultApplication) => resultApplication shouldEqual sampleApp1
          }
        }

      "returns an AppsInstalledException when an invalid packageName is provided" in
        new AppsServicesImplScope with AppsServicesImplInvalidPackageNameErrorScope {
          val result = mockAppsServicesImpl.getApplication(invalidPackageName)(contextSupport).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, appsException)) => appsException must beLike {
                case e: AppsInstalledException => e.cause must beSome.which(_ shouldEqual exception)
              }
            }
          }
        }

      "returns an AppsInstalledException when the resolveActivity method fails" in
        new AppsServicesImplScope with AppsServicesImplResolveActivityErrorScope {
          val result = mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, appsException)) => appsException must beLike {
                case e: AppsInstalledException => e.cause must beSome.which(_ shouldEqual exception)
              }
            }
          }
        }

      "returns an AppsInstalledException when the getPackageInfo method fails" in
        new AppsServicesImplScope with AppsServicesImplPackageInfoErrorScope {
          val result = mockAppsServicesImpl.getApplication(validPackageName)(contextSupport).run.run
          result must beLike {
            case Errata(e) => e.headOption must beSome.which {
              case (_, (_, appsException)) => appsException must beLike {
                case e: AppsInstalledException => e.cause must beSome.which(_ shouldEqual exception)
              }
            }
          }
        }
    }

    "getDefaultApps" should {

      "returns the list of installed default apps when they exist" in
        new AppsServicesImplScope {
          val result = mockAppsServicesImpl.getDefaultApps(contextSupport).run.run
          result must beLike {
            case Answer(resultApplicationList) => resultApplicationList shouldEqual defaultApplicationList
          }
        }

      "returns an AppsInstalledException when no default apps exist" in
        new AppsServicesImplScope with AppsServicesImplErrorScope {
          val result = mockAppsServicesImpl.getDefaultApps(contextSupport).run.run
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
