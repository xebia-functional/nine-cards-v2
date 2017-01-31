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

package cards.nine.process.accounts

import cards.nine.commons.services.TaskService.NineCardException

trait UserAccountsProcessException extends NineCardException

case class UserAccountsProcessExceptionImpl(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

case class UserAccountsProcessPermissionException(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

case class UserAccountsProcessOperationCancelledException(
    message: String,
    cause: Option[Throwable] = None)
    extends RuntimeException(message)
    with UserAccountsProcessException {
  cause map initCause
}

trait ImplicitsAccountsProcessExceptions {

  implicit def accountsServicesExceptionConverter =
    (t: Throwable) => UserAccountsProcessExceptionImpl(t.getMessage, Option(t))
}
