package com.fortysevendeg.ninecardslauncher.ui.commons

import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import macroid.{Tweak, ActivityContext, AppContext}

object AsyncImageApplicationTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit appContext: AppContext): Tweak[W] = Tweak[W](
    imageView => {
      Glide.`with`(appContext.get)
        .load(uri)
        .into(imageView)
    }
  )

}

object AsyncImageActivityTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit activityContext: ActivityContext): Tweak[W] = Tweak[W](
    imageView => {
      Glide.`with`(activityContext.get)
        .load(uri)
        .into(imageView)
    }
  )

}

object AsyncImageFragmentTweaks {
  type W = ImageView

  def ivUri(fragment: Fragment, uri: String): Tweak[W] = Tweak[W](
    imageView => {
      Glide.`with`(fragment)
        .load(uri)
        .into(imageView)
    }
  )

}
