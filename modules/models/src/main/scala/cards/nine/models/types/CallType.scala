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

package cards.nine.models.types

import android.provider.CallLog

sealed trait CallType

case object IncomingType extends CallType

case object OutgoingType extends CallType

case object MissedType extends CallType

case object OtherType extends CallType

object CallType {

  def apply(mode: Int): CallType = mode match {
    case CallLog.Calls.INCOMING_TYPE => IncomingType
    case CallLog.Calls.OUTGOING_TYPE => OutgoingType
    case CallLog.Calls.MISSED_TYPE   => MissedType
    case _                           => OtherType
  }

}
