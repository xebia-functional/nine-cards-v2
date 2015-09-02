package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.view.View
import macroid.Ui

import scala.concurrent.Future

object UiOps {

  implicit class OptionViewOp[T <: View](maybeView: Option[T]) {

    def mapUi(f: (T) => Ui[_]): Ui[_] = maybeView map f getOrElse Ui.nop

    def mapUiF(f: (T) => Ui[Future[_]]): Ui[Future[_]] = maybeView map f getOrElse Ui(Future.successful())

  }

}
