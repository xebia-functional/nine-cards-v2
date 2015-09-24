package com.fortysevendeg.ninecardslauncher.services.apps.impl

import android.content.Intent
import android.content.pm.{ApplicationInfo, ActivityInfo, ResolveInfo, PackageManager}
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

    val packageManager = mock[PackageManager]
    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns packageManager

    val mockIntent = mock[Intent]

    def createMockResolveInfo(sampleApp: Application) : ResolveInfo = {
      val sampleResolveInfo = mock[ResolveInfo]
      val mockActivityInfo = mock[ActivityInfo]
      val mockApplicationInfo = mock[ApplicationInfo]
      sampleResolveInfo.loadLabel(packageManager) returns sampleApp.name
      mockApplicationInfo.packageName = sampleApp.packageName
      mockActivityInfo.applicationInfo = mockApplicationInfo
      mockActivityInfo.name = sampleApp.className
      mockActivityInfo.icon = sampleApp.icon
      sampleResolveInfo.activityInfo = mockActivityInfo
      sampleResolveInfo
    }

    val mockApps = List(createMockResolveInfo(sampleApp1), createMockResolveInfo(sampleApp2))

    packageManager.queryIntentActivities(mockIntent, 0) returns mockApps

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
