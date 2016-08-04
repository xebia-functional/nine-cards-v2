package com.fortysevendeg.ninecardslauncher.app.services

import com.google.firebase.messaging.{FirebaseMessagingService, RemoteMessage}

class NineCardsFirebaseMessagingService extends FirebaseMessagingService {

  override def onMessageReceived(remoteMessage: RemoteMessage): Unit = {
    super.onMessageReceived(remoteMessage)

    // TODO - 584 (https://github.com/47deg/nine-cards-v2/issues/584)
    android.util.Log.d("9Cards", s"From: ${remoteMessage.getFrom}")
    Option(remoteMessage.getData) foreach { data =>
      if (data.size() > 0) {
        android.util.Log.d("9Cards", s"Message data payload: $data")
      }
    }
    Option(remoteMessage.getNotification) foreach { notification =>
      android.util.Log.d("9Cards", s"Message Notification Body: ${notification.getBody}")
    }
  }
}
