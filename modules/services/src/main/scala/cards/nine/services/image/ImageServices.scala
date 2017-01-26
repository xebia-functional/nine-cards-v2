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

package cards.nine.services.image

import android.content.Intent.ShortcutIconResource
import android.graphics.Bitmap
import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.BitmapPath

trait ImageServices {

  /**
   * Write a compressed version of the bitmap pass by parameter and return the path
   * @param bitmap the bitmap to save
   * @param width the width of the bitmap to save
   * @param height the height of the bitmap to save
   * @return the cards.nine.services.image.SaveBitmapPath contains
   *         path where the file was stored
   * @throws FileException if exist some problem storing bitmap
   */
  def saveBitmap(bitmap: Bitmap, width: Option[Int], height: Option[Int])(
      implicit contextSupport: ContextSupport): TaskService[BitmapPath]

  /**
   * Decode a Bitmap from a ShortcutIconResource
   * @param resource the ShortcutIconResource
   * @return the decoded Bitmap
   */
  def decodeShortcutIconResource(resource: ShortcutIconResource)(
      implicit context: ContextSupport): TaskService[Bitmap]

}
