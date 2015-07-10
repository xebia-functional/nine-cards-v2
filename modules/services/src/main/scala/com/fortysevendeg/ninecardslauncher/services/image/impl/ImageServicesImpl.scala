package com.fortysevendeg.ninecardslauncher.services.image.impl

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
        if (file.exists()) {
          \/-(file.getAbsolutePath)
        } else {
          val result: Task[NineCardsException \/ String] = for {
            bitmap <- tasks.getBitmapByAppOrName(request.packageName, request.icon, request.name) ▹ eitherT
            _ <- tasks.saveBitmap(file, bitmap) ▹ eitherT
          } yield file.getAbsolutePath
          result.run
        }
    }

  override def getAppWebsitePathAndSaveIfNotExists(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ String] =
    tasks.getPathByPackageName(request.packageName) map {
      case -\/(ex) => -\/(NineCardsException(msg = "Not possible get the file", cause = ex.some))
      case \/-(file) =>
        if (file.exists()) {
          \/-(file.getAbsolutePath)
        } else {
          val result: Task[NineCardsException \/ String] = for {
            bitmap <- tasks.getBitmapFromURLOrName(request.url, request.name) ▹ eitherT
            _ <- tasks.saveBitmap(file, bitmap) ▹ eitherT
          } yield file.getAbsolutePath
          result.run
        }
    }

}
