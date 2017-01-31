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

package cards.nine.app.receivers.moments

import cards.nine.app.ui.commons.action_filters.MomentBestAvailableActionFilter
import cards.nine.app.ui.commons.{BroadAction, Jobs}
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.InVehicleFence
import macroid.ContextWrapper

class ConnectionStatusChangedJobs(implicit contextWrapper: ContextWrapper) extends Jobs {

  def connectionStatusChanged(): TaskService[Unit] =
    sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action))

  def headphoneStatusChanged(key: String): TaskService[Unit] =
    sendBroadCastTask(BroadAction(MomentBestAvailableActionFilter.action, Option(key)))

  def inVehicleStatusChanged(): TaskService[Unit] =
    sendBroadCastTask(
      BroadAction(MomentBestAvailableActionFilter.action, Option(InVehicleFence.key)))

}

object ConnectionStatusChangedJobs {

  val action = "android.net.conn.CONNECTIVITY_CHANGE"

}
