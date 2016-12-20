package cards.nine.commons.test.data

import cards.nine.models._
import cards.nine.commons.test.data.LauncherExecutorValues._

trait LauncherExecutorTestData {

  val appAction             = AppAction(launcherExecutorPackageName, launcherExecutorClassName)
  val appGooglePlayAction   = AppGooglePlayAction(googlePlayUrl, launcherExecutorPackageName)
  val appLauncherAction     = AppLauncherAction(launcherExecutorPackageName)
  val appSettingsAction     = AppSettingsAction(launcherExecutorPackageName)
  val appUninstallAction    = AppUninstallAction(launcherExecutorPackageName)
  val contactAction         = ContactAction(launcherExecutorLookupKey)
  val emailAction           = EmailAction(launcherExecutorEmail, emailTitleDialog)
  val globalSettingsAction  = GlobalSettingsAction
  val googlePlayStoreAction = GooglePlayStoreAction
  val googleWeatherAction   = GoogleWeatherAction
  val phoneSmsAction        = PhoneSmsAction(launcherExecutorPhoneNumber)
  val phoneCallAction       = PhoneCallAction(launcherExecutorPhoneNumber)
  val phoneDialAction       = PhoneDialAction(Some(launcherExecutorPhoneNumber))
  val searchGlobalAction    = SearchGlobalAction
  val searchVoiceAction     = SearchVoiceAction
  val searchWebAction       = SearchWebAction
  val shareAction           = ShareAction(shareText, shareTitleDialog)
  val urlAction             = UrlAction(launcherExecutorUrl)

  val config = LauncherExecutorProcessConfig(googlePlayUrl, emailTitleDialog, shareTitleDialog)

}
