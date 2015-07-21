package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import macroid.{Tweak, ActivityContextWrapper, ContextWrapper}

object AsyncImageApplicationTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit context: ContextWrapper): Tweak[W] = Tweak[W](
    imageView => {
      Glide.`with`(context.application)
        .load(uri)
        .crossFade()
        .into(imageView)
    }
  )

}

object AsyncImageActivityTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit context: ActivityContextWrapper): Tweak[W] = Tweak[W](
    imageView => {
      Glide.`with`(context.getOriginal)
        .load(uri)
        .crossFade()
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
        .crossFade()
        .into(imageView)
    }
  )

}
