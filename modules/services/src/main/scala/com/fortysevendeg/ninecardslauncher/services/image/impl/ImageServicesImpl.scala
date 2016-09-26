package com.fortysevendeg.ninecardslauncher.services.image.impl

import android.media.ThumbnailUtils
import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.image._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._


class ImageServicesImpl(config: ImageServicesConfig, imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
  extends ImageServices {

  implicit val implicitConfig: ImageServicesConfig = config

  override def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport) = {

    val uniqueName = com.gilt.timeuuid.TimeUuid().toString

    def resizeBitmap = request.bitmapResize match {
      case Some(resize) =>
        ThumbnailUtils.extractThumbnail(
          request.bitmap,
          resize.width,
          resize.height,
          ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
      case _ => request.bitmap
    }

    for {
      file <- imageServicesTasks.getPathByName(uniqueName)
      _ <- imageServicesTasks.saveBitmap(file, resizeBitmap)
    } yield SaveBitmapPath(uniqueName, file.getAbsolutePath)
  }

}
