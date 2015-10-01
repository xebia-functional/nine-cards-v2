package com.fortysevendeg.ninecardslauncher.app.ui.commons

import java.io.InputStream

import android.content.{UriMatcher, ContentResolver}
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.widget.ImageView
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.stream.StreamModelLoader
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.{DrawableTypeRequest, Glide, Priority}
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
        request = glide().using(new ContactPhotoLoader(context.application.getContentResolver)).load(Uri.parse(uri)),
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
              case _ => None.orNull
            }
          case `idContact` => Contacts.openContactPhotoInputStream(contentResolver, model, true)
          case `idThumbnail` => contentResolver.openInputStream(model)
          case `idDisplayPhoto` => contentResolver.openInputStream(model)
          case _ => None.orNull
        }

        override def getId: String = model.toString

        override def cleanup(): Unit = {}

        override def cancel(): Unit = {}
      }
  }

}