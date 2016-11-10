package cards.nine.app.ui.profile.jobs

import android.support.design.widget.TabLayout.Tab
import android.support.design.widget.{AppBarLayout, TabLayout}
import android.support.v4.app.{DialogFragment, Fragment, FragmentManager}
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import cards.nine.app.ui.commons.AsyncImageTweaks._
import cards.nine.app.ui.commons.ExtraTweaks._
import cards.nine.app.ui.commons._
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionsAdapter
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.app.ui.components.drawables.{CharDrawable, PathMorphDrawable}
import cards.nine.app.ui.profile.adapters.AccountOptions._
import cards.nine.app.ui.profile.adapters.{AccountsAdapter, EmptyProfileAdapter, SubscriptionsAdapter}
import cards.nine.app.ui.profile.dialog.{EditAccountDeviceDialogFragment, RemoveAccountDeviceDialogFragment}
import cards.nine.app.ui.profile.models._
import cards.nine.commons._
import cards.nine.commons.services.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.PublishedByOther
import cards.nine.models.types.theme.{CardLayoutBackgroundColor, PrimaryColor}
import cards.nine.models.{NineCardsTheme, SharedCollection, Subscription}
import com.fortysevendeg.macroid.extras.DeviceVersion.Lollipop
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ProgressBarTweaks._
import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.macroid.extras.TabLayoutTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

class ProfileUiActions(dom: ProfileDOM, listener: ProfileListener)
  (implicit
    activityContextWrapper: ActivityContextWrapper,
    fragmentManagerContext: FragmentManagerContext[Fragment, FragmentManager],
    uiContext: UiContext[_])
  extends TabLayout.OnTabSelectedListener
  with AppBarLayout.OnOffsetChangedListener
  with ImplicitsUiExceptions {

  val tagDialog = "dialog"

  lazy val systemBarsTint = new SystemBarsTint

  lazy val iconIndicatorDrawable = PathMorphDrawable(
    defaultStroke = resGetDimensionPixelSize(R.dimen.stroke_default),
    padding = resGetDimensionPixelSize(R.dimen.padding_icon_home_indicator))

  implicit var theme = AppUtils.getDefaultTheme

  def initialize(nineCardsTheme: NineCardsTheme): TaskService[Unit] = {

    def initActionBar = Ui {
      activityContextWrapper.original.get match {
        case Some(activity: AppCompatActivity) =>
          activity.setSupportActionBar(dom.toolbar)
          activity.getSupportActionBar.setDisplayHomeAsUpEnabled(true)
          activity.getSupportActionBar.setHomeAsUpIndicator(iconIndicatorDrawable)
        case _ =>
      }
    }

    (Ui(theme = nineCardsTheme) ~
      (dom.rootLayout <~ vBackgroundColor(theme.get(CardLayoutBackgroundColor))) ~
      (dom.userContainer <~ vBackgroundColor(theme.get(PrimaryColor))) ~
      (dom.barLayout <~ vBackgroundColor(theme.get(PrimaryColor))) ~
      (dom.loadingView <~ pbColor(theme.get(PrimaryColor))) ~
      (dom.tabs <~ tlAddTabs(
        (resGetString(R.string.accounts), AccountsTab),
        (resGetString(R.string.publications), PublicationsTab),
        (resGetString(R.string.subscriptions), SubscriptionsTab))) ~
      (dom.tabs <~ tlSetListener(this)) ~
      (dom.recyclerView <~
        rvLayoutManager(new LinearLayoutManager(activityContextWrapper.application))) ~
      systemBarsTint.updateStatusColor(theme.get(PrimaryColor)) ~
      initActionBar ~
      Ui(dom.barLayout.addOnOffsetChangedListener(this))).toService
  }

  def showLoading(): TaskService[Unit] = ((dom.loadingView <~ vVisible) ~ (dom.recyclerView <~ vInvisible)).toService

  def showAddCollectionMessage(sharedCollectionId: String): TaskService[Unit] = {

    def prepareAdapter: TaskService[SharedCollectionsAdapter] =
      dom.getSharedCollectionsAdapter match {
        case Some(adapter) =>
          val newCollections = adapter.sharedCollections map {
            case col if col.sharedCollectionId == sharedCollectionId => col.copy(publicCollectionStatus = PublishedByOther)
            case col => col
          }
          TaskService.right(adapter.copy(sharedCollections = newCollections))
        case _ => TaskService.left(UiException("Can't find SharedCollectionsAdapter"))
      }

    for {
      _ <- showMessage(R.string.collectionAdded).toService
      adapter <- prepareAdapter
      _ <- (dom.recyclerView <~ rvSwapAdapter(adapter)).toService
    } yield ()
  }

  // TODO Remove when we've got different states for the switch - issue #783
  def refreshCurrentSubscriptions(): TaskService[Unit] =
    (dom.recyclerView <~ rvSwapAdapter(dom.recyclerView.getAdapter)).toService

  def showUpdatedSubscriptions(sharedCollectionId: String, subscribed: Boolean): TaskService[Unit] = {

    def prepareAdapter: TaskService[SubscriptionsAdapter] =
        dom.getSubscriptionsAdapter match {
          case Some(adapter) =>
            val subscriptions = adapter.subscriptions map {
              case subscription if subscription.sharedCollectionId == sharedCollectionId => subscription.copy(subscribed = subscribed)
              case subscription => subscription
            }
            TaskService.right(adapter.copy(subscriptions = subscriptions))
          case _ => TaskService.left(UiException("Can't find SubscriptionsAdapter"))
        }

    for {
      adapter <- prepareAdapter
      _ <- (dom.recyclerView <~ rvSwapAdapter(adapter)).toService
    } yield ()

  }

  def showErrorSubscribing(triedToSubscribe: Boolean): TaskService[Unit] =
    showMessage(if (triedToSubscribe) R.string.errorSubscribing else R.string.errorUnsubscribing).toService

  def showContactUsError(): TaskService[Unit] =
    uiShortToast2(R.string.contactUsError).toService

  def showSyncingError(): TaskService[Unit] =
    (showMessage(R.string.errorSyncing) ~ (dom.loadingView <~ vInvisible)).toService

  def showInvalidConfigurationNameError(): TaskService[Unit] =
    (dom.rootLayout <~ vSnackbarShort(res = R.string.errorEmptyNameForDevice)).toService

  def showErrorSavingCollectionInScreen(): TaskService[Unit] =
    showError(R.string.errorSavingPublicCollections, () => listener.onClickProfileTab(PublicationsTab)).toService

  def showMessageAccountSynced(): TaskService[Unit] =
    (showMessage(R.string.accountSynced) ~ (dom.loadingView <~ vInvisible)).toService

  def userProfile(maybeName: Option[String], email: String, avatarUrl: Option[String]): TaskService[Unit] = {
    val name = maybeName.getOrElse(email)
    ((dom.userName <~ tvText(name)) ~
      (dom.userEmail <~ tvText(email)) ~
      (dom.userAvatar <~
        (avatarUrl map ivUri getOrElse {
          val drawable = CharDrawable(name.substring(0, 1).toUpperCase)
          ivSrc(drawable)
        }) <~
        menuAvatarStyle)).toService
  }

  def setAccountsAdapter(items: Seq[AccountSync]): TaskService[Unit] =
    ((dom.recyclerView <~ vVisible <~ rvAdapter(AccountsAdapter(items, accountClickListener))) ~
      (dom.loadingView <~ vInvisible)).toService

  def setSubscriptionsAdapter(items: Seq[Subscription]): TaskService[Unit] =
    ((dom.recyclerView <~ vVisible <~ rvAdapter(SubscriptionsAdapter(items, listener.onClickSubscribeCollection))) ~
      (dom.loadingView <~ vInvisible)).toService

  def handleToolbarVisibility(percentage: Float): TaskService[Unit] =
    (dom.toolbar match {
      case t if percentage >= 0.5 && t.getVisibility == View.VISIBLE => dom.toolbar <~ SnailsCommons.applyFadeOut()
      case t if percentage < 0.5 && t.getVisibility == View.INVISIBLE => dom.toolbar <~ SnailsCommons.applyFadeIn()
      case _ => Ui.nop
    }).toService

  def handleProfileVisibility(percentage: Float): TaskService[Unit] = {
    val alpha = if (percentage <= 0.5f) 1f - (percentage * 2)  else 0f
    (dom.userContainer <~ vAlpha(alpha)).toService
  }

  def showDialogForDeleteDevice(cloudId: String): TaskService[Unit] =
    showDialog(new RemoveAccountDeviceDialogFragment(cloudId, listener.onClickOkRemoveDeviceDialog))

  def showDialogForCopyDevice(cloudId: String, actualName: String): TaskService[Unit] =
    showDialog(new EditAccountDeviceDialogFragment(
      title = R.string.copyAccountSyncDialogTitle,
      maybeText = None,
      action = listener.onClickOkOnCopyDeviceDialog(_, cloudId, actualName)))

  def showDialogForRenameDevice(cloudId: String, actualName: String): TaskService[Unit] =
    showDialog(new EditAccountDeviceDialogFragment(
      title = R.string.renameAccountSyncDialogTitle,
      maybeText = Some(actualName),
      action = listener.onClickOkRenameDeviceDialog(_, cloudId, actualName)))

  def loadPublications(sharedCollections: Seq[SharedCollection]): TaskService[Unit] = {
    val adapter = SharedCollectionsAdapter(sharedCollections, listener.onClickAddSharedCollection, listener.onClickShareSharedCollection)
    ((dom.recyclerView <~
      vVisible <~
      rvLayoutManager(adapter.getLayoutManager) <~
      rvAdapter(adapter)) ~
      (dom.loadingView <~ vInvisible)).toService
  }

  def showEmptyPublicationsContent(error: Boolean): TaskService[Unit] =
    showEmptyContent(PublicationsTab, error, () => listener.onClickReloadTab(PublicationsTab)).toService

  def showEmptySubscriptionsContent(error: Boolean): TaskService[Unit] =
    showEmptyContent(SubscriptionsTab, error, () => listener.onClickReloadTab(SubscriptionsTab)).toService

  def showEmptyAccountsContent(error: Boolean): TaskService[Unit] =
    showEmptyContent(AccountsTab, error, () => listener.onClickReloadTab(AccountsTab)).toService

  override def onTabReselected(tab: Tab): Unit = {}

  override def onTabUnselected(tab: Tab): Unit = {}

  override def onTabSelected(tab: Tab): Unit = tab.getTag match {
    case tab: ProfileTab => listener.onClickProfileTab(tab)
    case _ =>
  }

  override def onOffsetChanged(appBarLayout: AppBarLayout, offset: Int): Unit = {
    val maxScroll = appBarLayout.getTotalScrollRange.toFloat
    listener.onBarLayoutOffsetChanged(maxScroll, offset)
  }

  private[this] def showEmptyContent(tab: ProfileTab, error: Boolean, reload: () => Unit): Ui[Any] =
    (dom.recyclerView <~
      vVisible <~
      rvAdapter(EmptyProfileAdapter(tab, error, reload))) ~
      (dom.loadingView <~ vInvisible)

  private[this] def showDialog(dialog: DialogFragment): TaskService[Unit] = TaskService {
    CatchAll[UiException] {
      activityContextWrapper.original.get match {
        case Some(activity: AppCompatActivity) =>
          val ft = activity.getSupportFragmentManager.beginTransaction()
          Option(activity.getSupportFragmentManager.findFragmentByTag(tagDialog)) foreach ft.remove
          ft.addToBackStack(javaNull)
          dialog.show(ft, tagDialog)
        case _ =>
      }
    }
  }

  private[this] def showMessage(res: Int): Ui[Any] = dom.rootLayout <~ vSnackbarShort(res)

  private[this] def showError(message: Int, clickAction: () => Unit): Ui[Any] =
    (dom.rootLayout <~ vSnackbarIndefiniteAction(message, R.string.buttonErrorReload, clickAction)) ~
      (dom.loadingView <~ vInvisible)

  private[this] def accountClickListener(accountOption: AccountOption, accountSync: AccountSync): Unit =
    (accountOption, accountSync.cloudId) match {
      case (SyncOption, _) => listener.onClickSynchronizeDevice()
      case (DeleteOption, Some(cloudId)) => listener.onClickDeleteDevice(cloudId)
      case (CopyOption, Some(cloudId)) => listener.onClickCopyDevice(cloudId, accountSync.title)
      case (ChangeNameOption, Some(cloudId)) => listener.onClickRenameDevice(cloudId, accountSync.title)
      case (PrintInfoOption, Some(cloudId)) => listener.onClickPrintInfoDevice(cloudId)
      case _ =>
    }

  // Styles

  private[this] def menuAvatarStyle(implicit context: ContextWrapper): Tweak[ImageView] =
    Lollipop ifSupportedThen {
      vCircleOutlineProvider()
    } getOrElse Tweak.blank

}
