package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.services.image._
import rapture.core.Result
import rapture.core.scalazInterop.ResultT

import scalaz.concurrent.Task

class ImageServicesImpl(config: ImageServicesConfig, imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
  extends ImageServices
  with Conversions {

  implicit val implicitConfig: ImageServicesConfig = config

  override def saveAppIcon(request: AppPackage)(implicit contextSupport: ContextSupport) = for {
    file <- imageServicesTasks.getPathByApp(request.packageName, request.className)
    appPackagePath <- createIfNotExists(file, request)
  } yield appPackagePath

  override def saveAppIcon(request: AppWebsite)(implicit contextSupport: ContextSupport) = for {
    file <- imageServicesTasks.getPathByName(request.packageName)
    appWebsitePath <- createIfNotExists(file, request)
  } yield appWebsitePath

  override def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport) = for {
    file <- imageServicesTasks.getPathByName(request.name)
    _ <- imageServicesTasks.saveBitmap(file, request.bitmap)
  } yield SaveBitmapPath(request.name, file.getAbsolutePath)

  private[this] def createIfNotExists(file: File, request: AppPackage)(implicit contextSupport: ContextSupport):
  ServiceDef2[AppPackagePath, BitmapTransformationException with FileException] =
    file.exists match {
      case true =>
        ResultT(Task(Result.answer(toAppPackagePath(request, file.getAbsolutePath))))
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapByAppOrName(request.packageName, request.icon, request.name)
          _ <- imageServicesTasks.saveBitmap(file, bitmap)
        } yield toAppPackagePath(request, file.getAbsolutePath)
    }


  private[this] def createIfNotExists(file: File, request: AppWebsite)(implicit contextSupport: ContextSupport):
  ServiceDef2[AppWebsitePath, BitmapTransformationException with FileException] =
    file.exists match {
      case true =>
        ResultT(Task(Result.answer(toAppWebsitePath(request, file.getAbsolutePath))))
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapFromURLOrName(request.url, request.name)
          _ <- imageServicesTasks.saveBitmap(file, bitmap)
        } yield toAppWebsitePath(request, file.getAbsolutePath)
    }

}
