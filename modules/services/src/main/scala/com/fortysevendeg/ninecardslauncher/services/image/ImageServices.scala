package com.fortysevendeg.ninecardslauncher.services.image

import java.io.IOException

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2

trait ImageServices {

  /** Obtains the path from package creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppPackage)(implicit contextSupport: ContextSupport): ServiceDef2[AppPackagePath, BitmapTransformationException with IOException]

  /** Obtains the path from url creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppWebsite)(implicit contextSupport: ContextSupport): ServiceDef2[AppWebsitePath, BitmapTransformationException with IOException]

}
