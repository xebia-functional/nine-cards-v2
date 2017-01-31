/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.services.image.impl

import android.content.Intent.ShortcutIconResource
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService._
import cards.nine.models.BitmapPath
import cards.nine.services.image._

class ImageServicesImpl(imageServicesTasks: ImageServicesTasks = ImageServicesTasks)
    extends ImageServices {

  override def saveBitmap(bitmap: Bitmap, maybeWidth: Option[Int], maybeHeight: Option[Int])(
      implicit contextSupport: ContextSupport) = {

    val uniqueName = com.gilt.timeuuid.TimeUuid().toString

    def resizeBitmap = (maybeWidth, maybeHeight) match {
      case (Some(width), Some(height)) =>
        ThumbnailUtils
          .extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
      case _ => bitmap
    }

    for {
      file <- imageServicesTasks.getPathByName(uniqueName)
      _    <- imageServicesTasks.saveBitmap(file, resizeBitmap)
    } yield BitmapPath(uniqueName, file.getAbsolutePath)
  }

  override def decodeShortcutIconResource(resource: ShortcutIconResource)(
      implicit context: ContextSupport): TaskService[Bitmap] =
    imageServicesTasks.getBitmapFromShortcutIconResource(resource)

}
