package cards.nine.process.intents.impl

import cards.nine.models._
import cards.nine.process.intents.LauncherExecutorProcessConfig
import cards.nine.services.intents.{IntentLauncherServicesException, IntentLauncherServicesPermissionException}

trait LauncherExecutorProcessImplData {

  val intentLauncherServicesException = IntentLauncherServicesException("Irrelevant Message")
  val intentLauncherServicesPermissionException = IntentLauncherServicesPermissionException("Irrelevant Message")

  val packageName = "package.name"
  val className = "class.Name"
  val googlePlayUrl = "http://googlePlayUrl"
  val url = "http://mockUrl"
  val lookupKey = "lookupKey"
  val email = "email@google.com"
  val phoneNumber = "666 66 66 66"
  val shareText = "Share text"
  val emailTitleDialog = "Email Title Dialog"
  val shareTitleDialog = "Share Title Dialog"

  val unknownAction = "Unknown action"

  val appAction = AppAction(packageName, className)
  val appGooglePlayAction = AppGooglePlayAction(googlePlayUrl, packageName)
  val appLauncherAction = AppLauncherAction(packageName)
  val appSettingsAction = AppSettingsAction(packageName)
  val appUninstallAction = AppUninstallAction(packageName)
  val contactAction = ContactAction(lookupKey)
  val emailAction = EmailAction(email, emailTitleDialog)
  val globalSettingsAction = GlobalSettingsAction
  val googlePlayStoreAction = GooglePlayStoreAction
  val googleWeatherAction = GoogleWeatherAction
  val phoneSmsAction = PhoneSmsAction(phoneNumber)
  val phoneCallAction = PhoneCallAction(phoneNumber)
  val phoneDialAction = PhoneDialAction(Some(phoneNumber))
  val searchGlobalAction = SearchGlobalAction
  val searchVoiceAction = SearchVoiceAction
  val searchWebAction = SearchWebAction
  val shareAction = ShareAction(shareText, shareTitleDialog)
  val urlAction = UrlAction(url)

  val config = LauncherExecutorProcessConfig(googlePlayUrl, emailTitleDialog, shareTitleDialog)

}
