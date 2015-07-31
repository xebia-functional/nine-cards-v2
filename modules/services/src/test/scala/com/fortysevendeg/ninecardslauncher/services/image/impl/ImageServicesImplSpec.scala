package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{File, IOException}

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.image.{AppWebsitePath, AppPackagePath, BitmapTransformationException}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer, Result}

import scalaz.concurrent.Task

trait ImageServicesImplSpecification
  extends Specification
  with Mockito {

  val serviceBitmapException = Service(Task(Result.errata[Bitmap, BitmapTransformationException](BitmapTransformationException(""))))

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

    val fileExistsTask = Service(Task {
      Result.catching[IOException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns filePath
        file
      }
    })

    val fileNotExistsTask = Service(Task {
      Result.catching[IOException] {
        val file = mock[File]
        file.exists() returns false
        file.getAbsolutePath returns filePath
        file
      }
    })

    val defaultBitmapTask = Service(Task(Result.catching[BitmapTransformationException](mock[Bitmap])))

    val mockTasks = mock[ImageServicesTasks]

    mockTasks.getBitmapByName(
      appPackage.name)(contextSupport, imageServiceConfig) returns
      Result.answer(mock[Bitmap])

    mockTasks.getBitmapByApp(
      appPackage.packageName,
      appPackage.icon)(contextSupport) returns
      defaultBitmapTask

    mockTasks.getBitmapFromURL(
      appWebsite.url) returns
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
      Service(Task(Result.catching[IOException](())))

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileNotExistsTask

    mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport) returns
      fileNotExistsTask

    val mockImageService = new ImageServicesImpl(imageServiceConfig, mockTasks)

  }

  trait FilesExistsImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getPathByApp(appPackage.packageName, appPackage.className)(contextSupport) returns
      fileExistsTask

    mockTasks.getPathByPackageName(appWebsite.packageName)(contextSupport) returns
      fileExistsTask
  }

  trait BitmapErrorImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getBitmapByAppOrName(
      appPackage.packageName,
      appPackage.icon,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      serviceBitmapException

    mockTasks.getBitmapFromURLOrName(
      appWebsite.url,
      appPackage.name)(contextSupport, imageServiceConfig) returns
      serviceBitmapException
  }

}

class ImageServicesImplSpec
  extends ImageServicesImplSpecification {

  "Image Services with App Packages" should {

    "returns filename when the file exists" in
      new ImageServicesScope with FilesExistsImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        result must beLike[Result[AppPackagePath, IOException with BitmapTransformationException]] {
          case Answer(resultAppPackagePath) =>
            resultAppPackagePath shouldEqual appPackagePath
        }
      }

    "returns filename and save image when the file not exists" in
      new ImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        there was one(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike[Result[AppPackagePath, IOException with BitmapTransformationException]] {
          case Answer(resultAppPackagePath) =>
            resultAppPackagePath shouldEqual appPackagePath
        }
      }

    "returns a NineCardsException if the bitmaps can't be created" in
      new ImageServicesScope with BitmapErrorImageServicesScope {
        val result = mockImageService.saveAppIcon(appPackage)(contextSupport).run.run
        there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike[Result[AppPackagePath, IOException with BitmapTransformationException]] {
          case Errata(errors) =>
            errors.length must be_==(1)
        }
      }

  }

  "Image Services with Website Packages" should {

    "returns filename when the file exists" in
      new ImageServicesScope with FilesExistsImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        result must beLike[Result[AppWebsitePath, IOException with BitmapTransformationException]] {
          case Answer(resultAppWebsitePath) =>
            resultAppWebsitePath shouldEqual appWebsitePath
        }
      }

    "returns filename and save image when the file not exists" in
      new ImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        there was one(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike[Result[AppWebsitePath, IOException with BitmapTransformationException]] {
          case Answer(resultAppWebsitePath) =>
            resultAppWebsitePath shouldEqual appWebsitePath
        }
      }

    "returns a NineCardsException if the bitmaps can't be created" in
      new ImageServicesScope with BitmapErrorImageServicesScope {
        val result = mockImageService.saveAppIcon(appWebsite)(contextSupport).run.run
        there was exactly(0)(mockTasks).saveBitmap(any[File], any[Bitmap])
        result must beLike[Result[AppWebsitePath, IOException with BitmapTransformationException]] {
          case Errata(errors) =>
            errors.length must be_==(1)
        }
      }

  }

}
