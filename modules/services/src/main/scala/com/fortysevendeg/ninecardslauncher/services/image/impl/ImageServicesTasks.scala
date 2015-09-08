package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{FileOutputStream, InputStream, File}
import java.net.URL

import android.content.res.Resources
import android.graphics._
import android.graphics.drawable.{Drawable, BitmapDrawable}
import android.os.Build
import android.util.{DisplayMetrics, TypedValue}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils
import rapture.core.{Answer, Result}

import scalaz.concurrent.Task

trait ImageServicesTasks
  extends ImplicitsImageExceptions {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  def getPathByApp(packageName: String, className: String)(implicit context: ContextSupport): ServiceDef2[File, FileException] = Service {
    Task {
      CatchAll[FileException] {
        new File(resourceUtils.getPathPackage(packageName, className))
      }
    }
  }

  def getPathByName(name: String)(implicit context: ContextSupport): ServiceDef2[File, FileException] = Service {
    Task {
      CatchAll[FileException] {
        new File(resourceUtils.getPath(name))
      }
    }
  }

  def getBitmapByApp(packageName: String, icon: Int)(implicit context: ContextSupport): ServiceDef2[Bitmap, BitmapTransformationException] = Service {
    Task {
      val packageManager = context.getPackageManager
      Option(packageManager.getResourcesForApplication(packageName)) match {
        case Some(resources) =>
          val density = betterDensityForResource(resources, icon)
          tryIconByDensity(resources, icon, density) match {
            case Answer(a) =>
              Result.answer(a)
            case _ =>
              tryIconByPackageName(packageName)
          }
        case _ => Result.errata(BitmapTransformationExceptionImpl("Resource not found from packageName"))
      }
    }
  }

  def getBitmapFromURL(uri: String): ServiceDef2[Bitmap, BitmapTransformationException] = Service {
    Task {
      CatchAll[BitmapTransformationException] {
        createInputStream(uri) match {
          case is: InputStream => createBitmapByInputStream(is)
          case _ => throw BitmapTransformationExceptionImpl(s"Unexpected error while fetching content from uri: $uri")
        }
      }
    }
  }

  def saveBitmap(file: File, bitmap: Bitmap): ServiceDef2[Unit, FileException] = Service {
    Task {
      CatchAll[FileException] {
        val out = createFileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
      }
    }
  }

  def getBitmapByAppOrName(packageName: String, icon: Int, name: String)(implicit context: ContextSupport, imageServicesConfig: ImageServicesConfig):
  ServiceDef2[Bitmap, BitmapTransformationException] = Service {
    manageBitmapTask(name)(getBitmapByApp(packageName, icon).run)
  }

  def getBitmapFromURLOrName(url: String, name: String)(implicit context: ContextSupport, imageServicesConfig: ImageServicesConfig):
  ServiceDef2[Bitmap, BitmapTransformationException] = Service {
    manageBitmapTask(name)(getBitmapFromURL(url).run)
  }

  def getBitmapByName(text: String)(implicit context: ContextSupport, imageServicesConfig: ImageServicesConfig): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
      val ds = defaultSize
      val bitmap: Bitmap = createBitmap(ds)
      val bounds: Rect = createRect
      val paint = defaultPaint
      paint.getTextBounds(text, 0, text.length, bounds)
      val x: Int = ((ds / 2) - bounds.exactCenterX).toInt
      val y: Int = ((ds / 2) - bounds.exactCenterY).toInt
      val canvas: Canvas = createCanvas(bitmap)
      val color = imageServicesConfig.colors(scala.util.Random.nextInt(imageServicesConfig.colors.length))
      canvas.drawColor(color)
      canvas.drawText(text, x, y, paint)
      bitmap
    }

  private[this] def manageBitmapTask(name: String)(getBitmap: => Task[Result[Bitmap, BitmapTransformationException]])(implicit context: ContextSupport, imageServicesConfig: ImageServicesConfig) =
    getBitmap map {
      case answer @ Answer(_) => answer
      case _ => getBitmapByName(name)
    }

  private[this] def currentDensity(implicit context: ContextSupport): Int = getDisplayMetricsDensityDpi

  private[this] def densities(implicit context: ContextSupport): List[Int] = currentDensity match {
    case DisplayMetrics.DENSITY_HIGH =>
      List(DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
    case _ =>
      List(DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
  }

  private[this] def betterDensityForResource(resources: Resources, id: Int)(implicit context: ContextSupport): Int =
    densities.find(density => Result(resources.getValueForDensity(id, density, new TypedValue, true)).isAnswer) getOrElse noDensity

  private[this] def defaultSize(implicit context: ContextSupport): Int = {
    val width: Int = getDisplayMetricsWidthPixels / 3
    val height: Int = getDisplayMetricsHeightPixels / 3
    if (width > height) width else height
  }

  private[this] def defaultPaint(implicit context: ContextSupport): Paint = {
    def determineMaxTextSize(maxWidth: Float): Int = {
      var size: Int = 0
      val paint: Paint = createPaint
      do {
        size = size + 1
        paint.setTextSize(size)
      } while (paint.measureText("M") < maxWidth)
      size
    }
    val paint = createPaint
    paint.setColor(Color.WHITE)
    paint.setStyle(Paint.Style.FILL)
    paint.setAntiAlias(true)
    paint.setTextSize(determineMaxTextSize((defaultSize / 3) * 2))
    paint
  }

  private[this] def tryIconByDensity(resources: Resources, icon: Int, density: Int): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
      val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) resources.getDrawableForDensity(icon, density, null)
      else resources.getDrawableForDensity(icon, density)
      getIconByDensity(d)
    }

  private[this] def tryIconByPackageName(packageName: String)(implicit context: ContextSupport): Result[Bitmap, BitmapTransformationException] =
    CatchAll[BitmapTransformationException] {
      getIconByPackageName(packageName)
    }

  protected def getIconByDensity(drawable: Drawable): Bitmap = drawable.asInstanceOf[BitmapDrawable].getBitmap

  protected def getIconByPackageName(packageName: String)(implicit context: ContextSupport): Bitmap = context.getPackageManager.getApplicationIcon(packageName).asInstanceOf[BitmapDrawable].getBitmap

  protected def createInputStream(uri: String) = new URL(uri).getContent

  protected def createBitmapByInputStream(is: InputStream) = BitmapFactory.decodeStream(is)

  protected def createBitmap(defaultSize: Int) = Bitmap.createBitmap(defaultSize, defaultSize, Bitmap.Config.RGB_565)

  protected def createFileOutputStream(file: File): FileOutputStream = new FileOutputStream(file)

  protected def getDisplayMetricsDensityDpi(implicit context: ContextSupport): Int = context.getResources.getDisplayMetrics.densityDpi

  protected def getDisplayMetricsWidthPixels(implicit context: ContextSupport): Int = context.getResources.getDisplayMetrics.widthPixels

  protected def getDisplayMetricsHeightPixels(implicit context: ContextSupport): Int = context.getResources.getDisplayMetrics.heightPixels

  protected def createRect: Rect = new Rect

  protected def createPaint: Paint = new Paint

  protected def createCanvas(bitmap: Bitmap): Canvas = new Canvas(bitmap)

}

object ImageServicesTasks extends ImageServicesTasks