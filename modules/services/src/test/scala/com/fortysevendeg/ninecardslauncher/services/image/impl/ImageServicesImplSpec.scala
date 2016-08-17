package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService
import com.fortysevendeg.ninecardslauncher.commons.services.CatsService._
import com.fortysevendeg.ninecardslauncher.services.image._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scalaz.concurrent.Task

trait ImageServicesImplSpecification
  extends Specification
    with Mockito {

  val bitmapException = BitmapTransformationExceptionImpl("")

  val serviceBitmapException = CatsService(Task(Xor.Left(bitmapException)))

  val fileException = FileExceptionImpl("")

  val serviceFileException: CatsService[Unit] = CatsService(Task(Xor.Left(fileException)))

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

    val saveBitmap = SaveBitmap(bitmap = mock[Bitmap], bitmapResize = None)

    val fileExistsTask = CatsService(Task {
      Xor.catchOnly[FileException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns filePath
        file
      }
    })

    val fileNotExistsTask = CatsService(Task {
      Xor.catchOnly[FileException] {
        val file = mock[File]
        file.exists() returns false
        file.getAbsolutePath returns filePath
        file
      }
    })

    val saveBitmapTask = CatsService(Task {
      Xor.catchOnly[FileException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns resultFileSaveBitmap
        file
      }
    })

    val defaultBitmapTask = CatsService(Task(Xor.catchOnly[BitmapTransformationException](mock[Bitmap])))

    val mockTasks = mock[ImageServicesTasks]

    mockTasks.getBitmapFromURL(
      appWebsite.url) returns
      defaultBitmapTask

    mockTasks.saveBitmap(any[File], any[Bitmap]) returns
      CatsService(Task(Xor.catchOnly[FileException](())))

    val mockImageService = new ImageServicesImpl(imageServiceConfig, mockTasks)

  }

  trait SaveBitmapImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.getPathByName(any)(any) returns saveBitmapTask
  }

  trait SaveBitmapErrorImageServicesScope {

    self: ImageServicesScope =>

    mockTasks.saveBitmap(any[File], any[Bitmap]) returns serviceFileException
  }

}

class ImageServicesImplSpec
  extends ImageServicesImplSpecification {

  "Image Services with Bitmaps" should {

    "returns filename when the file exists" in
      new ImageServicesScope with SaveBitmapImageServicesScope {
        val result = mockImageService.saveBitmap(saveBitmap)(contextSupport).value.run
        result must beLike {
          case Xor.Right(resultSaveBitmapPath) =>
            resultSaveBitmapPath.path shouldEqual saveBitmapPath.path
        }
      }

    "returns a FileException if the bitmaps can't be stored" in
      new ImageServicesScope with SaveBitmapImageServicesScope with SaveBitmapErrorImageServicesScope {
        val result = mockImageService.saveBitmap(saveBitmap)(contextSupport).value.run
        result must beLike {
          case Xor.Left(e) => e shouldEqual fileException
        }
      }
  }

}
