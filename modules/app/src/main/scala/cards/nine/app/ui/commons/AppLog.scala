/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
