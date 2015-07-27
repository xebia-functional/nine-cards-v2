package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.support.v7.widget.Toolbar
import android.view.ContextThemeWrapper
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Ui, ActivityContextWrapper}
import macroid.FullDsl._

trait ToolbarLayout {

  var toolbar = slot[Toolbar]

  def darkToolbar(implicit context: ActivityContextWrapper) =
    Ui {
      val contextTheme = new ContextThemeWrapper(context.getOriginal, R.style.ThemeOverlay_AppCompat_Dark_ActionBar)
      val darkToolBar = new Toolbar(contextTheme)
      darkToolBar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light)
      darkToolBar
    } <~ wire(toolbar)

}
