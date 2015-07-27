package com.fortysevendeg.ninecardslauncher.services.image

import java.io.IOException

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef

trait ImageServices {

  /** Obtains the path from package creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppPackage): ServiceDef[ContextSupport, AppPackagePath, BitmapTransformationException with IOException]

  /** Obtains the path from url creating a new entry if non is found the very first time */
  def saveAppIcon(request: AppWebsite): ServiceDef[ContextSupport, AppWebsitePath, BitmapTransformationException with IOException]

}
