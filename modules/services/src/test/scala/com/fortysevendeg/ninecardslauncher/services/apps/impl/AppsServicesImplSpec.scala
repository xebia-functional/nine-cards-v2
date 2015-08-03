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

    val sampleResolveInfo1 = mock[ResolveInfo]
    var mockActivityInfo1 = mock[ActivityInfo]
    var mockApplicationInfo1 = mock[ApplicationInfo]
    sampleResolveInfo1.loadLabel(packageManager) returns sampleApp1.name
    mockApplicationInfo1.packageName = sampleApp1.packageName
    mockActivityInfo1.applicationInfo = mockApplicationInfo1
    mockActivityInfo1.name = sampleApp1.className
    mockActivityInfo1.icon = sampleApp1.icon
    sampleResolveInfo1.activityInfo = mockActivityInfo1

    val sampleResolveInfo2 = mock[ResolveInfo]
    var mockActivityInfo2 = mock[ActivityInfo]
    var mockApplicationInfo2 = mock[ApplicationInfo]
    sampleResolveInfo2.loadLabel(packageManager) returns sampleApp2.name
    mockApplicationInfo2.packageName = sampleApp2.packageName
    mockActivityInfo2.applicationInfo = mockApplicationInfo2
    mockActivityInfo2.name = sampleApp2.className
    mockActivityInfo2.icon = sampleApp2.icon
    sampleResolveInfo2.activityInfo = mockActivityInfo2

    val mockApps = List(sampleResolveInfo1, sampleResolveInfo2)

    packageManager.queryIntentActivities(mockIntent, 0) returns mockApps

    val mockAppsServicesImpl = new AppsServicesImpl {
      override def categoryLauncherIntent(): Intent = mockIntent
    }
  }

  trait AppsServicesImplErrorScope {
    self : AppsServicesImplScope =>

    packageManager.queryIntentActivities(mockIntent, 0) throws new RuntimeException("")

  }

}

class AppsServicesImplSpec
  extends AppsServicesImplSpecification {

  "Apps Services" should {

    "returns the list of installed apps when they exist" in
      new AppsServicesImplScope {
        val result = mockAppsServicesImpl.getInstalledApps(contextSupport).run.run
        result must beLike[Result[Seq[Application], AppsInstalledException]] {
          case Answer(resultApplicationList) => resultApplicationList shouldEqual applicationList
        }
      }

    "returns an AppsInstalledException when no apps exist" in
      new AppsServicesImplScope with AppsServicesImplErrorScope {
        val result = mockAppsServicesImpl.getInstalledApps(contextSupport).run.run
        result must beLike[Result[Seq[Application], AppsInstalledException]] {
          case Errata(errors) =>
            errors.length must be_==(1)
        }
      }

  }

}
