package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.widget._
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

trait Layout
    extends Styles {

  def content(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[FrameLayout](
      w[TextView] <~ titleStyle
    )
  )

}
