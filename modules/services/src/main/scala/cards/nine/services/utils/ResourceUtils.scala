package com.fortysevendeg.ninecardslauncher.services.utils

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport

class ResourceUtils {

  def getPath(filename: String)(implicit context: ContextSupport): String =
    s"${context.getAppIconsDir.getPath}/$filename"

}
