package cards.nine.app.ui.commons.ops

import cards.nine.app.ui.commons.AppLog

import scala.util.{Failure, Try}

object TryOps {

  implicit class FailureLogOps[T](t: Try[T]) {

    val tag = AppLog.tag

    def logInfo(): Unit = log(Info)

    def logDebug(): Unit = log(Debug)

    def logError(): Unit = log(Error)

    def logVerbose(): Unit = log(Verbose)

    def log(level: LogLevel): Unit =
      t match {
        case Failure(e) =>
          Option(e.getMessage) foreach { m =>
            level match {
              case Info    => android.util.Log.i(tag, m, e)
              case Debug   => android.util.Log.d(tag, m, e)
              case Error   => android.util.Log.e(tag, m, e)
              case Verbose => android.util.Log.v(tag, m, e)
            }
          }
        case _ =>
      }

  }

  sealed trait LogLevel

  case object Info extends LogLevel

  case object Debug extends LogLevel

  case object Error extends LogLevel

  case object Verbose extends LogLevel

}
