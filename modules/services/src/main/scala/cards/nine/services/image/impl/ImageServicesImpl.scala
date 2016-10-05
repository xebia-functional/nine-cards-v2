package cards.nine.services.image.impl

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.BitmapPath
import cards.nine.services.image._

class ImageServicesImpl(imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
  extends ImageServices {

  override def saveBitmap(
    bitmap: Bitmap, maybeWidth: Option[Int], maybeHeight: Option[Int])(implicit contextSupport: ContextSupport) = {

    val uniqueName = com.gilt.timeuuid.TimeUuid().toString

    def resizeBitmap = (maybeWidth, maybeHeight) match {
      case (Some(width), Some(height)) =>
        ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
      case _ => bitmap
    }

    for {
      file <- imageServicesTasks.getPathByName(uniqueName)
      _ <- imageServicesTasks.saveBitmap(file, resizeBitmap)
    } yield BitmapPath(uniqueName, file.getAbsolutePath)
  }

}
