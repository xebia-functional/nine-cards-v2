package com.fortysevendeg.ninecardslauncher.app.ui.collections.actions.shortcuts

import com.fortysevendeg.macroid.extras.RecyclerViewTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.actions.{BaseActionFragment, Styles}
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.tweaks.DialogToolbarTweaks._
import com.fortysevendeg.ninecardslauncher.process.device.models.Shortcut
import com.fortysevendeg.ninecardslauncher.process.theme.models.DrawerBackgroundColor
import com.fortysevendeg.ninecardslauncher2.{R, TR, TypedFindView}
import macroid._

import scala.math.Ordering.Implicits._

trait ShortcutUiActionsImpl
  extends ShortcutUiActions
  with Styles {

  self: TypedFindView with BaseActionFragment =>

  implicit val presenter: ShortcutPresenter

  lazy val recycler = findView(TR.actions_recycler)

  override def initialize(): Ui[Any] =
    (toolbar <~
      dtbInit(colorPrimary) <~
      dtbChangeText(R.string.shortcuts) <~
      dtbNavigationOnClickListener((_) => unreveal())) ~
      (recycler <~
        recyclerStyle <~
        vBackgroundColor(theme.get(DrawerBackgroundColor)))

  override def showLoading(): Ui[Any] = (loading <~ vVisible) ~ (recycler <~ vGone)

  override def close(): Ui[Any] = unreveal()

  override def showLoadingShortcutsError(): Ui[Any] = showError(R.string.errorLoadingShortcuts, presenter.loadShortcuts())

  override def loadShortcuts(shortcuts: Seq[Shortcut]): Ui[Any] = {
    val sortedShortcuts = shortcuts sortBy sortByTitle
    val adapter = ShortcutAdapter(sortedShortcuts)
      (recycler <~
        vVisible <~
        rvLayoutManager(adapter.getLayoutManager) <~
        rvAdapter(adapter)) ~
      (loading <~ vGone)
  }

  private[this] def sortByTitle(shortcut: Shortcut) = shortcut.title map (c => if (c.isUpper) 2 * c + 1 else 2 * (c - ('a' - 'A')))

}