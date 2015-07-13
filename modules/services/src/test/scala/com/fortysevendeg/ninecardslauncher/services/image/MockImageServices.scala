package com.fortysevendeg.ninecardslauncher.services.image

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import org.mockito.Mockito._
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
  when(contextSupport.getPackageManager) thenReturn mock[PackageManager]
  when(contextSupport.getAppIconsDir) thenReturn appIconDir
  when(contextSupport.getResources) thenReturn resources

  val fileFolder = "/file/example"

  val fileName = String.format("%s_%s", appPackage.packageName.toLowerCase.replace(".", "_"), appPackage.className.toLowerCase.replace(".", "_"))

  val filePath = s"$fileFolder/$fileName"

  val appIconDir = mock[File]
  when(appIconDir.getPath) thenReturn fileFolder

  val resources = mock[Resources]
  when(resources.getDisplayMetrics) thenReturn mock[DisplayMetrics]

  val imageServiceConfig = ImageServicesConfig(List(1, 2, 3, 4, 5))

  val fileExistsTask = Task {
    \/.fromTryCatchThrowable[File, NineCardsException] {
        val file = mock[File]
        when(file.exists()) thenReturn true
        when(file.getAbsolutePath) thenReturn filePath
        file
      }
  }

  val fileNotExistsTask = Task {
    \/.fromTryCatchThrowable[File, NineCardsException] {
        val file = mock[File]
        when(file.exists()) thenReturn false
        when(file.getAbsolutePath) thenReturn filePath
        file
      }
  }

}
