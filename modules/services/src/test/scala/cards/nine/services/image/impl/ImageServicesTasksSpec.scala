package cards.nine.services.image.impl

import java.io.{ByteArrayInputStream, File, FileOutputStream, InputStream}

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics._
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.javaNull
import cards.nine.commons.test.TaskServiceTestOps._
import cards.nine.commons.utils.StreamWrapper
import cards.nine.services.image.{BitmapTransformationException, FileException}
import cards.nine.services.utils.ResourceUtils
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait ImageServicesTasksSpecification
  extends Specification
  with Mockito {

  trait ImageServicesTasksScope
    extends Scope
      with ImageServicesImplData {

    class ImageServicesTaskImpl extends ImageServicesTasks

    val contextSupport = mock[ContextSupport]
    val packageManager = mock[PackageManager]
    val mockStreamWrapper = mock[StreamWrapper]

    val mockImageServicesTask = new ImageServicesTaskImpl

    val mockResources = mock[Resources]
    val mockFile = mock[File]
    val mockBitmap = mock[Bitmap]

    contextSupport.getPackageManager returns packageManager

  }
}


class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification {

  "Image Services Tasks" should {

    "return a File when a valid packageName and a valid className is provided" in
      new ImageServicesTasksScope {

        contextSupport.getResources returns mockResources

        val mockResourceUtils = new ResourceUtils {
          override def getPath(filename: String)(implicit context: ContextSupport): String = s"$fileFolder/$filename"
        }

        override val mockImageServicesTask = new ImageServicesTaskImpl {
          override val resourceUtils = mockResourceUtils
        }

        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).value.run
        result must beLike {
          case Right(resultFile) =>
            resultFile.getName shouldEqual packageName
            resultFile.getPath shouldEqual resultFilePathPackage
        }
      }

    "return a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope {

        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).value.run
        result must beAnInstanceOf[Left[FileException, _]]

      }

    "return a Bitmap when a valid uri is provided" in
      new ImageServicesTasksScope {

        val mockInputStream = mock[InputStream]

        mockStreamWrapper.createInputStream(any) returns mockInputStream
        mockStreamWrapper.createBitmapByInputStream(mockInputStream) returns mockBitmap

        val result = mockImageServicesTask.getBitmapFromURL(uri).value.run
        result must beAnInstanceOf[Left[BitmapTransformationException, _]]
      }

    "return a BitmapTransformationException with an invalid uri" in
      new ImageServicesTasksScope {

        mockStreamWrapper.createInputStream(any) returns javaNull
        val result = mockImageServicesTask.getBitmapFromURL(uri).value.run
        result must beAnInstanceOf[Left[BitmapTransformationException, _]]
      }


    "successfully saves the bitmap in the file" in
      new ImageServicesTasksScope {

        val mockFileOutputStream = mock[FileOutputStream]
        mockStreamWrapper.createFileOutputStream(existingFile) returns mockFileOutputStream

        val result = mockImageServicesTask.saveBitmap(existingFile, mockBitmap).value.run
        result shouldEqual Right((): Unit)

      }

    "return a FileException when the bitmap can not be saved" in
      new ImageServicesTasksScope {

        mockStreamWrapper.createFileOutputStream(any) returns javaNull
        val result = mockImageServicesTask.saveBitmap(mockFile, mockBitmap).value.run
        result must beAnInstanceOf[Left[FileException, _]]
      }
  }
}
