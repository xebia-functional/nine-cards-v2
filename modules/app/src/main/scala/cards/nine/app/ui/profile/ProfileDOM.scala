package cards.nine.app.ui.profile

import android.support.v7.app.ActionBar
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import cards.nine.app.ui.profile.adapters.SubscriptionsAdapter
import cards.nine.app.ui.profile.models.ProfileTab
import cards.nine.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}

trait ProfileDOM {

  finder: TypedFindView =>

  lazy val rootLayout = findView(TR.profile_root)

  lazy val barLayout = findView(TR.profile_appbar)

  lazy val toolbar = findView(TR.profile_toolbar)

  lazy val userContainer = findView(TR.profile_user_container)

  lazy val userAvatar = findView(TR.profile_user_avatar)

  lazy val userName = findView(TR.profile_user_name)

  lazy val userEmail = findView(TR.profile_user_email)

  lazy val tabs = findView(TR.profile_tabs)

  lazy val recyclerView = findView(TR.profile_recycler)

  lazy val loadingView = findView(TR.profile_loading)

  def actionBar: Option[ActionBar]

  def getSubscriptionsAdapter: Option[SubscriptionsAdapter] =
    recyclerView.getAdapter match {
      case a: SubscriptionsAdapter => Some(a)
      case _ => None
    }

  def getSharedCollectionsAdapter: Option[SharedCollectionsAdapter] =
    recyclerView.getAdapter match {
      case a: SharedCollectionsAdapter => Some(a)
      case _ => None
    }

}

trait ProfileListener {

  def onClickProfileTab(tab: ProfileTab): Unit

  def onClickReloadTab(tab: ProfileTab): Unit

  def onClickSynchronizeDevice(): Unit

  def onClickSubscribeCollection(sharedCollectionId: String, subscribed: Boolean): Unit

  def onClickPrintInfoDevice(cloudId: String): Unit

  def onClickOkRemoveDeviceDialog(cloudId: String): Unit

  def onClickOkRenameDeviceDialog(maybeName: Option[String], cloudId: String, actualName: String): Unit

  def onClickOkOnCopyDeviceDialog(maybeName: Option[String], cloudId: String, actualName: String): Unit

  def onBarLayoutOffsetChanged(maxScroll: Float, offset: Int): Unit

  def onClickAddSharedCollection(collection: SharedCollection): Unit

  def onClickShareSharedCollection(collection: SharedCollection): Unit

}