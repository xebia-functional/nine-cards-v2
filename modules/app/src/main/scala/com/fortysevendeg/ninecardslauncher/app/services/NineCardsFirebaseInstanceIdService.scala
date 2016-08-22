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
    readToken foreach di.userProcess.updateDeviceToken
  }
}
