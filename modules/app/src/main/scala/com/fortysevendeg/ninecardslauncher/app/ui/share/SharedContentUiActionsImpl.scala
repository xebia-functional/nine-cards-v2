package com.fortysevendeg.ninecardslauncher.app.ui.share

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import com.fortysevendeg.ninecardslauncher.app.ui.commons.UiContext
import com.fortysevendeg.ninecardslauncher.process.commons.models.Collection
import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.TypedFindView
import macroid.{Contexts, Ui}

trait SharedContentUiActionsImpl
  extends SharedContentUiActions {

  self: TypedFindView with Contexts[AppCompatActivity] =>

  implicit val uiContext: UiContext[Activity]

  implicit val presenter: SharedContentPresenter

  implicit lazy val theme: NineCardsTheme = presenter.getTheme

  override def showChooseCollection(collections: Seq[Collection]): Ui[Any] = ???

  override def showSuccess(): Ui[Any] = ???

  override def showErrorEmptyContent(): Ui[Any] = ???

  override def showErrorContentNotSupported(): Ui[Any] = ???

  override def showUnexpectedError(): Ui[Any] = ???
}
