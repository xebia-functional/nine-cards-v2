package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image._

import scalaz._
import Scalaz._
import EitherT._
import scalaz.concurrent.Task
import com.fortysevendeg.ninecardslauncher.commons.services.Service._
import com.fortysevendeg.ninecardslauncher.commons.NineCardExtensions._

class ImageServicesImpl(config: ImageServicesConfig, imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
    extends ImageServices
    with Conversions {

  implicit val implicitConfig: ImageServicesConfig = config

  override def saveAppIcon(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ AppPackagePath] =
    imageServicesTasks.getPathByApp(request.packageName, request.className) map {
      case -\/(ex) => -\/(NineCardsException(msg = "Not possible get the file", cause = ex.some))
      case \/-(file) => createIfNotExists(file, request)
    }

  override def saveAppIcon(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ AppWebsitePath] =
    imageServicesTasks.getPathByPackageName(request.packageName) map {
      case -\/(ex) => -\/(NineCardsException(msg = "Not possible get the file", cause = ex.some))
      case \/-(file) => createIfNotExists(file, request)
    }

  private[this] def createIfNotExists(file: File, request: AppPackage)(implicit context: ContextSupport): NineCardsException \/ AppPackagePath =
    file.exists match {
      case true =>
        \/-(toAppPackagePath(request, file.getAbsolutePath))
      case false =>
        val result: Task[NineCardsException \/ AppPackagePath] = for {
          bitmap <- imageServicesTasks.getBitmapByAppOrName(request.packageName, request.icon, request.name) ▹ eitherT
          _ <- imageServicesTasks.saveBitmap(file, bitmap) ▹ eitherT
        } yield toAppPackagePath(request, file.getAbsolutePath)
        toEnsureAttemptRun(result)
    }

  private[this] def createIfNotExists(file: File, request: AppWebsite)(implicit context: ContextSupport): NineCardsException \/ AppWebsitePath =
    file.exists match {
      case true =>
        \/-(toAppWebsitePath(request, file.getAbsolutePath))
      case false =>
        val result: Task[NineCardsException \/ AppWebsitePath] = for {
          bitmap <- imageServicesTasks.getBitmapFromURLOrName(request.url, request.name) ▹ eitherT
          _ <- imageServicesTasks.saveBitmap(file, bitmap) ▹ eitherT
        } yield toAppWebsitePath(request, file.getAbsolutePath)
        toEnsureAttemptRun(result)
    }

}
