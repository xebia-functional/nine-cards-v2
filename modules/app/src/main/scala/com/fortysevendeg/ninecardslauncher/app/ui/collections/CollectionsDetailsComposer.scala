package com.fortysevendeg.ninecardslauncher.app.ui.collections

import android.support.v7.widget.{CardView, RecyclerView}
import android.widget.{ImageView, LinearLayout, TextView}
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

trait CollectionFragmentLayout
  extends CollectionFragmentStyles {

  var recyclerView = slot[RecyclerView]

  def layout(implicit context: ActivityContextWrapper) = getUi(
    w[RecyclerView] <~ wire(recyclerView) <~ recyclerStyle
  )

}

class CollectionLayoutAdapter(heightCard: Int)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends CollectionAdapterStyles {

  var icon = slot[ImageView]

  var name = slot[TextView]

  val content = layout

  private def layout(implicit context: ActivityContextWrapper) = getUi(
    l[CardView](
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ contentStyle
    ) <~ rootStyle(heightCard)
  )

}

class ViewHolderCollectionAdapter(adapter: CollectionLayoutAdapter)(implicit context: ActivityContextWrapper, theme: NineCardsTheme)
  extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val icon = adapter.icon

  val name = adapter.name

}