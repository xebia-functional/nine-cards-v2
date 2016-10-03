package cards.nine.app.services.collections

import android.app.{Notification, NotificationManager, PendingIntent, Service}
import android.content.{Context, Intent}
import android.support.v4.app.NotificationCompat
import cards.nine.app.ui.commons.AppUtils._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.wizard.WizardActivity
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

trait CreateCollectionsUiActions {

  self: CreateCollectionsListener with Contexts[Service] =>

  lazy val notifyManager = serviceContextWrapper.bestAvailable.getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  lazy val builder = new NotificationCompat.Builder(serviceContextWrapper.bestAvailable)

  val notificationId: Int = 1101

  val maxProgress = 4

  def initialize(): TaskService[Unit] = Ui {
    val context = serviceContextWrapper.bestAvailable
    val notificationIntent: Intent = new Intent(context, classOf[WizardActivity])
    val title: String = context.getString(R.string.workingNotificationTitle)
    builder.
      setContentTitle(title).
      setTicker(title).
      setContentText(context.getString(R.string.downloadingAppsInfoMessage)).
      setSmallIcon(R.drawable.icon_notification_working).
      setProgress(1, maxProgress, true).
      setContentIntent(PendingIntent.getActivity(context, getUniqueId, notificationIntent, 0))
    processStarted(notificationId, builder.build())
  }.toService

  def setProcess(selectedCloudId: Option[String], process: CreateCollectionsProcess): TaskService[Unit] = {

    def getTextByProcess: Option[String] = process match {
      case GettingAppsProcess => Option(resGetString(R.string.loadingAppsInfoMessage))
      case LoadingConfigProcess => Option(resGetString(R.string.loadingUserConfigMessage))
      case CreatingCollectionsProcess => Option(selectedCloudId map (_ =>
        resGetString(R.string.loadingFromDeviceMessage)) getOrElse resGetString(R.string.loadingForMyDeviceMessage))
    }

    Ui {
      getTextByProcess foreach builder.setContentText
      builder.setProgress(maxProgress, process.progress, false)
      notifyManager.notify(notificationId, builder.build())
    }.toService
  }

  def endProcess: TaskService[Unit] = Ui(processFinished()).toService

}

trait CreateCollectionsListener {

  def processStarted(notificationId: Int, notification: Notification): Unit

  def processFinished(): Unit

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