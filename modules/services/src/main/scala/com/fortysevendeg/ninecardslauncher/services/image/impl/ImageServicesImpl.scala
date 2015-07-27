package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.exceptions.Exceptions.NineCardsException
import com.fortysevendeg.ninecardslauncher.services.image._

import scalaz._
import Scalaz._
import EitherT._
import scalaz.concurrent.Task
import com.fortysevendeg.ninecardslauncher.commons.services.Service._

class ImageServicesImpl(config: ImageServicesConfig, imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
    extends ImageServices
    with Conversions {

  implicit val implicitConfig: ImageServicesConfig = config

  override def saveAppIcon(request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ AppPackagePath] =
    for {
      file <- imageServicesTasks.getPathByApp(request.packageName, request.className) ▹ eitherT
      appPackagePath <- createIfNotExists(file, request) ▹ eitherT
    } yield appPackagePath

  override def saveAppIcon(request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ AppWebsitePath] =
    for {
      file <- imageServicesTasks.getPathByPackageName(request.packageName) ▹ eitherT
      appWebsitePath <- createIfNotExists(file, request) ▹ eitherT
    } yield appWebsitePath

  private[this] def createIfNotExists(file: File, request: AppPackage)(implicit context: ContextSupport): Task[NineCardsException \/ AppPackagePath] =
    file.exists match {
      case true =>
        Task { \/-(toAppPackagePath(request, file.getAbsolutePath)) }
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapByAppOrName(request.packageName, request.icon, request.name) ▹ eitherT
          _ <- imageServicesTasks.saveBitmap(file, bitmap) ▹ eitherT
        } yield toAppPackagePath(request, file.getAbsolutePath)
    }

  private[this] def createIfNotExists(file: File, request: AppWebsite)(implicit context: ContextSupport): Task[NineCardsException \/ AppWebsitePath] =
    file.exists match {
      case true =>
        Task { \/-(toAppWebsitePath(request, file.getAbsolutePath)) }
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapFromURLOrName(request.url, request.name) ▹ eitherT
          _ <- imageServicesTasks.saveBitmap(file, bitmap) ▹ eitherT
        } yield toAppWebsitePath(request, file.getAbsolutePath)
    }

}
