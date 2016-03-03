package com.fortysevendeg.ninecardslauncher.app.ui.commons

import java.io.{File, InputStream}

import android.content.{ContentResolver, UriMatcher}
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.stream.StreamModelLoader
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.{DrawableTypeRequest, Glide, Priority}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.{Snail, ActivityContextWrapper, Tweak}
import macroid.FullDsl._
import com.fortysevendeg.macroid.extras.ImageViewTweaks._

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
      loadCardUri(
        imageView = imageView,
        request = glide().load(uri),
        uri = uri,
        char = name.substring(0, 1),
        circular = circular)
    })

  def ivUriContact(uri: String, name: String, circular: Boolean = false)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      makeRequest(
        request = glide().using(new ContactPhotoLoader(context.application.getContentResolver)).load(Uri.parse(uri)),
        imageView = imageView,
        char = name.substring(0, 1),
        circular = circular,
        fadeInFailed = false)
    })

  def ivUriContactInfo(uri: String)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      makeContactRequest(
        request = glide().using(new ContactPhotoLoader(context.application.getContentResolver)).load(Uri.parse(uri)),
        imageView = imageView,
        fadeInFailed = false)
    })

  private[this] def glide()(implicit uiContext: UiContext[_]) = uiContext match {
    case c: ApplicationUiContext => Glide.`with`(c.value)
    case c: ActivityUiContext => Glide.`with`(c.value)
    case c: FragmentUiContext => Glide.`with`(c.value)
  }

  private[this] def loadCardUri(
    imageView: ImageView,
    request: DrawableTypeRequest[_],
    uri: String,
    char: String,
    circular: Boolean = false)(implicit context: ActivityContextWrapper, uiContext: UiContext[_]) = {
    if (new File(uri).exists()) {
      makeRequest(
        request = request,
        imageView = imageView,
        char = char,
        circular = circular)
    } else {
      runUi(imageView <~ ivSrc(new CharDrawable(char, circle = circular)))
    }
  }

  private[this] def makeRequest(
    request: DrawableTypeRequest[_],
    imageView: ImageView,
    char: String,
    circular: Boolean = false,
    fadeInFailed: Boolean = true)(implicit context: ActivityContextWrapper) = {
    request
      .crossFade()
      .into(new ViewTarget[ImageView, GlideDrawable](imageView) {
        override def onLoadStarted(placeholder: Drawable): Unit =
          imageView.setImageDrawable(javaNull)
        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit =
          runUi(imageView <~ ivSrc(new CharDrawable(char, circle = circular)) <~ (if (fadeInFailed) fadeIn(200) else Snail.blank))
        override def onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation[_ >: GlideDrawable]): Unit =
          view.setImageDrawable(resource.getCurrent)
      })
  }

  private[this] def makeContactRequest(
    request: DrawableTypeRequest[_],
    imageView: ImageView,
    fadeInFailed: Boolean = true)(implicit context: ActivityContextWrapper) = {
    request
      .crossFade()
      .into(new ViewTarget[ImageView, GlideDrawable](imageView) {
        override def onLoadStarted(placeholder: Drawable): Unit =
          imageView.setImageDrawable(javaNull)
        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit =
          runUi(imageView <~ ivSrc(R.drawable.dialog_contact_icon_general_info) <~ ivScaleType(ScaleType.CENTER_INSIDE) <~ (if (fadeInFailed) fadeIn(200) else Snail.blank))
        override def onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation[_ >: GlideDrawable]): Unit =
          view.setImageDrawable(resource.getCurrent)
      })
  }

  class ContactPhotoLoader(contentResolver: ContentResolver) extends StreamModelLoader[Uri] {

    // A lookup uri (e.g. content://com.android.contacts/contacts/lookup/3570i61d948d30808e537)
    val idLookup = 1
    // A contact thumbnail uri (e.g. content://com.android.contacts/contacts/38/photo)
    val idThumbnail = 2
    // A contact uri (e.g. content://com.android.contacts/contacts/38)
    val idContact = 3
    // A contact display photo (high resolution) uri (e.g. content://com.android.contacts/display_photo/5)
    val idDisplayPhoto = 4

    val matcher = new UriMatcher(UriMatcher.NO_MATCH)
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*/#", idLookup)
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*", idLookup)
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/#/photo", idThumbnail)
    matcher.addURI(ContactsContract.AUTHORITY, "contacts/#", idContact)
    matcher.addURI(ContactsContract.AUTHORITY, "display_photo/#", idDisplayPhoto)

    override def getResourceFetcher(model: Uri, width: Int, height: Int): DataFetcher[InputStream] =
      new DataFetcher[InputStream]() {

        override def loadData(priority: Priority): InputStream = matcher.`match`(model) match {
          case `idLookup` =>
            Option(ContactsContract.Contacts.lookupContact(contentResolver, model)) match {
              case Some(u) => Contacts.openContactPhotoInputStream(contentResolver, u, true)
              case _ => javaNull
            }
          case `idContact` => Contacts.openContactPhotoInputStream(contentResolver, model, true)
          case `idThumbnail` => contentResolver.openInputStream(model)
          case `idDisplayPhoto` => contentResolver.openInputStream(model)
          case _ => javaNull
        }

        override def getId: String = model.toString

        override def cleanup(): Unit = {}

        override def cancel(): Unit = {}
      }
  }

}