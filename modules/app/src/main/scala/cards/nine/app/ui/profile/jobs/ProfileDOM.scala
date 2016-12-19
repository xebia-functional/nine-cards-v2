package cards.nine.app.ui.profile.jobs

import android.app.Activity
import cards.nine.app.ui.commons.ActivityFindViews
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import cards.nine.app.ui.profile.adapters.SubscriptionsAdapter
import cards.nine.app.ui.profile.models.ProfileTab
import cards.nine.models.SharedCollection
import com.fortysevendeg.ninecardslauncher.TR

class ProfileDOM(activity: Activity) {

  import ActivityFindViews._

  lazy val rootLayout = findView(TR.profile_root).run(activity)

  lazy val barLayout = findView(TR.profile_appbar).run(activity)

  lazy val toolbar = findView(TR.profile_toolbar).run(activity)

  lazy val userContainer = findView(TR.profile_user_container).run(activity)

  lazy val userAvatar = findView(TR.profile_user_avatar).run(activity)

  lazy val userName = findView(TR.profile_user_name).run(activity)

  lazy val userEmail = findView(TR.profile_user_email).run(activity)

  lazy val tabs = findView(TR.profile_tabs).run(activity)

  lazy val recyclerView = findView(TR.profile_recycler).run(activity)

  lazy val loadingView = findView(TR.profile_loading).run(activity)

  def getSubscriptionsAdapter: Option[SubscriptionsAdapter] =
    recyclerView.getAdapter match {
      case a: SubscriptionsAdapter => Some(a)
      case _                       => None
    }

  def getSharedCollectionsAdapter: Option[SharedCollectionsAdapter] =
    recyclerView.getAdapter match {
      case a: SharedCollectionsAdapter => Some(a)
      case _                           => None
    }

}

trait ProfileListener {

  def onClickProfileTab(tab: ProfileTab): Unit

  def onClickReloadTab(tab: ProfileTab): Unit

  def onClickSynchronizeDevice(): Unit

  def onClickSubscribeCollection(sharedCollectionId: String, subscribed: Boolean): Unit

  def onClickCopyDevice(cloudId: String, actualName: String): Unit

  def onClickRenameDevice(cloudId: String, actualName: String): Unit

  def onClickDeleteDevice(cloudId: String): Unit

  def onClickPrintInfoDevice(cloudId: String): Unit

  def onClickOkRemoveDeviceDialog(cloudId: String): Unit

  def onClickOkRenameDeviceDialog(
      maybeName: Option[String],
      cloudId: String,
      actualName: String): Unit

  def onClickOkOnCopyDeviceDialog(
      maybeName: Option[String],
      cloudId: String,
      actualName: String): Unit

  def onBarLayoutOffsetChanged(maxScroll: Float, offset: Int): Unit

  def onClickAddSharedCollection(collection: SharedCollection): Unit

  def onClickShareSharedCollection(collection: SharedCollection): Unit

}
