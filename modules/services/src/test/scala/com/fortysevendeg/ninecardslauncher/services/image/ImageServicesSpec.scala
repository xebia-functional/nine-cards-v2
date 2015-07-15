package com.fortysevendeg.ninecardslauncher.services.image

import java.io.File

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image.impl.{ImageServicesImpl, ImageServicesTasks}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.mockito.Mockito._
import scalaz.{\/-, -\/, \/}
import scalaz.concurrent.Task
import org.specs2.matcher.DisjunctionMatchers

trait ImageServicesSpecification
  extends Specification
  with MockImageServices
  with Mockito {

  val exception = NineCardsException("")

  trait ImageServicesScope extends Scope {

    val defaultBitmapTask = Task(\/-(mock[Bitmap]))

    val mockTasks = mock[ImageServicesTasks]

    mockTasks.getBitmapByName(
      appPackage.name)(contextSupport, imageServiceConfig) returns
      defaultBitmapTask

    mockTasks.getBitmapByApp(
      appPackage.packageName,
      appPackage.icon)(contextSupport) returns
      defaultBitmapTask

    mockTasks.getBitmapFromURL(
      appWebsite.url)(contextSupport) returns
      defaultBitmapTask

    mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.icon,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      defaultBitmapTask

    mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      defaultBitmapTask

    mockTasks.saveBitmap(any[File], any[Bitmap]) returns
      Task(\/-(()))

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileNotExistsTask

    mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport) returns
      fileNotExistsTask

    val mockImageService = new ImageServicesImpl(imageServiceConfig, mockTasks)

  }

  trait FilesExistsImageServicesScope
    extends ImageServicesScope {

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileExistsTask

    mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport) returns
      fileExistsTask
  }

  trait BitmapErrorImageServicesScope
    extends ImageServicesScope {

    mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.icon,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      Task(-\/(exception))

    mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      Task(-\/(exception))
  }

}

class ImageServicesSpec
  extends ImageServicesSpecification
  with DisjunctionMatchers {

  "Image Services with App Packages" should {

    "returns filename when the file exists" in new FilesExistsImageServicesScope {
      val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }
    }

    "returns filename and save image when the file not exists" in new ImageServicesScope {
      val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run

      there was one(mockTasks).saveBitmap(any[File], any[Bitmap])

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }
    }

    "returns a NineCardsException if the bitmaps can't be created" in new BitmapErrorImageServicesScope {
      val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run
      there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
      result must be_-\/[NineCardsException]
    }

  }

  "Image Services with Website Packages" should {

    "returns filename when the file exists" in new FilesExistsImageServicesScope {
      val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }
    }

    "returns filename and save image when the file not exists" in new ImageServicesScope {
      val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run

      there was one(mockTasks).saveBitmap(any[File], any[Bitmap])

      result must be_\/-[String].which {
        path =>
          path shouldEqual filePath
      }
    }

    "returns a NineCardsException if the bitmaps can't be created" in new BitmapErrorImageServicesScope {
      val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run
      there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
      result must be_-\/[NineCardsException]
    }

  }

}
