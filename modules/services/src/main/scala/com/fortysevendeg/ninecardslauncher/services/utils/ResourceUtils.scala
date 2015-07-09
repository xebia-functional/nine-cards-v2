package com.fortysevendeg.ninecardslauncher.services.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

class ResourceUtils {

  def getPath(filename: String)(implicit context: ContextSupport): String =
    String.format("%s/%s", context.getAppIconsDir.getPath, filename)

  def getPathPackage(packageName: String, className: String)(implicit context: ContextSupport): String =
    getPath(String.format("%s_%s", packageName.toLowerCase.replace(".", "_"), className.toLowerCase.replace(".", "_")))

  def packageToFilename(packageName: String, className: String): String =
    String.format("%s_%s", packageName.toLowerCase.replace(".", "_"), className.toLowerCase.replace(".", "_"))

}
