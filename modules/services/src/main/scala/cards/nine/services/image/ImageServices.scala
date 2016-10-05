package cards.nine.services.image

import cards.nine.commons.contexts.ContextSupport
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.models.{SaveBitmap, SaveBitmapPath}

trait ImageServices {

  /**
   * Write a compressed version of the bitmap pass by parameter and return the path
   * @return the cards.nine.services.image.SaveBitmapPath contains
   *         path where the file was stored
   * @throws FileException if exist some problem storing bitmap
   */
  def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport): TaskService[SaveBitmapPath]
  
}
