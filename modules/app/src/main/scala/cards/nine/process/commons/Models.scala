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

package cards.nine.process.commons

import com.google.android.gms.common.api.GoogleApiClient

sealed trait ConnectionSuspendedCause

case object CauseNetworkLost extends ConnectionSuspendedCause

case object CauseServiceDisconnected extends ConnectionSuspendedCause

case object CauseUnknown extends ConnectionSuspendedCause

object ConnectionSuspendedCause {

  def apply(cause: Int): ConnectionSuspendedCause =
    cause match {
      case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST =>
        CauseNetworkLost
      case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED =>
        CauseServiceDisconnected
      case _ => CauseUnknown
    }

}
