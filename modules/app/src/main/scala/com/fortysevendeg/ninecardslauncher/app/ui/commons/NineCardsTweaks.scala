package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.fortysevendeg.ninecardslauncher.app.ui.components.CharDrawable
import macroid.{ActivityContextWrapper, ContextWrapper, Tweak}

object RecyclerViewListenerTweaks {
  type W = RecyclerView

  def rvCollectionScrollListener(
    scrolled: (Int, Int, Int) => Int,
    scrollStateChanged: (Int, RecyclerView, Int) => Unit
  )(implicit context: ContextWrapper): Tweak[W] = Tweak[W] {
    _.addOnScrollListener(new OnScrollListener {
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

object AsyncImageCardsTweaks {
  type W = ImageView

  def ivUri(fragment: Fragment, uri: String, name: String)(implicit context: ActivityContextWrapper): Tweak[W] = Tweak[W](
    imageView =>
      Glide.`with`(fragment)
        .load(uri)
        .asBitmap()
        .into(new SimpleTarget[Bitmap]() {
          override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit = {
            imageView.setImageDrawable(new CharDrawable(name.substring(0, 1), true))
          }
          override def onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation[_ >: Bitmap]): Unit = {
            imageView.setImageBitmap(resource)
          }
        })
  )

  def ivUriContact(fragment: Fragment, uri: String, name: String)(implicit context: ActivityContextWrapper): Tweak[W] = Tweak[W](
    imageView =>
      Glide.`with`(fragment)
        .loadFromMediaStore(Uri.withAppendedPath(Uri.parse(uri), Contacts.Photo.DISPLAY_PHOTO))
        .asBitmap()
        .into(new SimpleTarget[Bitmap]() {
          override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit = {
            imageView.setImageDrawable(new CharDrawable(name.substring(0, 1)))
          }
          override def onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation[_ >: Bitmap]): Unit = {
            imageView.setImageBitmap(resource)
          }
        })
  )

}