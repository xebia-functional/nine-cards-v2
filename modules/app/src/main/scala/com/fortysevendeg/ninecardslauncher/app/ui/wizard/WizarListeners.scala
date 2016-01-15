package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import macroid.Ui

trait WizarListeners {

  def requestToken(username: String): Unit

  def launchService(maybeKey: Option[String]): Unit

  def finishUi: Ui[_]

}
