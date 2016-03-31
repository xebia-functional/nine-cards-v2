package com.fortysevendeg.ninecardslauncher.app.ui.launcher.holders

import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.LauncherWorkSpaceHolder
import macroid.ContextWrapper
import macroid.FullDsl._

class LauncherWorkSpaceMomentsHolder(implicit contextWrapper: ContextWrapper)
  extends LauncherWorkSpaceHolder {

  var text = slot[TextView]

  addView((w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(30) <~ tvColor(Color.WHITE) <~ tvText("MOMENTS") <~ tvGravity(Gravity.CENTER)).get)

}