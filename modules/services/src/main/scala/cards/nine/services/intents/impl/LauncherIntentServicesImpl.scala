package cards.nine.services.intents.impl

import android.app.Activity
import android.content.Intent
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService.{NineCardException, TaskService}
import cards.nine.services.intents.models._
import cards.nine.services.intents.{IntentLauncherServicesException, IntentLauncherServicesPermissionException, LauncherIntentServices}
import cats.syntax.either._
import monix.eval.Task

class LauncherIntentServicesImpl
  extends LauncherIntentServices {

  val intentCreator = new IntentCreator

  override def launchIntentAction(intentAction: IntentAction)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = {

    import intentCreator._
    val intent = intentAction match {
      case AppAction(packageName, className) =>
        createAppIntent(packageName, className)
      case AppGooglePlayAction(googlePlayUrl, packageName) =>
        createAppGooglePlayIntent(googlePlayUrl, packageName)
      case AppLauncherAction(packageName) =>
        createAppLaunchIntent(packageName)
      case AppSettingsAction(packageName) =>
        createAppSettingsIntent(packageName)
      case AppUninstallAction(packageName) =>
        createAppUninstallIntent(packageName)
      case ContactAction(lookupKey) =>
        createContactIntent(lookupKey)
      case EmailAction(email, titleDialog) =>
        createEmailIntent(email, titleDialog)
      case GlobalSettingsAction =>
        createGlobalSettingsIntent()
      case GooglePlayStoreAction =>
        createGooglePlayStoreIntent()
      case GoogleWeatherAction =>
        createGoogleWeatherIntent()
      case PhoneCallAction(phoneNumber) =>
        createPhoneCallIntent(phoneNumber)
      case PhoneDialAction(maybePhoneNumber) =>
        createPhoneDialIntent(maybePhoneNumber)
      case PhoneSmsAction(phoneNumber) =>
        createPhoneSmsIntent(phoneNumber)
      case SearchGlobalAction =>
        createSearchGlobalIntent()
      case SearchVoiceAction =>
        createSearchVoiceIntent()
      case SearchWebAction =>
        createSearchWebIntent()
      case ShareAction(text, titleDialog) =>
        createShareIntent(text, titleDialog)
      case UrlAction(url) =>
        createUrlViewIntent(url)
    }
    launchIntent(intent)
  }

  override def launchIntent(intent: Intent)(implicit activityContext: ActivityContextSupport): TaskService[Unit] =
    TaskService {
      Task {
        withActivity { activity =>
          Either.catchNonFatal {
            activity.startActivity(intent)
          } leftMap {
            case e: SecurityException => IntentLauncherServicesPermissionException(e.getMessage, Some(e))
            case e => IntentLauncherServicesException(e.getMessage, Some(e))
          }
        }
      }
    }

  def withActivity(f: (Activity) => Either[NineCardException, Unit])
    (implicit activityContext: ActivityContextSupport): Either[NineCardException, Unit] =
    activityContext.getActivity match {
      case Some(activity) => f(activity)
      case None => Left(IntentLauncherServicesException("Activity not available", None))
    }
}
