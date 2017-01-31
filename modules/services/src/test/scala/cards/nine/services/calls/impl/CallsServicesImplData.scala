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

package cards.nine.services.calls.impl

import cards.nine.models.Call
import cards.nine.models.types._

trait CallsServicesImplData {

  val phoneHome   = "666666666"
  val phoneWork   = "777777777"
  val phoneMobile = "888888888"
  val phoneOther  = "999999999"

  val seqPhones        = Seq(phoneHome, phoneWork, phoneMobile, phoneOther)
  val seqPhoneCategory = Seq(PhoneHome, PhoneWork, PhoneMobile, PhoneOther)
  val seqCallType      = Seq(IncomingType, OutgoingType, MissedType, OtherType)

  val calls = generateCalls

  def generateCalls: Seq[Call] =
    0 to 3 map { i =>
      Call(seqPhones(i), Option(s"contact$i"), seqPhoneCategory(i), 1L, seqCallType(i))
    }

}
