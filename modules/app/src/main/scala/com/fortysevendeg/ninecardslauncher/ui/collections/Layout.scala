package com.fortysevendeg.ninecardslauncher.ui.collections

import android.support.v4.view.ViewPager
import android.support.v7.widget.{CardView, RecyclerView, Toolbar}
import android.view.ContextThemeWrapper
import android.widget.{FrameLayout, ImageView, LinearLayout, TextView}
import com.fortysevendeg.ninecardslauncher.di.{InjectorProvider, PersistentServicesProvider}
import com.fortysevendeg.ninecardslauncher.modules.ComponentRegistryImpl
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServices
import com.fortysevendeg.ninecardslauncher.ui.components.SlidingTabLayout
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext, IdGeneration, Ui}

trait Layout
  extends Styles
  with IdGeneration {

  self: InjectorProvider =>

  var toolbar = slot[Toolbar]

  var viewPager = slot[ViewPager]

  var tabs = slot[SlidingTabLayout]

  var iconContent = slot[FrameLayout]

  var icon = slot[ImageView]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      darkToolbar <~ toolbarStyle <~ wire(toolbar),
      l[FrameLayout](
        w[ImageView] <~ iconStyle <~ wire(icon)
      ) <~ iconContentStyle <~ wire(iconContent),
      l[ViewPager]() <~ viewPagerStyle <~ wire(viewPager) <~ id(Id.pager), // ViewPager need set resource id
      l[SlidingTabLayout]() <~ tabsStyle <~ wire(tabs)
    ) <~ rootStyle
  )

  def darkToolbar(implicit activityContext: ActivityContext) =
    Ui {
      val contextTheme = new ContextThemeWrapper(activityContext.get, R.style.ThemeOverlay_AppCompat_Dark_ActionBar)
      val darkToolBar = new Toolbar(contextTheme)
      darkToolBar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light)
      darkToolBar
    }

}

trait CollectionFragmentLayout
  extends CollectionFragmentStyles {

  var recyclerView = slot[RecyclerView]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    w[RecyclerView] <~ wire(recyclerView) <~ recyclerStyle
  )

}

class CollectionLayoutAdapter(
    heightCard: Int,
    persistentServices: Option[PersistentServices])
    (implicit context: ActivityContext, appContext: AppContext)
  extends PersistentServicesProvider
  with CollectionAdapterStyles
  with ComponentRegistryImpl {

  override val appContextProvider: AppContext = appContext

  var icon = slot[ImageView]

  var name = slot[TextView]

  val content = layout

  private def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[CardView](
      l[LinearLayout](
        w[ImageView] <~ wire(icon) <~ iconStyle,
        w[TextView] <~ wire(name) <~ nameStyle
      ) <~ contentStyle
    ) <~ rootStyle(heightCard)
  )

  override val getPersistentServices: Option[PersistentServices] = persistentServices
}

class ViewHolderCollectionAdapter(adapter: CollectionLayoutAdapter)(implicit context: ActivityContext, appContext: AppContext)
  extends RecyclerView.ViewHolder(adapter.content) {

  val content = adapter.content

  val icon = adapter.icon

  val name = adapter.name

}