package com.fortysevendeg.ninecardslauncher.process.theme

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.Service.ServiceDef2
import com.fortysevendeg.ninecardslauncher.commons.utils.AssetException
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme

trait ThemeProcess {
  def getTheme(themeFile: String)(implicit context: ContextSupport): ServiceDef2[NineCardsTheme, AssetException with ThemeException]
}
