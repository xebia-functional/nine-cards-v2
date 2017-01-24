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

package cards.nine.app.receivers.bluetooth

import cards.nine.app.ui.commons.action_filters.MomentBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ContextWrapper

class BluetoothJobs(implicit contextWrapper: ContextWrapper) extends Jobs {

  def addBluetoothDevice(device: String): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.addBluetoothDevice(device))
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

  def removeBluetoothDevice(device: String): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.removeBluetoothDevice(device))
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

  def removeAllBluetoothDevices(): TaskService[Unit] =
    for {
      _ <- TaskService.right(contextSupport.clearBluetoothDevices())
      _ <- sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))
    } yield ()

}
