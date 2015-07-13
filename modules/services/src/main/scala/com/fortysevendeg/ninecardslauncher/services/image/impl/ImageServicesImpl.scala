package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import android.graphics.Bitmap
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image.{ImageServicesConfig, AppWebsite, AppPackage, ImageServices}

import scalaz._
import Scalaz._
import EitherT._
import scalaz.concurrent.Task
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

class ImageServicesImpl(config: ImageServicesConfig, tasks: ImageServicesTasks = ImageServicesTasks)
    extends ImageServices {

  implicit val implicitConfig: ImageServicesConfig = config

  override def getAppPackagePathAndSaveIfNotExists(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    tasks.getPathByApp(request.packageName, request.className) map {
      case -\/(ex) => -\/(NineCardsException(msg = "Not possible get the file", cause = ex.some))
      case \/-(file) =>
        createIfNotExists(file)(tasks.getBitmapByAppOrName(request.packageName, request.icon, request.name))
    }

  override def getAppWebsitePathAndSaveIfNotExists(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    tasks.getPathByPackageName(request.packageName) map {
      case -\/(ex) => -\/(NineCardsException(msg = "Not possible get the file", cause = ex.some))
      case \/-(file) =>
        createIfNotExists(file)(tasks.getBitmapFromURLOrName(request.url, request.name))
    }

  private[this] def createIfNotExists(file: File)(getBitmap: => Task[NineCardsException \/ Bitmap]): NineCardsException \/ String =
    file.exists match {
      case true =>
        \/-(file.getAbsolutePath)
      case false =>
        val result: Task[NineCardsException \/ String] = for {
          bitmap <- getBitmap ▹ eitherT
          _ <- tasks.saveBitmap(file, bitmap) ▹ eitherT
        } yield file.getAbsolutePath
        toEnsureAttemptRun(result)
    }

}
