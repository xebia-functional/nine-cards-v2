package com.fortysevendeg.ninecardslauncher.app.ui.commons.glide

import android.graphics.drawable.{BitmapDrawable, Drawable}
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.bumptech.glide.util.Util
import com.fortysevendeg.ninecardslauncher.process.device.models.App
import macroid.ContextWrapper

class ApplicationIconDecoder(packageName: String)(implicit contextWrapper: ContextWrapper)
  extends ResourceDecoder[App, Drawable] {

  override def getId: String = packageName

  override def decode(source: App, width: Int, height: Int): Resource[Drawable] = {

    val icon = contextWrapper.application.getPackageManager.getApplicationIcon(source.packageName)

    new DrawableResource[Drawable](icon) {
      override def getSize: Int = drawable match {
        case bd: BitmapDrawable => Util.getBitmapByteSize(drawable.asInstanceOf[BitmapDrawable].getBitmap)
        case _ => 1
      }

      override def recycle(): Unit = {}
    }


  }

}
