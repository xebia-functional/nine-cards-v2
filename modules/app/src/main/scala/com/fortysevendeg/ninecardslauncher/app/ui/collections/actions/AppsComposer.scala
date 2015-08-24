package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions

import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait AppsComposer {

  self: TypedFindView =>

  lazy val toolbar = Option(findView(TR.actions_toolbar))

  lazy val recycler = Option(findView(TR.actions_recycler))

}
