package com.fortysevendeg.ninecardslauncher.ui.collections

import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import android.widget.{LinearLayout, TextView, FrameLayout}
import com.fortysevendeg.ninecardslauncher.modules.persistent.PersistentServicesComponent
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{IdGeneration, Ui, ActivityContext, AppContext}

trait Layout
  extends Styles
  with IdGeneration {

  self: PersistentServicesComponent =>

  var toolbar = slot[Toolbar]
  var viewPager = slot[ViewPager]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      darkToolbar <~ toolbarStyle <~ wire(toolbar),
      l[ViewPager]() <~ viewPagerStyle <~ wire(viewPager) <~ id(Id.pager) // ViewPager need set resource id
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

trait CollectionFragmentLayout {

  var test = slot[TextView]

  def layout(implicit appContext: AppContext, context: ActivityContext) = getUi(
    w[TextView] <~ wire(test)
  )

}
