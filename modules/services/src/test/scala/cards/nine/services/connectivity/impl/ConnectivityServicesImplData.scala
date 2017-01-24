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

package cards.nine.services.connectivity.impl

import android.net.wifi.WifiConfiguration

trait ConnectivityServicesImplData {

  val ssidResult = "My Wifi"

  val ssid: String = "\"" + ssidResult + "\""

  val ssidWithQuotesResult = "My wifi with \"quotes\""

  val ssidWithQuotes: String = "\"" + ssidWithQuotesResult + "\""

  val ssidWithoutQuotes: String = ssidResult

  val ssidWithError: String = "\"\""

  val networksUnsorted = Seq("znf", "Abc", "47 deg", "trn", "bcb", "BB", "ant")

  val wifiConfigurations = networksUnsorted map { network =>
    val wc = new WifiConfiguration
    wc.SSID = network
    wc
  }

  val networksSorted = Seq("47 deg", "Abc", "ant", "BB", "bcb", "trn", "znf")

}
