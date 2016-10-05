package cards.nine.services.image

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
  def saveBitmap(bitmap: Bitmap, width: Option[Int], height: Option[Int])(implicit contextSupport: ContextSupport): TaskService[BitmapPath]
  
}
