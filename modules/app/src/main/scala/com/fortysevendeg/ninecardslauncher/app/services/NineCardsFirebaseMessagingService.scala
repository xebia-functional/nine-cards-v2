package com.fortysevendeg.ninecardslauncher.app.services

import com.fortysevendeg.ninecardslauncher.app.services.payloads.SharedCollectionPayload
import com.google.firebase.messaging.{FirebaseMessagingService, RemoteMessage}
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class NineCardsFirebaseMessagingService extends FirebaseMessagingService {

  import jsonImplicits._
  import payloads._

  override def onMessageReceived(remoteMessage: RemoteMessage): Unit = {
    super.onMessageReceived(remoteMessage)

    def readJson[T <: Payload](json: String, f: (T) => Unit)(implicit reads: Reads[T]) = Try(Json.parse(json)) match {
      case Success(jsValue) => reads.reads(jsValue).asOpt foreach f
      case Failure(ex) => android.util.Log.e("9Cards", "Error parsing message payload")
    }


    Option(remoteMessage.getData) foreach { data =>
      (Option(data.get("payloadType")), Option(data.get("payload"))) match {
        case (Some(`sharedCollectionPayload`), Some(json)) => readJson(json, sharedCollectionNotification)
        case _ =>
      }
    }

  }

  def sharedCollectionNotification(payload: SharedCollectionPayload): Unit = {
    // TODO
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