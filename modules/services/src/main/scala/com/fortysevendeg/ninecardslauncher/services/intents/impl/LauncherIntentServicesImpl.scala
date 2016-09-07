package com.fortysevendeg.ninecardslauncher.services.intents.impl

import android.app.Activity
import android.content.Intent
import cats.data.Xor
import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{NineCardException, TaskService}
import com.fortysevendeg.ninecardslauncher.services.intents.models._
import com.fortysevendeg.ninecardslauncher.services.intents.{ImplicitsIntentLauncherServicesExceptions, IntentLauncherServicesException, IntentLauncherServicesPermissionException, LauncherIntentServices}

import scalaz.concurrent.Task

class LauncherIntentServicesImpl
  extends LauncherIntentServices
  with ImplicitsIntentLauncherServicesExceptions {

  val intentCreator = new IntentCreator

  override def launchIntent(intentAction: IntentAction)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = {

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
    }
    tryLaunchIntentService(intent)
  }

  def withActivity(f: (Activity) => Xor[NineCardException, Unit])
    (implicit activityContext: ActivityContextSupport): Xor[NineCardException, Unit] =
    activityContext.getActivity match {
      case Some(activity) => f(activity)
      case None => Xor.left(IntentLauncherServicesException("Activity not available", None))
    }

  def tryLaunchIntentService(intent: Intent)(implicit activityContext: ActivityContextSupport): TaskService[Unit] =
    TaskService {
      Task {
        withActivity { activity =>
          Xor.catchNonFatal {
            activity.startActivity(intent)
          } leftMap {
            case e: SecurityException => IntentLauncherServicesPermissionException(e.getMessage, Some(e))
            case e => IntentLauncherServicesException(e.getMessage, Some(e))
          }
        }
      }
    }
}
