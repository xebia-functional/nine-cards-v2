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

import cards.nine.commons.services.TaskService.NineCardException

case class UiException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsUiExceptions {
  implicit def uiExceptionConverter =
    (t: Throwable) => UiException(t.getMessage, Option(t))
}

case class ObserverException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsObserverExceptions {
  implicit def observerExceptionConverter =
    (t: Throwable) => ObserverException(t.getMessage, Option(t))
}

case class JobException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsJobExceptions {
  implicit def jobExceptionConverter =
    (t: Throwable) => JobException(t.getMessage, Option(t))
}
