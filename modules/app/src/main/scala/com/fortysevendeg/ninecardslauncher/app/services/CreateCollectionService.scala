package com.fortysevendeg.ninecardslauncher.app.services

import android.app.{NotificationManager, PendingIntent, Service}
import android.content.{Context, Intent}
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.commons.{BroadAction, BroadcastDispatcher, ContextSupportProvider}
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.services.CreateCollectionService._
import com.fortysevendeg.ninecardslauncher.app.services.commons.FirebaseExtensions._
import com.fortysevendeg.ninecardslauncher.app.services.commons.GoogleDriveApiClientService
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppLog._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AppUtils._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.SyncDeviceState.{stateFailure => _, stateSuccess => _}
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.WizardState._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.action_filters._
import com.fortysevendeg.ninecardslauncher.app.ui.wizard.WizardActivity
import cards.nine.commons.javaNull
import com.fortysevendeg.ninecardslauncher.process.device.ImplicitsDeviceException
import com.fortysevendeg.ninecardslauncher.process.user.models.User
import com.fortysevendeg.ninecardslauncher2.R
import com.google.android.gms.common.api.GoogleApiClient
import macroid.Contexts


class CreateCollectionService
  extends Service
  with Contexts[Service]
  with ContextSupportProvider
  with CreateCollectionsTasks
  with GoogleDriveApiClientService
  with ImplicitsDeviceException
  with BroadcastDispatcher { self =>

  val maxProgress = 4

  implicit lazy val di = new InjectorImpl

  lazy val builder = new NotificationCompat.Builder(this)

  lazy val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  private var selectedCloudId: Option[String] = None

  private var currentState: Option[String] = None

  override val actionsFilters: Seq[String] = WizardActionFilter.cases map (_.action)

  override def manageQuestion(action: String): Option[BroadAction] = WizardActionFilter(action) match {
    case WizardAskActionFilter => Option(BroadAction(WizardAnswerActionFilter.action, currentState))
    case _ => None
  }

  override def onStartCommand(intent: Intent, flags: Int, startId: Int): Int = {

    def startProcess() = {
      selectedCloudId = Option(intent) flatMap { i =>
        if (i.hasExtra(cloudIdKey)) {
          val key = i.getStringExtra(cloudIdKey)
          if (key == newConfiguration) None else Some(key)
        } else None
      }

      setState(stateCreatingCollections)

      val notificationIntent: Intent = new Intent(this, classOf[WizardActivity])
      val title: String = getString(R.string.workingNotificationTitle)
      builder.
        setContentTitle(title).
        setTicker(title).
        setContentText(getString(R.string.downloadingAppsInfoMessage)).
        setSmallIcon(R.drawable.icon_notification_working).
        setProgress(1, maxProgress, true).
        setContentIntent(PendingIntent.getActivity(this, getUniqueId, notificationIntent, 0))

      startForeground(notificationId, builder.build)

      synchronizeDevice
    }

    registerDispatchers

    val hasKey = Option(intent) exists (_.hasExtra(cloudIdKey))

    di.userProcess.getUser.resolveAsync2(
      onResult = (user: User) => {
        (hasKey, user.deviceCloudId.isEmpty) match {
          case (true, true) => startProcess()
          case (false, _ ) => setState(stateCloudIdNotSend, close = true)
          case (_, false) => setState(stateUserCloudIdPresent, close = true)
        }
      },
      onException = (_) => closeService()
    )

    Service.START_NOT_STICKY
  }

  private[this] def setState(state: String, close: Boolean = false) = {
    currentState = Option(state)
    self ! BroadAction(WizardStateActionFilter.action, Option(state))
    if (close) closeService()
  }

  protected def setProcess(process: CreateCollectionsProcess) = {
    getTextByProcess(process) foreach builder.setContentText
    builder.setProgress(maxProgress, process.progress, false)
    notifyManager.notify(notificationId, builder.build())
  }

  private[this] def getTextByProcess(process: CreateCollectionsProcess): Option[String] = process match {
    case GettingAppsProcess => Option(resGetString(R.string.loadingAppsInfoMessage))
    case LoadingConfigProcess => Option(resGetString(R.string.loadingUserConfigMessage))
    case CreatingCollectionsProcess => Option(selectedCloudId map (_ =>
      resGetString(R.string.loadingFromDeviceMessage)) getOrElse resGetString(R.string.loadingForMyDeviceMessage))
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
    unregisterDispatcher
  }

  private[this] def closeService() = {
    stopForeground(true)
    stopSelf()
  }

  override def onBind(intent: Intent): IBinder = javaNull

  override def connected(client: GoogleApiClient): Unit = {

    val service = selectedCloudId match {
      case Some(cloudId) => loadConfiguration(client, readToken, cloudId)
      case _ => createNewConfiguration(client, readToken)
    }

    service.resolveAsync2(
      onResult = collections => {
        setState(stateSuccess, close = true)
      },
      onException = ex => {
        setState(stateFailure, close = true)
      }
    )
  }

  override def error(message: String, maybeException: Option[Throwable]): Unit = {
    maybeException foreach (ex => printErrorMessage(ex))
    setState(stateFailure, close = true)
  }
}

object CreateCollectionService {
  val cloudIdKey: String = "__key_device__"
  val newConfiguration: String = "new-configuration"
  val notificationId: Int = 1101
  val homeMorningKey = "home"
}

sealed trait CreateCollectionsProcess {
  val progress: Int
}

case object GettingAppsProcess
  extends CreateCollectionsProcess {
  override val progress: Int = 2
}

case object LoadingConfigProcess
  extends CreateCollectionsProcess {
  override val progress: Int = 3
}

case object CreatingCollectionsProcess
  extends CreateCollectionsProcess {
  override val progress: Int = 4
}