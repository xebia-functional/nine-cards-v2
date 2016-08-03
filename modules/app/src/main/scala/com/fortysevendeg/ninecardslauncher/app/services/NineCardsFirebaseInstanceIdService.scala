package com.fortysevendeg.ninecardslauncher.app.services

import android.app.Service
import com.fortysevendeg.ninecardslauncher.app.commons.ContextSupportProvider
import com.fortysevendeg.ninecardslauncher.app.di.InjectorImpl
import com.fortysevendeg.ninecardslauncher.app.services.commons.FirebaseExtensions._
import com.google.firebase.iid.FirebaseInstanceIdService
import macroid.Contexts

class NineCardsFirebaseInstanceIdService
  extends FirebaseInstanceIdService
  with Contexts[Service]
  with ContextSupportProvider {

  lazy val di = new InjectorImpl

  override def onTokenRefresh(): Unit = {
    super.onTokenRefresh()
    android.util.Log.d("9Cards", "onTokenRefresh")
    readToken foreach di.userProcess.updateDeviceToken
    // TODO - Call to the backend as part of ticket 582 (https://github.com/47deg/nine-cards-v2/issues/582)
  }
}
