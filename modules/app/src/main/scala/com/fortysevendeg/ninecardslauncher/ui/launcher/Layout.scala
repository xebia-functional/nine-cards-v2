package com.fortysevendeg.ninecardslauncher.ui.launcher

import com.fortysevendeg.ninecardslauncher.ui.components.{TestMultipleTypesAnimatedWorkSpaces, TestAnimatedWorkSpaces}
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

trait Layout
    extends Styles {

  def content(implicit appContext: AppContext, context: ActivityContext) = getUi(
    l[TestMultipleTypesAnimatedWorkSpaces]()
  )

}
