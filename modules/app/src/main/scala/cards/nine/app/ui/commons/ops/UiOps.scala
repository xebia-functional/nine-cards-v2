package cards.nine.app.ui.commons.ops

import android.view.View
import cards.nine.app.ui.commons.UiException
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cats.syntax.either._
import macroid.Ui
import monix.eval.Task

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UiOps {

  implicit class OptionViewOp[T <: View](maybeView: Option[T]) {

    def mapUi(f: (T) => Ui[_]): Ui[_] = maybeView map f getOrElse Ui.nop

    def mapUiF(f: (T) => Ui[Future[_]]): Ui[Future[_]] = maybeView map f getOrElse Ui(Future.successful(()))

    def ifUi[T](doUi: Boolean)(ui: () => Ui[T]): Ui[_] = if (doUi) ui() else Ui.nop

  }

  implicit class UiActionsOp(ui: Ui[_]) {

    def ifUi[T](doUi: Boolean) = if (doUi) ui else Ui.nop

  }

  implicit class UiActionsFutureOp(ui: Ui[Future[_]]) {

    def ifUi[T](doUi: Boolean) = if (doUi) ui else Ui(Future.successful(()))

  }

  implicit class ServiceUi(ui: Ui[Any]) {

    def toService: TaskService[Unit] = TaskService {
      Task.defer {
        Task.fromFuture {
          ui.run map { _ =>
            Either.right[UiException, Unit](())
          } recover {
            case ex: Throwable => Either.left(UiException("", Option(ex)))
          }
        }
      }
    }

  }

}
