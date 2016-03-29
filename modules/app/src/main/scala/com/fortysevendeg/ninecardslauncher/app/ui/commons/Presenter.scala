package com.fortysevendeg.ninecardslauncher.app.ui.commons

import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import macroid.ContextWrapper

class Presenter(implicit contextWrapper: ContextWrapper)
  extends ContextSupportProvider {

  implicit lazy val di = new Injector

}
