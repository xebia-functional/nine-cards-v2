package com.fortysevendeg.ninecardslauncher.app.ui.collections

import com.fortysevendeg.ninecardslauncher.process.theme.models.NineCardsTheme
import com.fortysevendeg.ninecardslauncher2.{TR, TypedFindView}
import macroid.ActivityContextWrapper
import macroid.FullDsl._

trait CollectionsDetailsComposer
  extends Styles {

  self: TypedFindView =>

  lazy val toolbar = Option(findView(TR.collections_toolbar))

  lazy val root = Option(findView(TR.collections_root))

  lazy val viewPager = Option(findView(TR.collections_view_pager))

  lazy val tabs = Option(findView(TR.collections_tabs))

  lazy val iconContent = Option(findView(TR.collections_icon_content))

  lazy val icon = Option(findView(TR.collections_icon))

  def initUi(implicit context: ActivityContextWrapper, theme: NineCardsTheme) =
    (root <~ rootStyle) ~ (tabs <~ tabsStyle)

}

