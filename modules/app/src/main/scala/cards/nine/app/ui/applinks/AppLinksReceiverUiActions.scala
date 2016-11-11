package cards.nine.app.ui.applinks

import android.view.ViewGroup
import macroid.extras.UIActionsExtras._
import cards.nine.app.ui.commons.UiContext
import cards.nine.app.ui.commons.adapters.sharedcollections.SharedCollectionItem
import cards.nine.app.ui.commons.ops.TaskServiceOps._
import cards.nine.app.ui.commons.ops.UiOps._
import cards.nine.commons.services.TaskService._
import cards.nine.models.types.theme.{CardLayoutBackgroundColor, CardTextColor}
import cards.nine.models.{NineCardsTheme, SharedCollection}
import macroid.extras.TextViewTweaks._
import macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.R
import macroid._

class AppLinksReceiverUiActions(
  dom: AppLinksReceiverDOM)
  (implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends SharedCollectionItem {

  override def content: ViewGroup = dom.collectionView

  def initializeView(theme: NineCardsTheme): TaskService[Unit] =
    ((dom.rootView <~ vBackgroundColor(theme.get(CardLayoutBackgroundColor))) ~
      (dom.loadingText <~ tvColor(theme.get(CardTextColor))) ~
      initialize()(theme) ~
      (dom.loadingView <~ vVisible) ~
      (dom.collectionView <~ vGone)).toService

  def showCollection(jobs: AppLinksReceiverJobs, collection: SharedCollection, theme: NineCardsTheme): TaskService[Unit] = {

    def onAddCollection(): Unit =
      jobs.addCollection(collection).resolveAsyncServiceOr(_ => jobs.showError())

    def onShareCollection(): Unit =
      jobs.shareCollection(collection).resolveAsyncServiceOr(_ => jobs.showError())

    ((dom.loadingView <~ vGone) ~
      (dom.collectionView <~ vVisible) ~
      bind(collection, onAddCollection(), onShareCollection())(theme)).toService
  }

  def showLinkNotSupportedMessage(): TaskService[Unit] =
    uiShortToast(R.string.linkNotSupportedError).toService

  def showUnexpectedErrorMessage(): TaskService[Unit] =
    uiShortToast(R.string.contactUsError).toService

  def exit(): TaskService[Unit] =
    Ui(context.original.get foreach (_.finish())).toService


}