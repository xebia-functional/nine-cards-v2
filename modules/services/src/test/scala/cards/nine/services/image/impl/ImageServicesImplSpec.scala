package cards.nine.services.image.impl

import java.io.File

import android.content.Intent.ShortcutIconResource
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.DisplayMetrics
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.services.image._
import cats.syntax.either._
import monix.eval.Task
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ImageServicesImplSpecification extends Specification with Mockito {

  val bitmapException = BitmapTransformationException("")

  val serviceFileException: TaskService[Unit] = TaskService(Task(Left(FileException(""))))

  trait ImageServicesScope extends Scope with ImageServicesImplData {

    val contextSupport = mock[ContextSupport]
    contextSupport.getPackageManager returns mock[PackageManager]
    contextSupport.getAppIconsDir returns appIconDir
    contextSupport.getResources returns resources

    val appIconDir = mock[File]
    appIconDir.getPath returns fileFolder

    val resources = mock[Resources]
    resources.getDisplayMetrics returns mock[DisplayMetrics]

    val bitmap = mock[Bitmap]
    val width  = Option(10)
    val height = Option(10)

    val saveBitmapTask = TaskService(Task {
      Either.catchOnly[FileException] {
        val file = mock[File]
        file.exists() returns true
        file.getAbsolutePath returns resultFileSaveBitmap
        file
      }
    })

    val mockTasks = mock[ImageServicesTasks]

    val mockImageService = new ImageServicesImpl(mockTasks)

    val mockShortcutIconResource = mock[ShortcutIconResource]

  }

}

class ImageServicesImplSpec extends ImageServicesImplSpecification {

  "Image Services.saveBitmaps" should {

    "returns filename when the file exists" in
      new ImageServicesScope {

        mockTasks.saveBitmap(any[File], any[Bitmap]) returns TaskService(
          Task(Either.catchOnly[FileException](())))
        mockTasks.getPathByName(any)(any) returns saveBitmapTask

        val result = mockImageService.saveBitmap(bitmap, None, None)(contextSupport).value.run
        result must beLike {
          case Right(resultSaveBitmapPath) =>
            resultSaveBitmapPath.path shouldEqual bitmapPath.path
        }
      }

    "returns filename when the file exists with resize" in
      new ImageServicesScope {

        mockTasks.saveBitmap(any[File], any[Bitmap]) returns TaskService(
          Task(Either.catchOnly[FileException](())))
        mockTasks.getPathByName(any)(any) returns saveBitmapTask

        val result = mockImageService.saveBitmap(bitmap, width, height)(contextSupport).value.run
        result must beLike {
          case Right(resultSaveBitmapPath) =>
            resultSaveBitmapPath.path shouldEqual bitmapPath.path
        }
      }

    "returns a FileException if the bitmaps can't be stored" in
      new ImageServicesScope {

        mockTasks.getPathByName(any)(any) returns saveBitmapTask
        mockTasks.saveBitmap(any[File], any[Bitmap]) returns serviceFileException

        val result = mockImageService.saveBitmap(bitmap, None, None)(contextSupport).value.run
        result must beAnInstanceOf[Left[FileException, _]]
      }
  }

  "ImageServices.decodeShortcutIconResource" should {

    "call to ImageServicesTasks.getBitmapFromShortcutIconResource" in
      new ImageServicesScope {

        mockTasks.getBitmapFromShortcutIconResource(any)(any) returns TaskService.right(bitmap)

        val result = mockImageService
          .decodeShortcutIconResource(mockShortcutIconResource)(contextSupport)
          .value
          .run
        result shouldEqual Right(bitmap)

        there was one(mockTasks).getBitmapFromShortcutIconResource(===(mockShortcutIconResource))(
          any)

      }

  }

}
