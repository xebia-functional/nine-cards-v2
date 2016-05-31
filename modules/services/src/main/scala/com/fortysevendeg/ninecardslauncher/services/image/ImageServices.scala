package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2

trait ImageServices {

  /**
   * Write a compressed version of the bitmap pass by parameter and return the path
   * @return the com.fortysevendeg.ninecardslauncher.services.image.SaveBitmapPath contains
   *         path where the file was stored
   * @throws FileException if exist some problem storing bitmap
   */
  def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport): ServiceDef2[SaveBitmapPath, FileException]
  
}
