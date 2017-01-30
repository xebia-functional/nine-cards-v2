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

package cards.nine.app.ui.launcher.exceptions

import cards.nine.commons.services.TaskService.NineCardException

case class SpaceException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsSpaceException {
  implicit def momentException =
    (t: Throwable) => SpaceException(t.getMessage, Option(t))
}

case class LoadDataException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsLoadDataException {
  implicit def loadDataExceptionConverter =
    (t: Throwable) => LoadDataException(t.getMessage, Option(t))
}

case class ChangeMomentException(
    message: String,
    cause: Option[Throwable] = None,
    recoverable: Boolean = false)
    extends RuntimeException(message)
    with NineCardException {

  cause foreach initCause

}

trait ImplicitsChangeMomentException {
  implicit def changeMomentExceptionConverter =
    (t: Throwable) => ChangeMomentException(t.getMessage, Option(t))
}
