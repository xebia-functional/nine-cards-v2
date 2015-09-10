package com.fortysevendeg.ninecardslauncher.services.image

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2

trait ImageServices {

  /**
   * Obtains the path from package creating a new entry if non is found the very first time
   * @return the com.fortysevendeg.ninecardslauncher.services.image.AppPackagePath contains
   *         path where the file was stored
   * @throws BitmapTransformationException if exist some problem creating bitmap
   */
  def saveAppIcon(request: AppPackage)(implicit contextSupport: ContextSupport): ServiceDef2[AppPackagePath, BitmapTransformationException with FileException]

  /**
   * Obtains the path from url creating a new entry if non is found the very first time
   * @return the com.fortysevendeg.ninecardslauncher.services.image.AppWebsitePath contains
   *         path where the file was stored
   * @throws BitmapTransformationException if exist some problem creating bitmap
   */
  def saveAppIcon(request: AppWebsite)(implicit contextSupport: ContextSupport): ServiceDef2[AppWebsitePath, BitmapTransformationException with FileException]

  /**
   * Write a compressed version of the bitmap pass by parameter and return the path
   * @return the com.fortysevendeg.ninecardslauncher.services.image.SaveBitmapPath contains
   *         path where the file was stored
   * @throws FileException if exist some problem storing bitmap
   */
  def saveBitmap(request: SaveBitmap)(implicit contextSupport: ContextSupport): ServiceDef2[SaveBitmapPath, FileException]


}
