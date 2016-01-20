package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.util.Log
import com.crashlytics.android.Crashlytics

object AppLog {

  val tag = "9cards"

  def printErrorMessage(ex: Throwable, message: Option[String] = None) = {
    Crashlytics.logException(ex)
    Log.e(tag, message getOrElse errorMessage(ex), ex)
  }

  def printErrorTaskMessage(header: String, exs: Seq[Throwable]) = {
    Log.e(tag, header)
    exs foreach (ex => printErrorMessage(ex, Some(errorMessage(ex))))
  }

  private[this] def errorMessage(ex: Throwable): String =
    Option(ex.getMessage) getOrElse ex.getClass.toString

}
