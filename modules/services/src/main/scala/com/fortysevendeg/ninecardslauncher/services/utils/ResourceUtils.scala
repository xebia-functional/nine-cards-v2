package cards.nine.services.utils

import cards.nine.commons.contexts.ContextSupport

class ResourceUtils {

  def getPath(filename: String)(implicit context: ContextSupport): String =
    s"${context.getAppIconsDir.getPath}/$filename"

}
