package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.image._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata, Result}

import scalaz.concurrent.Task

trait ImageServicesImplSpecification
  extends Specification
  with Mockito {

  val bitmapException = BitmapTransformationExceptionImpl("")

  val serviceBitmapException = Service(Task(Result.errata[Bitmap, BitmapTransformationException](bitmapException)))

  val fileException = FileExceptionImpl("")

  val serviceFileException = Service(Task(Result.errata[Unit, FileException](fileException)))

  trait ImageServicesScope
    extends Scope
    with ImageServicesImplData {

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getAppIconsDir returns appIconDir
    contextSupport.getResources returns resources

    val appIconDir = mock[File]
    appIconDir.getPath returns fileFolder

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val saveBitmap = SaveBitmap(
      name = bitmapName,
      bitmap = mock[Bitmap])

    val fileExistsTask = Service(Task {
      Result.catching[FileException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns filePath
        file
      }
    })

    val fileNotExistsTask = Service(Task {
      Result.catching[FileException] {
        val file = mock[File]
        file.exists() returns false
        file.getAbsolutePath returns filePath
        file
      }
    })

    val saveBitmapTask = Service(Task {
      Result.catching[FileException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns resultFileSaveBitmap
        file
      }
    })

    val defaultBitmapTask = Service(Task(Result.catching[BitmapTransformationException](mock[Bitmap])))

    val mockTasks = mock[ImageServicesTasks]

    mockTasks.getBitmapByName(
      appPackage.name)(contextSupport, imageServiceConfig) returns
      Result.answer(mock[Bitmap])

    mockTasks.getBitmapByApp(
      appPackage.packageName)(contextSupport) returns
      defaultBitmapTask

    mockTasks.getBitmapFromURL(
      appWebsite.url) returns
      defaultBitmapTask

    mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      defaultBitmapTask

    mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      defaultBitmapTask

    mockTasks.saveBitmap(any[File], any[Bitmap]) returns
      Service(Task(Result.catching[FileException](())))

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileNotExistsTask

    mockTasks.getPathByName(appWebsite.packageName)(contextSupport) returns
      fileNotExistsTask

    val mockImageService = new ImageServicesImpl(imageServiceConfig, mockTasks)

  }

  trait FilesExistsImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileExistsTask

    mockTasks.getPathByName(appWebsite.packageName)(contextSupport) returns
      fileExistsTask
  }

  trait BitmapErrorImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      serviceBitmapException

    mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      serviceBitmapException
  }

  trait SaveBitmapImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getPathByName(bitmapName)(contextSupport) returns
      saveBitmapTask
  }

  trait SaveBitmapErrorImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.saveBitmap(any[File], any[Bitmap]) returns
      serviceFileException
  }

}

class ImageServicesImplSpec
  extends ImageServicesImplSpecification {

  "Image Services with App Packages" should {

    "returns filename when the file exists" in
      new ImageServicesScope with FilesExistsImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        result must beLike {
          case Answer(resultAppPackagePath) =>
            resultAppPackagePath shouldEqual appPackagePath
        }
      }

    "returns filename and save image when the file not exists" in
      new ImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        there was one(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike {
          case Answer(resultAppPackagePath) =>
            resultAppPackagePath shouldEqual appPackagePath
        }
      }

    "returns a BitmapTransformationException if the bitmaps can't be created" in
      new ImageServicesScope with BitmapErrorImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual bitmapException
          }
        }
      }

  }

  "Image Services with Website Packages" should {

    "returns filename when the file exists" in
      new ImageServicesScope with FilesExistsImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        result must beLike {
          case Answer(resultAppWebsitePath) =>
            resultAppWebsitePath shouldEqual appWebsitePath
        }
      }

    "returns filename and save image when the file not exists" in
      new ImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        there was one(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike {
          case Answer(resultAppWebsitePath) =>
            resultAppWebsitePath shouldEqual appWebsitePath
        }
      }

    "returns a BitmapTransformationException if the bitmaps can't be created" in
      new ImageServicesScope with BitmapErrorImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual bitmapException
          }
        }
      }

  }

  "Image Services with Bitmaps" should {

    "returns filename when the file exists" in
      new ImageServicesScope with SaveBitmapImageServicesScope {
        val result = mockImageService.saveBitmap(saveBitmap)(contextSupport).run.run
        result must beLike {
          case Answer(resultSaveBitmapPath) =>
            resultSaveBitmapPath.path shouldEqual saveBitmapPath.path
        }
      }

    "returns a FileException if the bitmaps can't be stored" in
      new ImageServicesScope with SaveBitmapImageServicesScope with SaveBitmapErrorImageServicesScope {
        val result = mockImageService.saveBitmap(saveBitmap)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception shouldEqual fileException
          }
        }
      }

  }

}
