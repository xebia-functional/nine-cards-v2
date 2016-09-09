package com.fortysevendeg.ninecardslauncher.app.ui.profile

import android.app.Activity
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.Tab
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TabLayoutTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.AsyncImageTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import com.fortysevendeg.ninecardslauncher.app.ui.commons.{SnailsCommons, SystemBarsTint, UiContext}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.{CharDrawable, PathMorphDrawable}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.adapters.{AccountsAdapter, SubscriptionsAdapter}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.dialog.{CopyAccountDeviceDialogFragment, RemoveAccountDeviceDialogFragment}
import com.fortysevendeg.ninecardslauncher.app.ui.profile.models.AccountSync
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

trait ProfileUiActionsImpl
  extends ProfileUiActions
  with ProfileStyles
  with TabLayout.OnTabSelectedListener {

  self: TypedFindView with SystemBarsTint with Contexts[AppCompatActivity] =>

  implicit val presenter: ProfilePresenter

  implicit val uiContext: UiContext[Activity]

  implicit lazy val theme = presenter.getTheme

  val tagDialog = "dialog"

  lazy val rootLayout = Option(findView(TR.profile_root))

  lazy val barLayout = Option(findView(TR.profile_appbar))

  lazy val toolbar = Option(findView(TR.profile_toolbar))

  lazy val userContainer = Option(findView(TR.profile_user_container))

  lazy val userAvatar = Option(findView(TR.profile_user_avatar))

  lazy val userName = Option(findView(TR.profile_user_name))

  lazy val userEmail = Option(findView(TR.profile_user_email))

  lazy val tabs = Option(findView(TR.profile_tabs))

  lazy val recyclerView = findView(TR.profile_recycler)

  lazy val loadingView = Option(findView(TR.profile_loading))

  lazy val iconIndicatorDrawable = PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  def showMessage(res: Int): Ui[_] = rootLayout <~ vSnackbarShort(res)

  override def initialize(): Ui[_] =
      (tabs <~ tlAddTabs(
        (resGetString(R.string.publications), PublicationsTab),
        (resGetString(R.string.subscriptions), SubscriptionsTab),
        (resGetString(R.string.accounts), AccountsTab))) ~
      (tabs <~ tlSetListener(this)) ~
      (recyclerView <~
        rvLayoutManager(new LinearLayoutManager(activityContextWrapper.application))) ~
      Ui(presenter.loadPublications())

  override def showLoading(): Ui[_] = (loadingView <~ vVisible) ~ (recyclerView <~ vInvisible)

  override def showAddCollectionMessage(mySharedCollectionId: String): Ui[Any] = {
    val adapter = recyclerView.getAdapter match {
      case sharedCollectionsAdapter: SharedCollectionsAdapter =>
        sharedCollectionsAdapter.copy(mySharedCollectionIds = sharedCollectionsAdapter.mySharedCollectionIds :+ mySharedCollectionId)
    }
    showMessage(R.string.collectionAdded) ~
      (recyclerView <~ rvSwapAdapter(adapter))
  }

  override def showErrorLoadingCollectionInScreen(clickAction: () => Unit): Ui[Any] = showError(R.string.errorLoadingPublishedCollections, clickAction)

  override def showEmptyMessageInScreen(clickAction: () => Unit): Ui[Any] = showError(R.string.emptyPublishedCollections, clickAction)

  override def showContactUsError(clickAction: () => Unit): Ui[Any] = showError(R.string.contactUsError, clickAction)

  override def showContactUsError(): Ui[Any] = uiShortToast(R.string.contactUsError)

  override def showConnectingGoogleError(clickAction: () => Unit): Ui[Any] = showError(R.string.errorConnectingGoogle, clickAction)

  override def showLoadingUserError(clickAction: () => Unit): Ui[Any] = showError(R.string.errorLoadingUser, clickAction)

  override def showSyncingError(): Ui[Any] = showMessage(R.string.errorSyncing) ~ (loadingView <~ vInvisible)

  override def showInvalidConfigurationNameError(resourceId: String): Ui[Any] =
    rootLayout <~ vSnackbarIndefiniteAction(
      R.string.errorEmptyNameForDevice,
      R.string.errorEmptyNameForDeviceButton,
      () => showDialogForCopyDevice(resourceId))

  override def showErrorSavingCollectionInScreen(clickAction: () => Unit): Ui[Any] = showError(R.string.errorSavingPublicCollections, clickAction)

  override def showMessageAccountSynced(): Ui[Any] = showMessage(R.string.accountSynced) ~ (loadingView <~ vInvisible)

  override def userProfile(name: String, email: String, avatarUrl: Option[String]): Ui[_] =
    (userName <~ tvText(name)) ~
    (userEmail <~ tvText(email)) ~
      (userAvatar <~
        (avatarUrl map ivUri getOrElse {
          val drawable = CharDrawable(name.substring(0, 1).toUpperCase)
          ivSrc(drawable)
        }) <~
        menuAvatarStyle)

  override def setAccountsAdapter(items: Seq[AccountSync]): Ui[Any] =
    (recyclerView <~ vVisible <~ rvAdapter(AccountsAdapter(items, accountClickListener))) ~
      (loadingView <~ vInvisible)

  override def setSubscriptionsAdapter(items: Seq[String]): Ui[Any] =
    (recyclerView <~ vVisible <~ rvAdapter(SubscriptionsAdapter(items))) ~
      (loadingView <~ vInvisible)

  override def handleToolbarVisibility(percentage: Float): Ui[Any] = toolbar match {
    case Some(t) if percentage >= 0.5 && t.getVisibility == View.VISIBLE => toolbar <~ SnailsCommons.applyFadeOut()
    case Some(t) if percentage < 0.5 && t.getVisibility == View.INVISIBLE => toolbar <~ SnailsCommons.applyFadeIn()
    case _ => Ui.nop
  }

  override def handleProfileVisibility(percentage: Float): Ui[Any] = {
    val alpha = if (percentage <= 0.5f) 1f - (percentage * 2)  else 0f
    userContainer <~ vAlpha(alpha)
  }

  override def showDialogForDeleteDevice(cloudId: String): Unit =
    showDialog(new RemoveAccountDeviceDialogFragment(cloudId))

  override def showDialogForCopyDevice(cloudId: String): Unit =
    showDialog(new CopyAccountDeviceDialogFragment(cloudId))

  override def loadPublications(
    sharedCollections: Seq[SharedCollection],
    onAddCollection: (SharedCollection) => Unit,
    onShareCollection: (SharedCollection) => Unit,
    mySharedCollectionIds: Seq[String]): Ui[Any] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, onAddCollection, onShareCollection, mySharedCollectionIds)
    (recyclerView <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (loadingView <~ vInvisible)
  }

  private[this] def showDialog(dialog: DialogFragment): Unit = {
    activityContextWrapper.original.get match {
      case Some(activity: AppCompatActivity) =>
        val ft = activity.getSupportFragmentManager.beginTransaction()
        Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
        ft.addToBackStack(javaNull)
        dialog.show(ft, tagDialog)
      case _ =>
    }
  }

  private[this] def showError(message: Int, clickAction: () => Unit): Ui[_] =
    (rootLayout <~ vSnackbarIndefiniteAction(message, R.string.buttonErrorReload, clickAction)) ~
      (loadingView <~ vInvisible)

  private[this] def accountClickListener(position: Int, itemId: Int, accountSync: AccountSync): Unit =
    itemId match {
      case R.id.action_sync => presenter.launchService()
      case R.id.action_delete => accountSync.cloudId foreach showDialogForDeleteDevice
      case R.id.action_copy => accountSync.cloudId foreach showDialogForCopyDevice
      case _ =>
    }

  override def onTabReselected(tab: Tab): Unit = {}

  override def onTabUnselected(tab: Tab): Unit = {}

  override def onTabSelected(tab: Tab): Unit = tab.getTag match {
    case PublicationsTab => presenter.loadPublications()
    case SubscriptionsTab => presenter.loadSubscriptions()
    case AccountsTab => presenter.loadUserAccounts()
    case _ =>
  }

}
