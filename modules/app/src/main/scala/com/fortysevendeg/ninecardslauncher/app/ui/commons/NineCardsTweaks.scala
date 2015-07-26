package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import macroid.{Tweak, ActivityContextWrapper, ContextWrapper}

object RecyclerViewListenerTweaks {
  type W = RecyclerView

  def rvCollectionScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit
    )(implicit context: ContextWrapper): Tweak[W] = Tweak[W] {
    _.setOnScrollListener(new OnScrollListener {
      var scrollY = 0
      override def onScrolled(recyclerView: W, dx: Int, dy: Int): Unit = {
        super.onScrolled(recyclerView, dx, dy)
        scrollY = scrolled(scrollY, dx, dy)
      }
      override def onScrollStateChanged(recyclerView: W, newState: Int): Unit = {
        super.onScrollStateChanged(recyclerView, newState)
        scrollStateChanged(scrollY, recyclerView, newState)
      }
    })
  }

}

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
