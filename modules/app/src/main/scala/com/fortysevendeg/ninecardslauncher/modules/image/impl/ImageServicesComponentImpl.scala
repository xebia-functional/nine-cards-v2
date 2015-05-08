package com.fortysevendeg.ninecardslauncher.modules.image.impl

import java.io.{InputStream, FileOutputStream, File}
import java.net.{HttpURLConnection, URL}

import android.content.Context
import android.content.pm.{PackageManager, ActivityInfo, ResolveInfo}
import android.graphics._
import android.graphics.drawable.BitmapDrawable
import android.util.DisplayMetrics
import com.fortysevendeg.ninecardslauncher.commons.ContextWrapperProvider
import com.fortysevendeg.ninecardslauncher.commons.Service
import com.fortysevendeg.ninecardslauncher.modules.image.{StoreImageAppResponse, StoreImageAppRequest, ImageServices, ImageServicesComponent}
import com.fortysevendeg.ninecardslauncher2.R

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

trait ImageServicesComponentImpl
  extends ImageServicesComponent {

  self: ContextWrapperProvider =>

  lazy val imageServices = new ImageServicesImpl

  class ImageServicesImpl
    extends ImageServices {

    private var currentColor: Int = 0

    val color1 = contextProvider.application.getResources.getColor(R.color.background_default_1)
    val color2 = contextProvider.application.getResources.getColor(R.color.background_default_2)
    val color3 = contextProvider.application.getResources.getColor(R.color.background_default_3)
    val color4 = contextProvider.application.getResources.getColor(R.color.background_default_4)
    val color5 = contextProvider.application.getResources.getColor(R.color.background_default_5)

    val packageManager = contextProvider.application.getPackageManager

    val cacheDir = contextProvider.application.getDir("icons_apps", Context.MODE_PRIVATE)

    val (defaultSize, currentDensity) = {
      val metrics: DisplayMetrics = contextProvider.application.getResources.getDisplayMetrics
      val width: Int = metrics.widthPixels / 3
      val height: Int = metrics.heightPixels / 3
      val size = if (width > height) width else height
      (size, metrics.densityDpi)
    }

    val paint = {
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

    val densities: List[Int] = {
      val allDensities: List[Int] = List(
        DisplayMetrics.DENSITY_XXXHIGH,
        DisplayMetrics.DENSITY_XXHIGH,
        DisplayMetrics.DENSITY_XHIGH,
        DisplayMetrics.DENSITY_HIGH)

      currentDensity match {
        case DisplayMetrics.DENSITY_HIGH =>
          List(DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
        case _ =>
          List(DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_HIGH)
      }
    }

    def createAppBitmap(term: String, info: ResolveInfo): String = {
      val packageName: String = info.activityInfo.applicationInfo.packageName
      val filename: String = convertToFilename(info.activityInfo)

      if (!new File(getPath(filename)).exists) {
        getBestBitmapForApp(packageName, info.activityInfo.icon) map {
          bitmap =>
            saveBitmap(filename, bitmap)
        } getOrElse {
          val firstChar = Option(term) map {
            t => if (t.length > 0) term.substring(0, 1).toUpperCase else "_"
          } getOrElse "_"
          if (!new File(getPath(firstChar)).exists) {
            saveBitmap(firstChar, createDefaultBitmap(firstChar))
          } else {
            getPath(firstChar)
          }
        }
      } else {
        getPath(filename)
      }
    }

    override def getImagePath(packageName: String, className: String): String = getPath(convertToFilename(packageName, className))

    override def getPath(filename: String): String = String.format("%s/%s", cacheDir.getPath, filename)

    override def storeImageApp: Service[StoreImageAppRequest, StoreImageAppResponse] =
      request =>
        Future {
          getBitmapFromURL(request.url) map {
            bitmap =>
              saveBitmapDensity(request.packageName, bitmap)
              StoreImageAppResponse(Some(request.packageName))
          } getOrElse StoreImageAppResponse(None)
        }

    private def saveBitmapDensity(filename: String, bitmap: Bitmap): String = {
      import DisplayMetrics._
      val newBitmap = (currentDensity, bitmap) match {
        case (cd, bmp) if (cd == DENSITY_XXHIGH || cd == DENSITY_TV) && bmp.getWidth > 48 * 4 =>
          Bitmap.createScaledBitmap(bitmap, 48 * 4, 48 * 4, true)
        case (cd, bmp) if cd == DENSITY_XHIGH && bmp.getWidth > 48 * 3 =>
          Bitmap.createScaledBitmap(bitmap, 48 * 3, 48 * 3, true)
        case (cd, bmp) if cd == DENSITY_HIGH && bmp.getWidth > 48 * 2 =>
          Bitmap.createScaledBitmap(bitmap, 48 * 2, 48 * 2, true)
        case (cd, bmp) if cd == DENSITY_MEDIUM && bmp.getWidth > 48 * 1.5 =>
          Bitmap.createScaledBitmap(bitmap, (48 * 1.5).toInt, (48 * 1.5).toInt, true)
        case (_, bmp) => bmp
      }
      saveBitmap(filename, newBitmap)
    }

    private def createDefaultBitmap(text: String): Bitmap = {
      val bitmap: Bitmap = Bitmap.createBitmap(defaultSize, defaultSize, Bitmap.Config.RGB_565)
      val bounds: Rect = new Rect
      paint.getTextBounds(text, 0, text.length, bounds)
      val x: Int = ((defaultSize / 2) - bounds.exactCenterX).toInt
      val y: Int = ((defaultSize / 2) - bounds.exactCenterY).toInt
      val canvas: Canvas = new Canvas(bitmap)
      canvas.drawColor(getCurrentColor())
      canvas.drawText(text, x, y, paint)
      bitmap
    }

    private def convertToFilename(packageName: String, className: String): String =
      String.format("%s_%s", packageName.toLowerCase.replace(".", "_"), className.toLowerCase.replace(".", "_"))

    private def convertToFilename(activityInfo: ActivityInfo): String =
      convertToFilename(activityInfo.applicationInfo.packageName, activityInfo.name)

    private def getBestBitmapForApp(packageName: String, icon: Int): Option[Bitmap] = {
      def getBitmap(ic: Int): Option[Bitmap] = {
        Try {
          Option(packageManager.getResourcesForApplication(packageName)) flatMap {
            resources =>
              var bitmap: Option[Bitmap] = None
              for (density <- densities) {
                if (bitmap.isEmpty) {
                  bitmap = Try {
                    val d = resources.getDrawableForDensity(icon, density)
                    d.asInstanceOf[BitmapDrawable].getBitmap
                  } match {
                    case Success(response) => Some(response)
                    case Failure(ex) => None
                  }
                }
              }
              if (bitmap.isEmpty) {
                bitmap = Try {
                  packageManager.getApplicationIcon(packageName).asInstanceOf[BitmapDrawable].getBitmap
                } match {
                  case Success(response) => Some(response)
                  case Failure(ex) => None
                }
              }
              bitmap
          }
        } match {
          case Success(response) => response
          case Failure(ex) => None
        }
      }

      val ic = if (icon == 0) {
        Option(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)) map (_.icon)
      } else {
        Some(icon)
      }

      ic flatMap getBitmap

    }

    private def saveBitmap(filename: String, bitmap: Bitmap, force: Boolean = false): String = {
      val path: String = getPath(filename)
      if (force || !new File(filename).exists) {
        Try {
          val out: FileOutputStream = new FileOutputStream(path)
          bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
          out.flush()
          out.close()
        }
      }
      path
    }

    private def getBitmapFromURL(uri: String): Option[Bitmap] = {
      Try {
        val url: URL = new URL(uri)
        val connection: HttpURLConnection = url.openConnection.asInstanceOf[HttpURLConnection]
        connection.setDoInput(true)
        connection.connect()
        val input: InputStream = connection.getInputStream
        BitmapFactory.decodeStream(input)
      } match {
        case Success(bitmap) => Some(bitmap)
        case Failure(ex) => ex.printStackTrace(); None
      }
    }

    private def getCurrentColor(): Int = {
      val color = currentColor match {
        case 0 => color1
        case 1 => color2
        case 2 => color3
        case 3 => color4
        case _ => color5
      }
      currentColor = currentColor + 1
      color
    }


  }

}
