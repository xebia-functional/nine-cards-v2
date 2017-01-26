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

package cards.nine.services.intents.impl

import cards.nine.models._

trait LauncherIntentServicesImplData {

  val runtimeException  = new RuntimeException("Irrelevant Message")
  val securityException = new SecurityException("Irrelevant Message")

  val packageName   = "package.name"
  val className     = "class.Name"
  val googlePlayUrl = "http://googlePlayUrl"
  val url           = "http://mockUrl"
  val lookupKey     = "lookupKey"
  val email         = "email@google.com"
  val titleDialog   = "Dialog Tile"
  val phoneNumber   = "666 66 66 66"
  val shareText     = "Share text"

  val appAction             = AppAction(packageName, className)
  val appGooglePlayAction   = AppGooglePlayAction(googlePlayUrl, packageName)
  val appLauncherAction     = AppLauncherAction(packageName)
  val appSettingsAction     = AppSettingsAction(packageName)
  val appUninstallAction    = AppUninstallAction(packageName)
  val contactAction         = ContactAction(lookupKey)
  val emailAction           = EmailAction(email, titleDialog)
  val globalSettingsAction  = GlobalSettingsAction
  val googlePlayStoreAction = GooglePlayStoreAction
  val googleWeatherAction   = GoogleWeatherAction
  val phoneSmsAction        = PhoneSmsAction(phoneNumber)
  val phoneCallAction       = PhoneCallAction(phoneNumber)
  val phoneDialAction       = PhoneDialAction(Some(phoneNumber))
  val searchGlobalAction    = SearchGlobalAction
  val searchVoiceAction     = SearchVoiceAction
  val searchWebAction       = SearchWebAction
  val shareAction           = ShareAction(shareText, titleDialog)
  val urlAction             = UrlAction(url)

}
