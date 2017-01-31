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

package cards.nine.process.device.impl

import cards.nine.commons.NineCardExtensions._
import cards.nine.commons.services.TaskService._
import cards.nine.process.device.{DeviceProcess, ImplicitsDeviceException, ResetException}
import cards.nine.services.persistence.ImplicitsPersistenceServiceExceptions

trait ResetProcessImpl extends DeviceProcess {

  self: DeviceProcessDependencies
    with ImplicitsDeviceException
    with ImplicitsPersistenceServiceExceptions =>

  def resetSavedItems() =
    (for {
      _ <- persistenceServices.deleteAllWidgets()
      _ <- persistenceServices.deleteAllCollections()
      _ <- persistenceServices.deleteAllCards()
      _ <- persistenceServices.deleteAllDockApps()
    } yield ()).resolve[ResetException]

}
