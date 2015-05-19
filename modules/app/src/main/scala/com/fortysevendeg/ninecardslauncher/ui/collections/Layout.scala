package com.fortysevendeg.ninecardslauncher.ui.collections

import android.support.v4.view.ViewPager
import android.support.v7.widget.{CardView, RecyclerView}
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.ui.commons.ToolbarLayout
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayout
import macroid.FullDsl._
import macroid.{ContextWrapper, ActivityContextWrapper, IdGeneration}

trait Layout
  extends Styles
  with ToolbarLayout
  with IdGeneration {

  var viewPager = slot[ViewPager]

  var tabs = slot[SlidingTabLayout]

  var iconContent = slot[FrameLayout]

  var icon = slot[ImageView]

  def layout(implicit context: ActivityContextWrapper) = getUi(
    l[FrameLayout](
      darkToolbar <~ toolbarStyle,
      l[FrameLayout](
        w[ImageView] <~ iconStyle <~ wire(icon)
      ) <~ iconContentStyle <~ wire(iconContent),
      l[ViewPager]() <~ viewPagerStyle <~ wire(viewPager) <~ id(Id.pager), // ViewPager need set resource id
      l[SlidingTabLayout]() <~ tabsStyle <~ wire(tabs)
    ) <~ rootStyle
  )

}

trait CollectionFragmentLayout
  extends CollectionFragmentStyles {

  var recyclerView = slot[RecyclerView]

  def layout(implicit context: ActivityContextWrapper) = getUi(
    w[RecyclerView] <~ wire(recyclerView) <~ recyclerStyle
  )

}

class CollectionLayoutAdapter(heightCard: Int)(implicit context: ActivityContextWrapper)
  extends CollectionAdapterStyles
  with ComponentRegistryImpl {

  override val contextProvider: ContextWrapper = context

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

class ViewHolderCollectionAdapter(adapter: CollectionLayoutAdapter)(implicit context: ActivityContextWrapper)
  extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val icon = adapter.icon

  val name = adapter.name

}