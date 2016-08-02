package com.fortysevendeg.ninecardslauncher.app.services

import com.google.firebase.iid.{FirebaseInstanceId, FirebaseInstanceIdService}

class NineCardsFirebaseInstanceIdService extends FirebaseInstanceIdService {

  override def onTokenRefresh(): Unit = {
    super.onTokenRefresh()

    val token = FirebaseInstanceId.getInstance().getToken()
    android.util.Log.i("9Cards", s"----> $token")
  }
}
