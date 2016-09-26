package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait ShortcutsDOM {

  self: TypedFindView =>

  lazy val recycler = findView(TR.actions_recycler)

}

trait ShortcutsUiListener {

  def loadShortcuts(): Unit

  def onConfigure(shortcut: Shortcut): Unit

}