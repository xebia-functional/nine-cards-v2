package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

class LauncherWorkSpaceWidgetsHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder {

  var text = slot[TextView]

  addView(
    getUi(
      w[TextView] <~ wire(text) <~ vMatchParent <~ tvSize(30) <~ tvColor(Color.WHITE) <~ tvText("WIDGETS") <~ tvGravity(Gravity.CENTER)
    )
  )

}