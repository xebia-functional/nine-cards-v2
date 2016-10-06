package cards.nine.models

sealed trait IntentAction

case class AppAction(packageName: String, className: String) extends IntentAction

case class AppGooglePlayAction(googlePlayUrl: String, packageName: String) extends IntentAction

case class AppLauncherAction(packageName: String) extends IntentAction

case class AppSettingsAction(packageName: String) extends IntentAction

case class AppUninstallAction(packageName: String) extends IntentAction

case class ContactAction(lookupKey: String) extends IntentAction

case class EmailAction(email: String, titleDialog: String) extends IntentAction

case object GlobalSettingsAction extends IntentAction

case object GooglePlayStoreAction extends IntentAction

case object GoogleWeatherAction extends IntentAction

case class PhoneCallAction(phoneNumber: String) extends IntentAction

case class PhoneDialAction(maybePhoneNumber: Option[String]) extends IntentAction

case class PhoneSmsAction(phoneNumber: String) extends IntentAction

case object SearchGlobalAction extends IntentAction

case object SearchVoiceAction extends IntentAction

case object SearchWebAction extends IntentAction

case class ShareAction(text: String, titleDialog: String) extends IntentAction

case class UrlAction(url: String) extends IntentAction
