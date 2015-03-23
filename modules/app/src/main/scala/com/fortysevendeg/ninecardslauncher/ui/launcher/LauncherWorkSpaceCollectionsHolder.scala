package com.fortysevendeg.ninecardslauncher.ui.launcher

import android.widget.ImageView
import com.fortysevendeg.macroid.extras.ViewTweaks._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import macroid.FullDsl._
import macroid.{ActivityContext, AppContext}

class LauncherWorkSpaceCollectionsHolder(implicit appContext: AppContext, activityContext: ActivityContext)
  extends LauncherWorkSpaceHolder {

  var image = slot[ImageView]

  addView(
    getUi(
      w[ImageView] <~ wire(image) <~ vMatchParent
    )
  )

  def populate(data: LauncherData) = runUi(image <~ ivSrc(resGetDrawable(data.collection.head.icon).get))

}