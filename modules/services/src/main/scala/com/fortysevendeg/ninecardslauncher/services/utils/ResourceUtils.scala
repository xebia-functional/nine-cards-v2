package com.fortysevendeg.ninecardslauncher.services.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

class ResourceUtils {

  def getPath(filename: String)(implicit context: ContextSupport): String =
    s"${context.getAppIconsDir.getPath}/$filename"

  def getPathPackage(packageName: String, className: String)(implicit context: ContextSupport): String =
    getPath(s"${replace(packageName)}_${replace(className)}")

  private[this] def replace(text: String) = text.toLowerCase.replace(".", "_")

}
