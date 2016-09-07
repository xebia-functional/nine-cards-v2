package com.fortysevendeg.ninecardslauncher.process.intents.impl

import cats.data.{Xor, XorT}
import com.fortysevendeg.ninecardslauncher.commons.contexts.ActivityContextSupport
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService.{NineCardException, TaskService}
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.models.NineCardsIntentExtras._
import com.fortysevendeg.ninecardslauncher.process.intents.{LauncherExecutorProcess, LauncherExecutorProcessException, LauncherExecutorProcessPermissionException}
import com.fortysevendeg.ninecardslauncher.services.intents.models._
import com.fortysevendeg.ninecardslauncher.services.intents.{IntentLauncherServicesPermissionException, LauncherIntentServices}

import scalaz.concurrent.Task

class LauncherExecutorProcessImpl(launcherIntentServices: LauncherIntentServices)
  extends LauncherExecutorProcess {

  override def execute(intent: NineCardIntent)(implicit activityContext: ActivityContextSupport) = {

    def createAppAction: Option[IntentAction] = for {
      packageName <- intent.extractPackageName()
      className <- intent.extractClassName()
    } yield {
      AppAction(packageName, className)
    }

    def createLaunchAppAction: Option[IntentAction] = intent.extractPackageName() map AppLauncherAction

    def createGooglePlayAppAction: Option[IntentAction] =
      intent.extractPackageName() map (packageName => AppGooglePlayAction("", packageName))

    def createSmsAction: Option[IntentAction] = intent.extractPhone() map PhoneSmsAction

    def createCallAction: Option[IntentAction] = intent.extractPhone() map PhoneCallAction

    def createEmailAction: Option[IntentAction] = intent.extractEmail() map (email => EmailAction(email, ""))

    def mapServicesException[E >: NineCardException]: (NineCardException => E) = {
      case e: IntentLauncherServicesPermissionException =>
        LauncherExecutorProcessPermissionException(e.message, Some(e))
      case e =>
        LauncherExecutorProcessException(e.message, Some(e))
    }

    def tryLaunchIntentService(maybeAction: Option[IntentAction]): TaskService[Unit] =
      maybeAction match {
        case Some(action) => launcherIntentServices.launchIntentAction(action).leftMap(mapServicesException)
        case None => TaskService {
          Task(Xor.left(LauncherExecutorProcessException(s"Not suitable intent for this NineCardIntent $intent")))
        }
      }

    def verifyPermissionExceptionOrTry[A](f: => Option[IntentAction]): PartialFunction[NineCardException, XorT[Task, NineCardException, Unit]] = {
      case e: LauncherExecutorProcessPermissionException => TaskService(Task(Xor.left(e)))
      case _ => tryLaunchIntentService(f)
    }

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
        intent.extraLookup() match {
          case Some(lookupKey) => executeContact(lookupKey)
          case None => TaskService(Task(Xor.left(LauncherExecutorProcessException("Contact lookup not found", None))))
        }
      case _ =>
        launcherIntentServices.launchIntent(intent).leftMap(mapServicesException)
    }
  }

  override def executeContact(contactLookupKey: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchShare(title: String, text: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchGoogleWeather(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchVoiceSearch(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchSettings(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchUninstall(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchDial(phoneNumber: Option[String])(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchPlayStore(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchApp(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???

  override def launchGooglePlay(packageName: String)(implicit activityContext: ActivityContextSupport): TaskService[Unit] = ???
}
