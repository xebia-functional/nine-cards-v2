package cards.nine.app.services

import android.app.Service
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.di.InjectorImpl
import cards.nine.app.services.commons.FirebaseExtensions._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import com.google.firebase.iid.FirebaseInstanceIdService
import macroid.Contexts

class NineCardsFirebaseInstanceIdService
  extends FirebaseInstanceIdService
  with Contexts[Service]
  with ContextSupportProvider {

  lazy val di = new InjectorImpl

  override def onTokenRefresh(): Unit = {
    super.onTokenRefresh()
    readToken foreach { token =>
      di.userProcess.updateDeviceToken(token).resolveAsync()
    }
  }
}
