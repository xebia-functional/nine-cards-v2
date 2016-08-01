package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{File, FileOutputStream, InputStream}

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics._
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.javaNull
import com.fortysevendeg.ninecardslauncher.services.image.{BitmapTransformationExceptionImpl, FileException}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Answer, Errata}

trait ImageServicesTasksSpecification
  extends Specification
  with Mockito {

  trait ImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    class ImageServicesTaskImpl extends ImageServicesTasks

    val contextSupport = mock[ContextSupport]
    val packageManager = mock[PackageManager]

    val mockImageServicesTask = new ImageServicesTaskImpl

    val mockResources = mock[Resources]
    val mockFile = mock[File]
    val mockBitmap = mock[Bitmap]

    contextSupport.getPackageManager returns packageManager

  }

  trait FileNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    contextSupport.getResources returns mockResources

    val mockResourceUtils = new ResourceUtils {
      override def getPath(filename: String)(implicit context: ContextSupport): String = s"$fileFolder/$filename"
    }

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override val resourceUtils = mockResourceUtils
    }

  }

  trait ErrorFileNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockResourceUtils = new ResourceUtils {
      override def getPath(filename: String)(implicit context: ContextSupport): String = ""
    }

  }

  trait BitmapUrlImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockInputStream = mock[InputStream]

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def createInputStream(uri: String) = mockInputStream
      override def createBitmapByInputStream(is: InputStream) = mockBitmap
    }

  }

  trait ErrorBitmapUrlImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val density = DisplayMetrics.DENSITY_HIGH
    val mockInputStream = mock[InputStream]

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def createInputStream(uri: String) = javaNull
    }

  }

  trait SaveBitmapImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockFileOutputStream = mock[FileOutputStream]

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def createFileOutputStream(file: File): FileOutputStream = mockFileOutputStream
    }
  }

  trait ErrorSaveBitmapImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockFileOutputStream = mock[FileOutputStream]

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def createFileOutputStream(file: File): FileOutputStream = javaNull
    }
  }

}


class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification{

  "Image Services Tasks" should {

    "return a File when a valid packageName and a valid className is provided" in
      new ImageServicesTasksScope with FileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual packageName
            resultFile.getPath shouldEqual resultFilePathPackage
        }
      }

    "return a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with ErrorFileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(packageName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[FileException]
          }
        }
      }

    "return a Bitmap when when a valid uri is provided" in
      new ImageServicesTasksScope with BitmapUrlImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapFromURL(uri).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap
        }
      }

    "return a BitmapTransformationException with an invalid uri" in
      new ImageServicesTasksScope with ErrorBitmapUrlImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapFromURL(uri).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[BitmapTransformationExceptionImpl]
          }
        }
      }

    "successfuly saves the bitmap in the file" in
      new ImageServicesTasksScope with SaveBitmapImageServicesTasksScope {
        val result = mockImageServicesTask.saveBitmap(mockFile, mockBitmap).run.run
        result must beLike {case Answer(response) => response shouldEqual((): Unit)}
        there was one(mockBitmap).compress(Bitmap.CompressFormat.PNG, 90, mockFileOutputStream)
      }

    "return a FileException when the bitmap can not be saved" in
      new ImageServicesTasksScope with ErrorSaveBitmapImageServicesTasksScope{
        val result = mockImageServicesTask.saveBitmap(mockFile, mockBitmap).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[FileException]
          }
        }
      }

  }
}
