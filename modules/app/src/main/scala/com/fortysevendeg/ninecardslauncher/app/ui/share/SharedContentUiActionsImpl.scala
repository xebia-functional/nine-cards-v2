package com.fortysevendeg.ninecardslauncher.app.ui.share

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.macroid.extras.UIActionsExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.app.ui.components.dialogs.CollectionDialog
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{R, TypedFindView}
import macroid.{Contexts, Ui}

trait SharedContentUiActionsImpl
  extends SharedContentUiActions {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val uiContext: UiContext[Activity]

  implicit val presenter: SharedContentPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  override def showChooseCollection(collections: Seq[Collection]): Ui[Any] = activityContextWrapper.original.get match {
    case Some(activity: Activity) => Ui(new CollectionDialog(collections).show())
    case _ => Ui.nop
  }

  override def showSuccess(): Ui[Any] = uiShortToast(R.string.sharedCardAdded) ~ finishUi()

  override def showErrorEmptyContent(): Ui[Any] = uiShortToast(R.string.sharedContentErrorEmpty) ~ finishUi()

  override def showErrorContentNotSupported(): Ui[Any] = uiLongToast(R.string.sharedContentErrorNotSupported) ~ finishUi()

  override def showUnexpectedError(): Ui[Any] = uiShortToast(R.string.sharedContentErrorUnexpected) ~ finishUi()

  override def finishUi(): Ui[Any] = Ui(uiContext.value.finish())
}
