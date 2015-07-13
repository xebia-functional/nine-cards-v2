package com.fortysevendeg.ninecardslauncher.services.image

import java.io.File

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image.impl.{ImageServicesImpl, ImageServicesTasks}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.mockito.Mockito._
import scalaz.\/
import scalaz.concurrent.Task
import org.specs2.matcher.DisjunctionMatchers

trait ImageServicesSpecification
    extends Specification
    with MockImageServices
    with Mockito {

  trait ImageServicesScope extends Scope {

    val defaultBitmapTask = Task {
      \/.fromTryCatchThrowable[Bitmap, NineCardsException] {
        mock[Bitmap]
      }
    }

    val mockTasks = mock[ImageServicesTasks]
    when(mockTasks.getBitmapByName(
      appPackage.name)(contextSupport, imageServiceConfig)) thenReturn defaultBitmapTask
    when(mockTasks.getBitmapByApp(
      appPackage.packageName,
      appPackage.icon)(contextSupport)) thenReturn defaultBitmapTask
    when(mockTasks.getBitmapFromURL(
      appWebsite.url)(contextSupport)) thenReturn defaultBitmapTask
    when(mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.icon,
      appPackage.name)(contextSupport, imageServiceConfig)) thenReturn defaultBitmapTask
    when(mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig)) thenReturn defaultBitmapTask
    when(mockTasks.saveBitmap(any[File], any[Bitmap])) thenReturn Task {
      \/.fromTryCatchThrowable[Unit, NineCardsException] {
        ()
      }
    }

    val mockImageService = new ImageServicesImpl(imageServiceConfig, mockTasks)

  }

}

class ImageServicesSpec
    extends ImageServicesSpecification
    with DisjunctionMatchers {

  "Image Services with App Packages" should {

    "returns filename when the file exists" in new ImageServicesScope {

      when(mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport)) thenReturn fileExistsTask

      val result = mockImageService.androidAppPackage(appPackage)(contextSupport).run

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }

    }

    "returns filename and save image when the file not exists" in new ImageServicesScope {

      when(mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport)) thenReturn fileNotExistsTask

      val result = mockImageService.androidAppPackage(appPackage)(contextSupport).run

      there was one(mockTasks).saveBitmap(any[File], any[Bitmap])

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }

    }

  }

  "Image Services with Website Packages" should {

    "returns filename when the file exists" in new ImageServicesScope {

      when(mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport)) thenReturn fileExistsTask

      val result = mockImageService.androidAppWebsite(appWebsite)(contextSupport).run

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }

    }

    "returns filename and save image when the file not exists" in new ImageServicesScope {

      when(mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport)) thenReturn fileNotExistsTask

      val result = mockImageService.androidAppWebsite(appWebsite)(contextSupport).run

      there was one(mockTasks).saveBitmap(any[File], any[Bitmap])

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }

    }

  }

}
