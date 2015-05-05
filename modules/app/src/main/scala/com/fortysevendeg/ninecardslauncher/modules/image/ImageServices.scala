package com.fortysevendeg.ninecardslauncher.modules.image

import android.content.pm.ResolveInfo

trait ImageServices {
  def createAppBitmap(term: String, info: ResolveInfo): String
  def getPath(filename: String): String
  def getImagePath(packageName: String, className: String): String
}

trait ImageServicesComponent {
  val imageServices: ImageServices
}

