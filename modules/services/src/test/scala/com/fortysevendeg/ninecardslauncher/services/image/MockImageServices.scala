package com.fortysevendeg.ninecardslauncher.services.image

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

import scalaz.\/
import scalaz.concurrent.Task

trait MockImageServices
  extends Scope
  with Mockito {

  val appPackage = AppPackage(
    "com.fortysevendeg.ninecardslauncher.test",
    "ClassNameExample",
    "Sample Name",
    0)

  val appWebsite = AppWebsite(
    "com.fortysevendeg.ninecardslauncher.test",
    "http://www.example.com/image.jpg",
    "Sample Name")

  val contextSupport = mock[ContextSupport]
  contextSupport.getPackageManager returns mock[PackageManager]
  contextSupport.getAppIconsDir returns appIconDir
  contextSupport.getResources returns resources

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val filePath = s"$fileFolder/$fileName"

  val appPackagePath = AppPackagePath(
    packageName = appPackage.packageName,
    className = appPackage.className,
    path = filePath
  )

  val appWebsitePath = AppWebsitePath(
    packageName = appWebsite.packageName,
    url = appWebsite.url,
    path = filePath
  )

  val appIconDir = mock[File]
  appIconDir.getPath returns fileFolder

  val resources = mock[Resources]
  resources.getDisplayMetrics returns mock[DisplayMetrics]

  val imageServiceConfig = ImageServicesConfig(List(1, 2, 3, 4, 5))

  val fileExistsTask = Task {
    \/.fromTryCatchThrowable[File, NineCardsException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns filePath
        file
      }
  }

  val fileNotExistsTask = Task {
    \/.fromTryCatchThrowable[File, NineCardsException] {
        val file = mock[File]
        file.exists() returns false
        file.getAbsolutePath returns filePath
        file
      }
  }

}
