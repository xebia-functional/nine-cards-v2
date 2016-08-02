package com.fortysevendeg.ninecardslauncher.app.services

import com.google.firebase.messaging.{FirebaseMessagingService, RemoteMessage}

class NineCardsFirebaseMessagingService extends FirebaseMessagingService {

  override def onMessageReceived(remoteMessage: RemoteMessage): Unit = {
    super.onMessageReceived(remoteMessage)
    android.util.Log.d("9Cards", s"From: ${remoteMessage.getFrom}")
    if (remoteMessage.getData.size() > 0) {
      android.util.Log.d("9Cards", s"Message data payload: ${remoteMessage.getData}")
    }
    if (remoteMessage.getNotification != null) {
      android.util.Log.d("9Cards", s"Message Notification Body: ${remoteMessage.getNotification.getBody}")
    }
  }
}
