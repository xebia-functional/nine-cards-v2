package com.fortysevendeg.ninecardslauncher.app.services

import android.app.{PendingIntent, Service}
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.Injector
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ActionFilters._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.Contexts
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._

import scalaz.concurrent.Task

class CreateCollectionService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with CreateCollectionsTasks
  with BroadcastDispatcher { self =>

  val maxProgress = 4

  implicit lazy val di = new Injector

  lazy val builder = new NotificationCompat.Builder(this)

  private var loadDeviceId: Option[String] = None

  private var currentState: Option[String] = None

  override val actionsFilters: Seq[String] = Seq(
    wizardStateActionFilter,
    wizardAskStateActionFilter,
    wizardAnswerStateActionFilter)

  override def manageCommand(action: String, data: Option[String]): Unit = {}

  override def manageQuestion(action: String): Option[BroadAction] = action match {
    case `wizardAskStateActionFilter` => Option(BroadAction(wizardAnswerStateActionFilter, currentState))
    case _ => None
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    loadDeviceId = Option(intent) flatMap {
      i => if (i.hasExtra(keyDevice)) Some(i.getStringExtra(keyDevice)) else None
    }

    setState(stateCreatingCollections)

    val notificationIntent: Intent = new Intent(this, classOf[WizardActivity])
    val title: String = getString(com.fortysevendeg.ninecardslauncher2.R.string.workingNotificationTitle)
    builder.
      setContentTitle(title).
      setTicker(title).
      setContentText(getString(com.fortysevendeg.ninecardslauncher2.R.string.downloadingAppsInfoMessage)).
      setSmallIcon(com.fortysevendeg.ninecardslauncher2.R.drawable.icon_notification_working).
      setProgress(1, maxProgress, true).
      setContentIntent(PendingIntent.getActivity(this, getUniqueId, notificationIntent, 0))

    registerDispatchers

    startForeground(notificationId, builder.build)

    val service = loadDeviceId map loadConfiguration getOrElse createNewConfiguration

    Task.fork(service.run).resolveAsync(
      onResult = collections => {
        setState(stateSuccess)
        closeService()
      },
      onException = ex => {
        setState(stateFaliure)
        closeService()
      }
    )
    super.onStartCommand(intent, flags, startId)
  }

  private[this] def setState(state: String) = {
    currentState = Option(state)
    self ! BroadAction(wizardStateActionFilter, Option(state))
  }

  protected def setProcess(process: String) = {
    getTextByProcess(process) foreach builder.setContentText
    val progress = getProgressByProcess(process)
    builder.setProgress(maxProgress, progress, false)
  }

  private[this] def getTextByProcess(process: String): Option[String] = process match {
    case `processGettingApps` => Option(resGetString(R.string.loadingAppsInfoMessage))
    case `processLoadingConfig` => Option(resGetString(R.string.loadingUserConfigMessage))
    case `processCreatingCollections` => Option(loadDeviceId map (_ =>
      resGetString(R.string.loadingFromDeviceMessage)) getOrElse resGetString(R.string.loadingForMyDeviceMessage))
    case _ => None
  }

  private[this] def getProgressByProcess(process: String): Int = process match {
    case `processGettingApps` => 2
    case `processLoadingConfig` => 3
    case `processCreatingCollections` => 4
    case _ => 0
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  private[this] def closeService() = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = null

}

object CreateCollectionService {
  val keyDevice: String = "__key_device__"
  val notificationId: Int = 1101
  val homeMorningKey = "home"

  val processGettingApps = "wizard-process-getting-apps"
  val processLoadingConfig = "wizard-process-loading-config"
  val processCreatingCollections = "wizard-process-creating-collections"

}