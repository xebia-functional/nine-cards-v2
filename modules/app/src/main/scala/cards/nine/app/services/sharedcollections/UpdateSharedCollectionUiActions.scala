package cards.nine.app.services.sharedcollections

import android.app.{NotificationManager, Service}
import android.content.Context
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons.{ImplicitsUiExceptions, UiException}
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.commons.CatchAll
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.R
import macroid.Contexts

trait UpdateSharedCollectionUiActions
  extends ImplicitsUiExceptions {

  self: Contexts[Service] =>

  lazy val notifyManager = serviceContextWrapper.bestAvailable
    .getSystemService(Context.NOTIFICATION_SERVICE)
    .asInstanceOf[NotificationManager]

  def cancelNotification(): TaskService[Unit] = TaskService {
    CatchAll[UiException](notifyManager.cancel(UpdateSharedCollectionService.notificationId))
  }

  def showUnsubscribedMessage: TaskService[Unit] = uiShortToast2(R.string.sharedCollectionUnsubscribed).toService

  def showCollectionUpdatedMessage: TaskService[Unit] = uiShortToast2(R.string.sharedCollectionUpdated).toService

}
