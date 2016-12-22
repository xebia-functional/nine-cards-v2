package cards.nine.app.ui.commons

import android.util.Log
import com.crashlytics.android.Crashlytics

object AppLog {

  val tag = "9cards"

  def invalidConfigurationV2: Unit = info("Invalid configuration for backend V2")

  def info(message: String): Unit = Log.i(tag, message)

  def printErrorMessage(ex: Throwable, message: Option[String] = None): Unit = {
    try {
      val outputEx = Option(ex.getCause) getOrElse ex
      Log.e(tag, message getOrElse errorMessage(outputEx), outputEx)
      Crashlytics.logException(ex)
    } catch { case _: Throwable => }
  }

  def printErrorTaskMessage(header: String, ex: Throwable): Unit = {
    try {
      Log.e(tag, header)
      printErrorMessage(ex, Some(errorMessage(ex)))
    } catch { case _: Throwable => }
  }

  private[this] def errorMessage(ex: Throwable): String =
    Option(ex.getMessage) getOrElse ex.getClass.toString

}
