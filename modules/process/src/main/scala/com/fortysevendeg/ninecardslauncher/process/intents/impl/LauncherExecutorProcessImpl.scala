package com.fortysevendeg.ninecardslauncher.process.intents.impl

import cats.data.EitherT
import cards.nine.commons.contexts.ActivityContextSupport
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.intents.{LauncherExecutorProcess, LauncherExecutorProcessConfig, LauncherExecutorProcessException, LauncherExecutorProcessPermissionException}
import com.fortysevendeg.ninecardslauncher.services.intents.models._
import com.fortysevendeg.ninecardslauncher.services.intents.{IntentLauncherServicesPermissionException, LauncherIntentServices}
import monix.eval.Task
import cats.syntax.either._

class LauncherExecutorProcessImpl(
  config: LauncherExecutorProcessConfig,
  launcherIntentServices: LauncherIntentServices)
  extends LauncherExecutorProcess {

  override def execute(intent: NineCardIntent)(implicit activityContext: ActivityContextSupport) = {

    def createAppAction: Option[IntentAction] = for {
      packageName <- intent.extractPackageName()
      className <- intent.extractClassName()
    } yield {
      AppAction(packageName, className)
    }

    def createLaunchAppAction: Option[IntentAction] =
      intent.extractPackageName() map AppLauncherAction

    def createGooglePlayAppAction: Option[IntentAction] =
      intent.extractPackageName() map (packageName => AppGooglePlayAction(config.googlePlayUrl, packageName))

    def createSmsAction: Option[IntentAction] =
      intent.extractPhone() map PhoneSmsAction

    def createCallAction: Option[IntentAction] =
      intent.extractPhone() map PhoneCallAction

    def createEmailAction: Option[IntentAction] =
      intent.extractEmail() map (email => EmailAction(email, config.titleEmailDialogChooser))

    intent.getAction match {
      case `openApp` =>
        tryLaunchIntentService(createAppAction)
          .recoverWith(verifyPermissionExceptionOrTry(createLaunchAppAction))
          .recoverWith(verifyPermissionExceptionOrTry(createGooglePlayAppAction))
      case `openNoInstalledApp` =>
        tryLaunchIntentService(createGooglePlayAppAction)
      case `openSms` =>
        tryLaunchIntentService(createSmsAction)
      case `openPhone` =>
        tryLaunchIntentService(createCallAction)
      case `openEmail` =>
        tryLaunchIntentService(createEmailAction)
      case `openContact` =>
        intent.extractLookup() match {
          case Some(lookupKey) => executeContact(lookupKey)
          case None => TaskService(Task(Either.left(LauncherExecutorProcessException("Contact lookup not found", None))))
        }
      case _ =>
        launcherIntentServices.launchIntent(intent).leftMap(mapServicesException)
    }
  }

  override def executeContact(lookupKey: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(ContactAction(lookupKey))

  override def launchShare(text: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(ShareAction(text, config.titleShareDialogChooser))

  override def launchSearch(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(SearchGlobalAction)
      .recoverWith(verifyPermissionExceptionOrTry(Some(SearchWebAction)))

  override def launchGoogleWeather(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(GoogleWeatherAction)

  override def launchVoiceSearch(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(SearchVoiceAction)

  override def launchSettings(packageName: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(AppSettingsAction(packageName))
      .recoverWith(verifyPermissionExceptionOrTry(Some(GlobalSettingsAction)))

  override def launchUninstall(packageName: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(AppUninstallAction(packageName))

  override def launchDial(phoneNumber: Option[String])(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(PhoneDialAction(phoneNumber))

  override def launchPlayStore(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(GooglePlayStoreAction)

  override def launchApp(packageName: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(AppLauncherAction(packageName))

  override def launchGooglePlay(packageName: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(AppGooglePlayAction(config.googlePlayUrl, packageName))

  override def launchUrl(url: String)(implicit activityContext: ActivityContextSupport) =
    toProcessServiceAction(UrlAction(url))

  private[this] def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
    case e: IntentLauncherServicesPermissionException =>
      LauncherExecutorProcessPermissionException(e.message, Some(e))
    case e =>
      LauncherExecutorProcessException(e.message, Some(e))
  }

  private[this] def toProcessServiceAction(
    action: IntentAction)(implicit activityContext: ActivityContextSupport): TaskService[Unit] =
    launcherIntentServices.launchIntentAction(action).leftMap(mapServicesException)

  private[this] def tryLaunchIntentService(
    maybeAction: Option[IntentAction])(implicit activityContext: ActivityContextSupport): TaskService[Unit] =
    maybeAction match {
      case Some(action) => toProcessServiceAction(action)
      case None => TaskService {
        Task(Either.left(LauncherExecutorProcessException(s"Not suitable intent for this NineCardIntent")))
      }
    }

  private[this] def verifyPermissionExceptionOrTry[A](f: => Option[IntentAction])
    (implicit activityContext: ActivityContextSupport): PartialFunction[NineCardException, EitherT[Task, NineCardException, Unit]] = {
    case e: LauncherExecutorProcessPermissionException => TaskService(Task(Either.left(e)))
    case _ => tryLaunchIntentService(f)
  }
}
