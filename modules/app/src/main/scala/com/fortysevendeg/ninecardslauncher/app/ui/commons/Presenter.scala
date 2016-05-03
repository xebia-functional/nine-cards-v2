package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.{Injector, InjectorImpl}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import macroid.{ContextWrapper, Ui}
import rapture.core.Answer

class Presenter(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider {

  implicit lazy val di: Injector = new InjectorImpl

  def getTheme: NineCardsTheme = {
    di.themeProcess.getSelectedTheme.run.run match {
      case Answer(t) => t
      case _ => getDefaultTheme
    }
  }

}
