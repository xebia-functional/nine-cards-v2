package com.fortysevendeg.ninecardslauncher.app.ui.wizard

import macroid.Ui

trait WizardListeners {

  def requestToken(username: String): Unit

  def launchService(maybeKey: Option[String]): Unit

  def finishUi: Ui[_]

}
