package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{FileOutputStream, File, InputStream}

import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics._
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.image.{ImageServicesConfig, BitmapTransformationExceptionImpl, FileException}
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import rapture.core.{Errata, Answer}

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

  trait ErrorPackageImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockResourceUtils = new ResourceUtils {
      override def getPathPackage(packageName: String, className: String)(implicit context: ContextSupport): String = ""
    }

  }

  trait BitmapAppDensityImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    packageManager.getResourcesForApplication(packageName) returns mockResources

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def getDisplayMetricsDensityDpi( implicit context: ContextSupport) = densityDpi
      override def getIconByDensity(drawable: Drawable) = mockBitmap
    }
  }

  trait BitmapAppPackageNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    packageManager.getResourcesForApplication(packageName) returns mockResources

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def getDisplayMetricsDensityDpi( implicit context: ContextSupport) = densityDpi
      override def getIconByPackageName(packageName: String)(implicit context: ContextSupport): Bitmap = mockBitmap
    }
  }

  trait ErrorBitmapAppImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    packageManager.getResourcesForApplication(packageName) returns null

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
      override def createInputStream(uri: String) = null
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
      override def createFileOutputStream(file: File): FileOutputStream = null
    }
  }

  trait BitmapAppImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockImageServicesConfig = mock[ImageServicesConfig]

    packageManager.getResourcesForApplication(packageName) returns mockResources

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def getDisplayMetricsDensityDpi( implicit context: ContextSupport) = densityDpi
      override def getIconByDensity(drawable: Drawable) = mockBitmap
    }
  }

  trait BitmapNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockImageServicesConfig = mock[ImageServicesConfig]
    val mockRect = mock[Rect]
    val mockPaint = mock[Paint]
    val mockCanvas = mock[Canvas]

    packageManager.getResourcesForApplication(packageName) returns mockResources
    mockPaint.measureText(textToMeasure) returns textSize
    mockImageServicesConfig.colors returns colorsList

    override val mockImageServicesTask = new ImageServicesTaskImpl {
      override def getDisplayMetricsDensityDpi( implicit context: ContextSupport) = densityDpi
      override def getDisplayMetricsWidthPixels( implicit context: ContextSupport) = widthPixels
      override def getDisplayMetricsHeightPixels( implicit context: ContextSupport) = heightPixels
      override def createBitmap(defaultSize: Int) = mockBitmap
      override def createRect = mockRect
      override def createPaint = mockPaint
      override def createCanvas(bitmap: Bitmap) = mockCanvas
    }
  }

  trait ErrorBitmapAppNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockImageServicesConfig = mock[ImageServicesConfig]

    packageManager.getResourcesForApplication(packageName) returns null

  }

  trait BitmapURLNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData
    with BitmapUrlImageServicesTasksScope {

    self: ImageServicesTasksScope =>

    val mockImageServicesConfig = mock[ImageServicesConfig]

  }

  trait ErrorBitmapURLNameImageServicesTasksScope
    extends Scope
    with ImageServicesImplData {

    self: ImageServicesTasksScope =>

    val mockImageServicesConfig = mock[ImageServicesConfig]

    packageManager.getResourcesForApplication(packageName) returns null

  }

}


class ImageServicesTasksSpec
  extends ImageServicesTasksSpecification{

  "Image Services Tasks" should {

    "return a File when a valid fileName is provided" in
      new ImageServicesTasksScope with FileNameImageServicesTasksScope{
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual resultFileName
            resultFile.getPath shouldEqual resultFilePath
        }
      }

    "return a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with ErrorFileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByName(fileName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) =>
              exception must beAnInstanceOf[FileException]
          }
        }
      }

    "return a File when the file is created with a valid packageName" in
      new ImageServicesTasksScope with FileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByApp(packageName, className)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual fileName
            resultFile.getPath shouldEqual filePath
        }
      }

    "return a FileException when getPathPackage in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with ErrorPackageImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByApp(packageName, className)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[FileException]
          }
        }
      }

    "return a File when a valid packageName and a valid className is provided" in
      new ImageServicesTasksScope with FileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByPackageName(packageName)(contextSupport).run.run
        result must beLike {
          case Answer(resultFile) =>
            resultFile.getName shouldEqual packageName
            resultFile.getPath shouldEqual resultFilePathPackage
        }
      }

    "return a FileException when getPath in resourceUtils returns an empty string" in
      new ImageServicesTasksScope with ErrorFileNameImageServicesTasksScope {
        val result = mockImageServicesTask.getPathByPackageName(packageName)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[FileException]
          }
        }
      }

    "return a Bitmap when the file is created by density" in
      new ImageServicesTasksScope with BitmapAppDensityImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByApp(packageName, icon)(contextSupport).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a Bitmap when the file is created by packageName" in
      new ImageServicesTasksScope with BitmapAppPackageNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByApp(packageName, icon)(contextSupport).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a BitmapTransformationException when no resources can be found" in
      new ImageServicesTasksScope with ErrorBitmapAppImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByApp(packageName, icon)(contextSupport).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[BitmapTransformationExceptionImpl]
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

    "return a Bitmap when a valid app is provided" in
      new ImageServicesTasksScope with BitmapAppImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByAppOrName(
          packageName, icon, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a Bitmap when an invalid app and a valid name are provided" in
      new ImageServicesTasksScope with BitmapNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByAppOrName(
          packageName, icon, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a BitmapTransformationException when an invalid app and name are provided" in
      new ImageServicesTasksScope with ErrorBitmapAppNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByAppOrName(
          packageName, icon, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[BitmapTransformationExceptionImpl]
          }
        }
      }

    "return a Bitmap when a valid url is provided" in
      new ImageServicesTasksScope with BitmapURLNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapFromURLOrName(
          uri, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a Bitmap when an invalid url and a valid name is provided" in
      new ImageServicesTasksScope with BitmapNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapFromURLOrName(
          uri, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a BitmapTransformationException with an invalid url and an invalid name" in
      new ImageServicesTasksScope with ErrorBitmapURLNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapFromURLOrName(
          uri, name)(contextSupport, mockImageServicesConfig).run.run
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[BitmapTransformationExceptionImpl]
          }
        }
      }

    "return a Bitmap when a valid name is provided" in
      new ImageServicesTasksScope with BitmapNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByName(name)(contextSupport, mockImageServicesConfig)
        result must beLike {case Answer(resultBitmap) => resultBitmap shouldEqual mockBitmap}
      }

    "return a BitmapTransformationException when an invalid app and name are provided" in
      new ImageServicesTasksScope with ErrorBitmapAppNameImageServicesTasksScope {
        val result = mockImageServicesTask.getBitmapByName(name)(contextSupport, mockImageServicesConfig)
        result must beLike {
          case Errata(e) => e.headOption must beSome.which {
            case (_, (_, exception)) => exception must beAnInstanceOf[BitmapTransformationExceptionImpl]
          }
        }
      }

  }
}
