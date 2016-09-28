package com.fortysevendeg.ninecardslauncher.app.ui.commons.glide

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Util
import com.fortysevendeg.macroid.extras.DeviceVersion.Marshmallow
import macroid.ContextWrapper

class IconFromPackageDecoder(packageName: String)(implicit contextWrapper: ContextWrapper)
  extends ResourceDecoder[Int, Bitmap] {

  override def getId: String = packageName

  override def decode(source: Int, width: Int, height: Int): Resource[Bitmap] = {

    val resources = contextWrapper.application.getPackageManager.getResourcesForApplication(packageName)
    val icon = Marshmallow ifSupportedThen {
      resources.getDrawable(source, null).asInstanceOf[BitmapDrawable].getBitmap
    } getOrElse {
      resources.getDrawable(source).asInstanceOf[BitmapDrawable].getBitmap
    }

    val pool = Glide.get(contextWrapper.bestAvailable).getBitmapPool

    new BitmapResource(icon, pool) {
      override def getSize: Int = Util.getBitmapByteSize(icon)

      override def recycle(): Unit = {}
    }

  }

}
