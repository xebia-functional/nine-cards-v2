package com.fortysevendeg.ninecardslauncher.app.ui.applinks

import android.view.ViewGroup
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ExtraTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.commons.adapters.sharedcollections.SharedCollectionItem
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.TaskServiceOps._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.ops.UiOps._
import com.fortysevendeg.ninecardslauncher.commons.services.TaskService._
import com.fortysevendeg.ninecardslauncher.process.sharedcollections.models.SharedCollection
import com.fortysevendeg.ninecardslauncher.process.theme.models.{CardLayoutBackgroundColor, NineCardsTheme}
import com.fortysevendeg.ninecardslauncher2.R
import macroid._

class AppLinksReceiverUiActions(
  dom: AppLinksReceiverDOM)
  (implicit val context: ActivityContextWrapper, val uiContext: UiContext[_])
  extends SharedCollectionItem {

  override def content: ViewGroup = dom.collectionView

  def initializeView()(implicit theme: NineCardsTheme): TaskService[Unit] =
    ((dom.rootView <~ vBackgroundColor(theme.get(CardLayoutBackgroundColor))) ~
      initialize() ~
      (dom.loadingView <~ vVisible) ~
      (dom.collectionView <~ vGone)).toService

  def showCollection(jobs: AppLinksReceiverJobs, collection: SharedCollection)(implicit theme: NineCardsTheme): TaskService[Unit] =
    ((dom.loadingView <~ vGone) ~
      (dom.collectionView <~ vVisible) ~
      bind(collection, onAddCollection(jobs), onShareCollection(jobs))).toService

  def showLinkNotSupportedMessage(): TaskService[Unit] =
    uiShortToast2(R.string.linkNotSupportedError).toService

  def showUnexpectedErrorMessage(): TaskService[Unit] =
    uiShortToast2(R.string.contactUsError).toService

  def exit(): TaskService[Unit] =
    Ui(context.original.get foreach (_.finish())).toService

  def onAddCollection(jobs: AppLinksReceiverJobs)(col: SharedCollection): Unit =
    (for {
      _ <- jobs.addCollection(col)
      _ <- exit()
    } yield ()).resolveAsync()

  def onShareCollection(jobs: AppLinksReceiverJobs)(col: SharedCollection): Unit =
    (for {
      _ <- jobs.shareCollection(col)
      _ <- exit()
    } yield ()).resolveAsync()
}