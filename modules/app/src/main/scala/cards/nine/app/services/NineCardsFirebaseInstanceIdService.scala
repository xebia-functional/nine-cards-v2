package cards.nine.app.services

import android.app.Service
import cards.nine.app.commons.ContextSupportProvider
import com.google.firebase.iid.FirebaseInstanceIdService
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.Contexts

class NineCardsFirebaseInstanceIdService
    extends FirebaseInstanceIdService
    with Contexts[Service]
    with ContextSupportProvider {

  lazy val jobs = new NineCardsFirebaseJobs

  override def onTokenRefresh(): Unit = {
    super.onTokenRefresh()
    jobs.updateDeviceToken().resolveAsync()
  }
}
