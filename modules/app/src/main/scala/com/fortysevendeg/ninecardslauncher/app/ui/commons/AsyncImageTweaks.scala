package com.fortysevendeg.ninecardslauncher.app.ui.commons

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract.Contacts
import android.widget.ImageView
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.{DrawableTypeRequest, Glide}
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.macroid.extras.ResourcesExtras._
import com.fortysevendeg.ninecardslauncher.app.ui.components.CharDrawable
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid.{ActivityContextWrapper, Tweak}

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

  def ivCardUri(uri: String, name: String, circular: Boolean = false)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      makeRequest(
        request = glide().load(uri),
        imageView = imageView,
        char = name.substring(0, 1),
        circular = circular)
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
    circular: Boolean = false)(implicit context: ActivityContextWrapper) = {
    val fadeDuration = resGetInteger(R.integer.fade_duration_new_image)
    request
      .asBitmap()
      .into(new SimpleTarget[Bitmap]() {
        override def onLoadStarted(placeholder: Drawable): Unit =
          imageView.setImageDrawable(null)

        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit =
          runUi(imageView <~ ivSrc(new CharDrawable(char, circle = circular)) <~ fadeIn(fadeDuration))

        override def onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation[_ >: Bitmap]): Unit =
          runUi(imageView <~ ivSrc(resource) <~ fadeIn(fadeDuration))
      })
  }

}