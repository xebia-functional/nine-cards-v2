package com.fortysevendeg.ninecardslauncher.services.image.impl

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.services.image._

class ImageServicesImpl(config: ImageServicesConfig, imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
  extends ImageServices
  with Conversions {

  implicit val implicitConfig: ImageServicesConfig = config

  override def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport) = {

    val uniqueName = com.gilt.timeuuid.TimeUuid().toString

    for {
      file <- imageServicesTasks.getPathByName(uniqueName)
      _ <- imageServicesTasks.saveBitmap(file, request.bitmap)
    } yield SaveBitmapPath(uniqueName, file.getAbsolutePath)
  }

}
