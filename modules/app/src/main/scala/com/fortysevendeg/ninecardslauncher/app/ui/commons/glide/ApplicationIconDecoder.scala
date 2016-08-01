package com.fortysevendeg.ninecardslauncher.app.ui.commons.glide

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.util.Util
import macroid.ContextWrapper

class ApplicationIconDecoder(packageName: String)(implicit contextWrapper: ContextWrapper)
  extends ResourceDecoder[String, Bitmap] {

  override def getId: String = packageName

  override def decode(source: String, width: Int, height: Int): Resource[Bitmap] = {

    val icon =
      contextWrapper.application.getPackageManager.getApplicationIcon(packageName).asInstanceOf[BitmapDrawable].getBitmap

    val pool = Glide.get(contextWrapper.bestAvailable).getBitmapPool

    new BitmapResource(icon, pool) {
      override def getSize: Int = Util.getBitmapByteSize(icon)

      override def recycle(): Unit = {}
    }

  }

}
