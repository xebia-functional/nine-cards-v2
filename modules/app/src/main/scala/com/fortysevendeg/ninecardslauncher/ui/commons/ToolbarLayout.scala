package com.fortysevendeg.ninecardslauncher.ui.commons

import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, ActivityContext}
import macroid.FullDsl._

trait ToolbarLayout {

  var toolbar = slot[Toolbar]

  def darkToolbar(implicit activityContext: ActivityContext) =
    Ui {
      val contextTheme = new ContextThemeWrapper(activityContext.get, R.style.ThemeOverlay_AppCompat_Dark_ActionBar)
      val darkToolBar = new Toolbar(contextTheme)
      darkToolBar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light)
      darkToolBar
    } <~ wire(toolbar)

}
