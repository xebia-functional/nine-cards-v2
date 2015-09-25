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
import macroid.Contexts

import com.fortysevendeg.ninecardslauncher.app.ui.commons.TasksOps._

import scalaz.concurrent.Task

class CreateCollectionService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with CreateCollectionsTasks
  with BroadcastDispatcher {

  val tag = "9cards"

  implicit lazy val di = new Injector

  private var loadDeviceId: Option[String] = None

  override val actionsFilters: Seq[String] = Seq(testFilter, testQuestionFilter, testAnswerFilter)

  override def manageCommand(action: String, data: Option[String]): Unit = {}

  override def manageQuestion(action: String): Option[BroadAction] = action match {
    case `testQuestionFilter` => Option(BroadAction(testAnswerFilter, Option("vamos!!!")))
    case _ => None
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {
    loadDeviceId = Option(intent) flatMap {
      i => if (i.hasExtra(keyDevice)) Some(i.getStringExtra(keyDevice)) else None
    }

    val builder = new NotificationCompat.Builder(this)
    val notificationIntent: Intent = new Intent(this, classOf[WizardActivity])
    val title: String = getString(com.fortysevendeg.ninecardslauncher2.R.string.workingNotificationTitle)
    builder.
      setContentTitle(title).
      setTicker(title).
      setContentText(getString(com.fortysevendeg.ninecardslauncher2.R.string.loadingYourAppsMessage)).
      setSmallIcon(com.fortysevendeg.ninecardslauncher2.R.drawable.icon_notification_working).
      setProgress(0, 0, true).
      setContentIntent(PendingIntent.getActivity(this, getUniqueId, notificationIntent, 0))

    registerDispatchers

    startForeground(notificationId, builder.build)

    val service = loadDeviceId map loadConfiguration getOrElse createNewConfiguration

    Task.fork(service.run).resolveAsync(
      onResult = collections => closeService(),
      onException = ex => closeService() // TODO We should show the error in UI
    )
    super.onStartCommand(intent, flags, startId)
  }


  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  private def closeService() = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = null

}


object CreateCollectionService {
  val keyDevice: String = "__key_device__"
  val notificationId: Int = 1101
  val homeMorningKey = "home"
}