package com.fortysevendeg.ninecardslauncher.services.image.impl

import java.io.File

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service
import com.fortysevendeg.ninecardslauncher.services.image._
import rapture.core.Result

import scalaz.concurrent.Task

class ImageServicesImpl
  extends ImageServices
  with Conversions {

  override def saveAppIcon(request: AppPackage) = Service { dependencies: ImageServicesTasks with ImageServicesConfig with ContextSupport =>
    for {
      file <- dependencies.getPathByApp(request.packageName, request.className)
      appPackagePath <- createIfNotExists(file, request)
    } yield appPackagePath
  }

  override def saveAppIcon(request: AppWebsite) = Service { dependencies: ImageServicesTasks with ImageServicesConfig with ContextSupport =>
    for {
      file <- dependencies.getPathByPackageName(request.packageName)
      appWebsitePath <- createIfNotExists(file, request)
    } yield appWebsitePath
  }

  private[this] def createIfNotExists(file: File, request: AppPackage) = Service { imageServicesTasks: ImageServicesTasks =>
    file.exists match {
      case true =>
        Task(Result.answer(toAppPackagePath(request, file.getAbsolutePath)))
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapByAppOrName(request.packageName, request.icon, request.name)
          _ <- imageServicesTasks.saveBitmap(file, bitmap)
        } yield toAppPackagePath(request, file.getAbsolutePath)
    }
  }

  private[this] def createIfNotExists(file: File, request: AppWebsite) = Service { imageServicesTasks: ImageServicesTasks =>
    file.exists match {
      case true =>
        Task(Result.answer(toAppWebsitePath(request, file.getAbsolutePath)))
      case false =>
        for {
          bitmap <- imageServicesTasks.getBitmapFromURLOrName(request.url, request.name)
          _ <- imageServicesTasks.saveBitmap(file, bitmap)
        } yield toAppWebsitePath(request, file.getAbsolutePath)
    }
  }

}
