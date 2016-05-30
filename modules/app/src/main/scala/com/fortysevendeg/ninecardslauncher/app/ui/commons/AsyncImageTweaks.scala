package com.fortysevendeg.ninecardslauncher.app.ui.commons

import java.io.{File, InputStream}

import android.content.{ContentResolver, UriMatcher}
import android.graphics.Bitmap
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
import com.bumptech.glide._
import com.bumptech.glide.load.resource.bitmap.{BitmapEncoder, StreamBitmapDecoder}
import com.bumptech.glide.load.resource.file.FileToStreamDecoder
import com.fortysevendeg.macroid.extras.ImageViewTweaks._
import com.fortysevendeg.ninecardslauncher.app.ui.commons.glide.{AppIconLoader, ApplicationIconDecoder}
import com.fortysevendeg.ninecardslauncher.app.ui.components.drawables.CharDrawable
import com.fortysevendeg.ninecardslauncher.commons._
import com.fortysevendeg.ninecardslauncher2.R
import macroid.FullDsl._
import macroid._

import scala.util.Try

object AsyncImageTweaks {
  type W = ImageView

  def ivUri(uri: String)(implicit context: UiContext[_]): Tweak[W] = Tweak[W] { imageView =>
    glide() foreach { glide =>
      glide
        .load(uri)
        .crossFade()
        .into(imageView)
    }
  }

  def ivSrcByPackageName(maybePackageName: Option[String], term: String)(implicit context: UiContext[_], contextWrapper: ContextWrapper): Tweak[W] = Tweak[W](
    imageView => {
      (glide(), maybePackageName) match {
        case (Some(glide), Some(packageName)) =>
          glide
            .using(new AppIconLoader, classOf[String])
            .from(classOf[String])
            .as(classOf[Bitmap])
            .decoder(new ApplicationIconDecoder(packageName))
            .cacheDecoder(new FileToStreamDecoder(new StreamBitmapDecoder(contextWrapper.application)))
            .encoder(new BitmapEncoder())
            .load(packageName)
            .into(imageView)
        case _ =>
          (imageView <~ ivSrc(new CharDrawable(term.charAt(0).toString, circle = true))).run
      }
      glide() foreach { glide =>
        maybePackageName map { packageName =>
        glide
          .using(new AppIconLoader, classOf[String])
          .from(classOf[String])
          .as(classOf[Bitmap])
          .decoder(new ApplicationIconDecoder(packageName))
          .cacheDecoder(new FileToStreamDecoder(new StreamBitmapDecoder(contextWrapper.application)))
          .encoder(new BitmapEncoder())
          .load(packageName)
          .into(imageView)
        } getOrElse {

        }
      }
    }
  )

  def ivCardUri(uri: String, name: String, circular: Boolean = false)(implicit context: ContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      glide() foreach { glide =>
        loadCardUri(
          imageView = imageView,
          request = glide.load(uri),
          uri = uri,
          char = name.substring(0, 1),
          circular = circular)
      }
    })

  def ivUriContact(uri: String, name: String, circular: Boolean = false)(implicit context: ContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      glide() foreach { glide =>
        makeRequest(
          request = glide.using(new ContactPhotoLoader(context.application.getContentResolver)).load(Uri.parse(uri)),
          imageView = imageView,
          char = name.substring(0, 1),
          circular = circular,
          fadeInFailed = false)
      }
    })

  def ivUriContactInfo(uri: String, header: Boolean = true)(implicit context: ContextWrapper, uiContext: UiContext[_]): Tweak[W] = Tweak[W](
    imageView => {
      glide() foreach { glide =>
        makeContactRequest(
          request = glide.using(new ContactPhotoLoader(context.application.getContentResolver)).load(Uri.parse(uri)),
          imageView = imageView,
          header = header,
          fadeInFailed = false)
      }
    })

  private[this] def glide()(implicit uiContext: UiContext[_]): Option[RequestManager] =
    Try {
      uiContext match {
        case c: ApplicationUiContext => Glide.`with`(c.value)
        case c: ActivityUiContext => Glide.`with`(c.value)
        case c: FragmentUiContext => Glide.`with`(c.value)
        case c: GenericUiContext => Glide.`with`(c.value)
      }
    }.toOption

  private[this] def loadCardUri(
    imageView: ImageView,
    request: DrawableTypeRequest[_],
    uri: String,
    char: String,
    circular: Boolean = false)(implicit context: ContextWrapper, uiContext: UiContext[_]) = {
    if (new File(uri).exists()) {
      makeRequest(
        request = request,
        imageView = imageView,
        char = char,
        circular = circular)
    } else {
      (imageView <~ ivSrc(new CharDrawable(char, circle = circular))).run
    }
  }

  private[this] def makeRequest(
    request: DrawableTypeRequest[_],
    imageView: ImageView,
    char: String,
    circular: Boolean = false,
    fadeInFailed: Boolean = true)(implicit context: ContextWrapper) = {
    request
      .crossFade()
      .into(new ViewTarget[ImageView, GlideDrawable](imageView) {
        override def onLoadStarted(placeholder: Drawable): Unit =
          view.setImageDrawable(javaNull)
        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit =
          (view <~ ivSrc(new CharDrawable(char, circle = circular)) <~ (if (fadeInFailed) fadeIn(200) else Snail.blank)).run
        override def onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation[_ >: GlideDrawable]): Unit =
          view.setImageDrawable(resource.getCurrent)
      })
  }

  private[this] def makeContactRequest(
    request: DrawableTypeRequest[_],
    imageView: ImageView,
    header: Boolean,
    fadeInFailed: Boolean = true)(implicit context: ContextWrapper) = {
    request
      .crossFade()
      .into(new ViewTarget[ImageView, GlideDrawable](imageView) {
        override def onLoadStarted(placeholder: Drawable): Unit =
          imageView.setImageDrawable(javaNull)
        override def onLoadFailed(e: Exception, errorDrawable: Drawable): Unit =
          if(header) loadDefaultHeaderImage(imageView, fadeInFailed) else loadDefaultGeneralInfoImage(imageView, fadeInFailed)
        override def onResourceReady(resource: GlideDrawable, glideAnimation: GlideAnimation[_ >: GlideDrawable]): Unit =
          view.setImageDrawable(resource.getCurrent)
      })
  }

  private[this] def loadDefaultHeaderImage(imageView: ImageView, fadeInFailed: Boolean = true) =
    (imageView <~ ivSrc(R.drawable.dialog_contact_header_no_image) <~ ivScaleType(ScaleType.CENTER_INSIDE) <~ (if (fadeInFailed) fadeIn(200) else Snail.blank)).run

  private[this] def loadDefaultGeneralInfoImage(imageView: ImageView, fadeInFailed: Boolean = true) =
    (imageView <~ ivSrc(R.drawable.dialog_contact_icon_general_info) <~ (if (fadeInFailed) fadeIn(200) else Snail.blank)).run

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