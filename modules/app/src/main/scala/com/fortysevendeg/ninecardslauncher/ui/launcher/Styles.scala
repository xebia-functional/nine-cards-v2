package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.graphics.Color
import android.widget.TextView
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.TextTweaks._
import macroid.{AppContext, Tweak}

trait Styles {

  def titleStyle(implicit appContext: AppContext): Tweak[TextView] =
    vMatchParent +
      tvText("test") +
      tvColor(Color.WHITE)

}
