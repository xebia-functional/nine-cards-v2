package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.util.Log

object AppLog {

  val tag = "9cards"

  def printErrorMessage(ex: Throwable, message: Option[String] = None) = {
    Log.e(tag, message getOrElse errorMessage(ex), ex)
  }

  def printErrorTaskMessage(header: String, exs: Seq[Throwable]) = {
    Log.e(tag, header)
    exs foreach (ex => Log.e(tag, errorMessage(ex), ex))
  }

  private[this] def errorMessage(ex: Throwable): String =
    Option(ex.getMessage) getOrElse ex.getClass.toString

}
