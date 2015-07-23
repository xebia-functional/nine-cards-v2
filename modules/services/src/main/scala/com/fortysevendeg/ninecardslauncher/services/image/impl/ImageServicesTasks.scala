package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.{File, FileOutputStream, InputStream}
import java.net.URL

import android.content.res.Resources
import android.graphics._
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.{DisplayMetrics, TypedValue}
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image.ImageServicesConfig
import com.fortysevendeg.ninecardslauncher.services.utils.ResourceUtils

import scala.util.{Failure, Success, Try}
import scalaz._
import scalaz.concurrent.Task

trait ImageServicesTasks {

  val noDensity = 0

  val resourceUtils = new ResourceUtils

  def getPathByName(name: String)(implicit context: ContextSupport): Task[NineCardsException \/ File] =
    Task {
      fromTryCatchNineCardsException[File] {
        new File(resourceUtils.getPath(if (name.isEmpty) "_" else name.substring(0, 1).toUpperCase))
      }
    }

  def getPathByApp(packageName: String, className: String)(implicit context: ContextSupport): Task[NineCardsException \/ File] =
    Task {
      fromTryCatchNineCardsException[File] {
        new File(resourceUtils.getPathPackage(packageName, className))
      }
    }

  def getPathByPackageName(packageName: String)(implicit context: ContextSupport): Task[NineCardsException \/ File] =
    Task {
      fromTryCatchNineCardsException[File] {
        new File(resourceUtils.getPath(packageName))
      }
    }

  def getBitmapByApp(packageName: String, icon: Int)(implicit context: ContextSupport): Task[NineCardsException \/ Bitmap] =
    Task {
      fromTryCatchNineCardsException[Bitmap] {
        val packageManager = context.getPackageManager
        (Option(packageManager.getResourcesForApplication(packageName)) map {
          resources =>
            val density = betterDensityForResource(resources, icon)
            tryIconByDensity(resources, icon, density) match {
              case Success(bmp) => bmp
              case Failure(_) => tryIconByPackageName(packageName) match {
                case Success(bmp) => bmp
                case Failure(_) => throw NineCardsException("Icon no created")
              }
            }
        }) getOrElse (throw NineCardsException("Resource not found from packageName"))
      }
    }

  def getBitmapFromURL(uri: String)(implicit context: ContextSupport): Task[NineCardsException \/ Bitmap] =
    Task {
      fromTryCatchNineCardsException[Bitmap] {
        val is = new URL(uri).getContent.asInstanceOf[InputStream]
        BitmapFactory.decodeStream(is)
      }
    }

  def saveBitmap(file: File, bitmap: Bitmap): Task[NineCardsException \/ Unit] =
    Task {
      fromTryCatchNineCardsException[Unit] {
        val out: FileOutputStream = new FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        out.flush()
        out.close()
      }
    }

  def getBitmapByAppOrName(packageName: String, icon: Int, name: String)(implicit context: ContextSupport, config: ImageServicesConfig): Task[NineCardsException \/ Bitmap] =
    manageBitmapTask(name)(getBitmapByApp(packageName, icon))

  def getBitmapFromURLOrName(url: String, name: String)(implicit context: ContextSupport, config: ImageServicesConfig): Task[NineCardsException \/ Bitmap] =
    manageBitmapTask(name)(getBitmapFromURL(url))

  def getBitmapByName(text: String)(implicit context: ContextSupport, config: ImageServicesConfig): NineCardsException \/ Bitmap =
    fromTryCatchNineCardsException[Bitmap] {
        val bitmap: Bitmap = Bitmap.createBitmap(defaultSize, defaultSize, Bitmap.Config.RGB_565)
        val bounds: Rect = new Rect
        val paint = defaultPaint
        paint.getTextBounds(text, 0, text.length, bounds)
        val x: Int = ((defaultSize / 2) - bounds.exactCenterX).toInt
        val y: Int = ((defaultSize / 2) - bounds.exactCenterY).toInt
        val canvas: Canvas = new Canvas(bitmap)
        val color = config.colors(scala.util.Random.nextInt(config.colors.length))
        canvas.drawColor(color)
        canvas.drawText(text, x, y, paint)
        bitmap
      }

  private[this] def manageBitmapTask(name: String)(getBitmap: => Task[NineCardsException \/ Bitmap])(implicit context: ContextSupport, config: ImageServicesConfig) =
    getBitmap map {
      case -\/(_) => getBitmapByName(name)
      case \/-(r) => \/-(r)
    }

  private[this] def currentDensity(implicit context: ContextSupport): Int =
    context.getResources.getDisplayMetrics.densityDpi

  private[this] def densities(implicit context: ContextSupport): List[Int] = currentDensity match {
    case DisplayMetrics.DENSITY_HIGH =>
      List(DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
    case _ =>
      List(DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
  }

  private[this] def betterDensityForResource(resources: Resources, id: Int)(implicit context: ContextSupport) =
    densities.find(density => Try(resources.getValueForDensity(id, density, new TypedValue, true)).isSuccess) getOrElse noDensity

  private[this] def defaultSize(implicit context: ContextSupport): Int = {
    val metrics: DisplayMetrics = context.getResources.getDisplayMetrics
    val width: Int = metrics.widthPixels / 3
    val height: Int = metrics.heightPixels / 3
    if (width > height) width else height
  }

  private[this] def defaultPaint(implicit context: ContextSupport): Paint = {
    def determineMaxTextSize(maxWidth: Float): Int = {
      var size: Int = 0
      val paint: Paint = new Paint
      do {
        size = size + 1
        paint.setTextSize(size)
      } while (paint.measureText("M") < maxWidth)
      size
    }
    val paint = new Paint
    paint.setColor(Color.WHITE)
    paint.setStyle(Paint.Style.FILL)
    paint.setAntiAlias(true)
    paint.setTextSize(determineMaxTextSize((defaultSize / 3) * 2))
    paint
  }

  private[this] def tryIconByDensity(resources: Resources, icon: Int, density: Int) =
    Try {
      val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) resources.getDrawableForDensity(icon, density, null)
      else resources.getDrawableForDensity(icon, density)
      d.asInstanceOf[BitmapDrawable].getBitmap
    }

  private[this] def tryIconByPackageName(packageName: String)(implicit context: ContextSupport) =
    Try {
      context.getPackageManager.getApplicationIcon(packageName).asInstanceOf[BitmapDrawable].getBitmap
    }

}

object ImageServicesTasks extends ImageServicesTasks