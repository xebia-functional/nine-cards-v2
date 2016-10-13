package cards.nine.app.services.sharedcollections

 import android.app.{IntentService, Service}
import android.content.Intent
import cards.nine.app.commons.ContextSupportProvider
import cards.nine.app.ui.commons.AppLog._
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import macroid.Contexts

class UpdateSharedCollectionService
  extends IntentService("updateSharedCollectionService")
  with Contexts[Service]
  with ContextSupportProvider
  with UpdateSharedCollectionUiActions {

  lazy val jobs = new UpdateSharedCollectionJobs(this)

  override def onHandleIntent(intent: Intent): Unit = {

    jobs.handleIntent(intent).resolveAsync(onException = e => printErrorMessage(e))

  }
}

object UpdateSharedCollectionService {

  val intentExtraCollectionId = "_collectionId_"

  val intentExtraSharedCollectionId = "_sharedCollectionId_"

  val intentExtraPackages = "_addedPackages_"

  val actionUnsubscribe = "unsubscribeCollection"

  val actionSync = "syncCollection"

  val notificationId: Int = 2101

}