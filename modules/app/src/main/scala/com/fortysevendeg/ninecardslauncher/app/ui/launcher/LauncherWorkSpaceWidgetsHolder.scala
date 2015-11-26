package com.fortysevendeg.ninecardslauncher.app.ui.launcher

import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.components.layouts.LauncherWorkSpaceHolder
import macroid.FullDsl._
import macroid.{ActivityContextWrapper}

class LauncherWorkSpaceWidgetsHolder(implicit activityContext: ActivityContextWrapper)
  extends LauncherWorkSpaceHolder {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(30) <~ tvColor(Color.WHITE) <~ tvText("WIDGETS") <~ tvGravity(Gravity.CENTER)
    )
  )

}