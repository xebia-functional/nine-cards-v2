package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import macroid.Ui

import scala.concurrent.Future

object UiOps {

  implicit class OptionViewOp(maybeView: Option[View]) {

    def mapUi(f: (View) => Ui[_]): Ui[_] = maybeView map f getOrElse Ui.nop

    def mapUiF(f: (View) => Ui[Future[_]]): Ui[Future[_]] = maybeView map f getOrElse Ui(Future.successful())

  }

}
