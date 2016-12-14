package cards.nine.app.services

import android.app.{Notification, NotificationManager, PendingIntent, Service}
import android.content.{Context, Intent}
import android.support.v4.app.NotificationCompat
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.InjectorImpl
import cards.nine.app.services.payloads.SharedCollectionPayload
import cards.nine.app.services.sharedcollections.UpdateSharedCollectionService
import cards.nine.app.ui.commons.AppLog
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.models.types.PublishedByOther
import macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.R
import com.google.firebase.messaging.{FirebaseMessagingService, RemoteMessage}
import macroid.Contexts
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class NineCardsFirebaseMessagingService
  extends FirebaseMessagingService
  with Contexts[Service]
  with ContextSupportProvider {

  import jsonImplicits._
  import payloads._

  implicit lazy val di = new InjectorImpl

  lazy val builder = new NotificationCompat.Builder(this)

  lazy val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]

  override def onMessageReceived(remoteMessage: RemoteMessage): Unit = {
    super.onMessageReceived(remoteMessage)

    def readJson[T <: Payload](json: String, f: (T) => Unit)(implicit reads: Reads[T]) = Try(Json.parse(json)) match {
      case Success(jsValue) => reads.reads(jsValue).asOpt foreach f
      case Failure(ex) => AppLog.printErrorMessage(ex, Some("Error parsing message payload"))
    }


    Option(remoteMessage.getData) foreach { data =>
      (Option(data.get("payloadType")), Option(data.get("payload"))) match {
        case (Some(`sharedCollectionPayload`), Some(json)) => readJson(json, sharedCollectionNotification)(sharedCollectionPayloadReads)
        case _ =>
      }
    }

  }

  def sharedCollectionNotification(payload: SharedCollectionPayload): Unit = {
    di.collectionProcess.getCollectionBySharedCollectionId(payload.publicIdentifier).resolveAsync(
      onResult = {
        case None =>
          di.sharedCollectionsProcess.unsubscribe(payload.publicIdentifier).resolveAsync()
        case Some(col) if col.publicCollectionStatus == PublishedByOther && !col.sharedCollectionSubscribed =>
          di.sharedCollectionsProcess.unsubscribe(payload.publicIdentifier).resolveAsync()
        case Some(col) if col.publicCollectionStatus == PublishedByOther =>
          val collectionName = col.name
          val collectionId = col.id

          val title = resGetString(R.string.sharedCollectionChangedNotificationTitle)
          val msg = resGetString(R.string.sharedCollectionChangedNotificationMsg, collectionName)
          val bigMsg = resGetQuantityString(
            R.plurals.sharedCollectionChangedNotificationBigMsg,
            payload.addedPackages.size,
            collectionName)

          val unsubscribeIntent = new Intent(this, classOf[UpdateSharedCollectionService])
          unsubscribeIntent.setAction(UpdateSharedCollectionService.actionUnsubscribe)
          unsubscribeIntent.putExtra(UpdateSharedCollectionService.intentExtraCollectionId, collectionId)
          unsubscribeIntent.putExtra(UpdateSharedCollectionService.intentExtraSharedCollectionId, payload.publicIdentifier)

          val syncIntent = new Intent(this, classOf[UpdateSharedCollectionService])
          syncIntent.setAction(UpdateSharedCollectionService.actionSync)
          syncIntent.putExtra(UpdateSharedCollectionService.intentExtraCollectionId, collectionId)
          syncIntent.putExtra(UpdateSharedCollectionService.intentExtraPackages, payload.addedPackages.toArray[String])

          val notification = builder
            .setTicker(title)
            .setContentTitle(title)
            .setContentText(msg)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(bigMsg))
            .setSmallIcon(R.drawable.icon_notification_default)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(PendingIntent.getService(this, 0, syncIntent, 0))
            .addAction(
              R.drawable.icon_notification_action_unsubscribe,
              resGetString(R.string.sharedCollectionChangedNotificationUnsubscribe),
              PendingIntent.getService(this, 0, unsubscribeIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .addAction(
              R.drawable.icon_notification_action_synchronize,
              resGetString(R.string.sharedCollectionChangedNotificationSynchronize),
              PendingIntent.getService(this, 0, syncIntent, PendingIntent.FLAG_UPDATE_CURRENT))
            .build()

          notifyManager.notify(UpdateSharedCollectionService.notificationId, notification)
      },
      onException = (_) => stopSelf()
    )
  }
}

object jsonImplicits {

  implicit val sharedCollectionPayloadReads = Json.reads[SharedCollectionPayload]

}

object payloads {

  val sharedCollectionPayload = "sharedCollection"

  sealed trait Payload {
    def payloadType: String
  }

  case class SharedCollectionPayload(
    publicIdentifier: String,
    addedPackages: Seq[String]) extends Payload {

    override def payloadType: String = sharedCollectionPayload

  }

}