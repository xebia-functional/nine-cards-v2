package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.widget.ImageView
import com.bumptech.glide.{DrawableTypeRequest, Glide}
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

object AsyncImageTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit context: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      glide()
        .load(uri)
        .crossFade()
        .into(imageView)
    }
  )

  def ivCardUri(uri: String, name: String)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      makeRequest(
        request = glide().load(uri),
        imageView = imageView,
        char = name.substring(0, 1))
    })

  def ivUriContact(uri: String, name: String, circular: Boolean = false)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      makeRequest(
        request = glide().loadFromMediaStore(Uri.withAppendedPath(Uri.parse(uri), Contacts.Photo.DISPLAY_PHOTO)),
        imageView = imageView,
        char = name.substring(0, 1),
        circular = circular)
    })

  private[this] def glide()(implicit uiContext: UiContext[_]) = uiContext match {
    case c: ApplicationUiContext => Glide.`with`(c.value)
    case c: ActivityUiContext => Glide.`with`(c.value)
    case c: FragmentUiContext => Glide.`with`(c.value)
  }

  private[this] def makeRequest(
    request: DrawableTypeRequest[_],
    imageView: ImageView,
    char: String,
    circular: Boolean = false)(implicit context: ActivityContextWrapper) =
      request
        .asBitmap()
        .into(new SimpleTarget[Bitmap]() {
        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit = {
          imageView.setImageDrawable(new CharDrawable(char, circle = circular))
        }

        override def onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation[_ >: Bitmap]): Unit = {
          imageView.setImageBitmap(resource)
        }
      })

}