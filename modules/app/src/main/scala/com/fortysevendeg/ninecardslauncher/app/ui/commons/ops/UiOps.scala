package com.fortysevendeg.ninecardslauncher.app.ui.commons.ops

import android.view.View
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{ImplicitsUiExceptions, UiException}
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import macroid.Ui

import scala.concurrent.Future

object UiOps {

  implicit class OptionViewOp[T <: View](maybeView: Option[T]) {

    def mapUi(f: (T) => Ui[_]): Ui[_] = maybeView map f getOrElse Ui.nop

    def mapUiF(f: (T) => Ui[Future[_]]): Ui[Future[_]] = maybeView map f getOrElse Ui(Future.successful(()))

    def ifUi[T](doUi: Boolean)(ui: () => Ui[T]) = if (doUi) ui() else Ui.nop

  }

  implicit class UiActionsOp(ui: Ui[_]) {

    def ifUi[T](doUi: Boolean) = if (doUi) ui else Ui.nop

  }

  implicit class ServiceUi(ui: Ui[Any]) extends ImplicitsUiExceptions {

    def toService: TaskService[Unit] = TaskService {
      CatchAll[UiException](ui.run)
    }

  }

}
