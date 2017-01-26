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

package cards.nine.services.persistence

import cards.nine.commons.services.TaskService.NineCardException

case class AndroidIdNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class InstallationNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class PersistenceServiceException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

case class UserNotFoundException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with NineCardException {
  cause map initCause
}

trait ImplicitsPersistenceServiceExceptions {

  implicit def androidIdNotFoundException =
    (t: Throwable) => AndroidIdNotFoundException(t.getMessage, Option(t))

  implicit def installationNotFoundException =
    (t: Throwable) => InstallationNotFoundException(t.getMessage, Option(t))

  implicit def persistenceServiceException =
    (t: Throwable) => PersistenceServiceException(t.getMessage, Option(t))

  implicit def userNotFoundException =
    (t: Throwable) => UserNotFoundException(t.getMessage, Option(t))
}
