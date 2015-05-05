package com.fortysevendeg.ninecardslauncher.modules.image

import android.content.pm.ResolveInfo
import com.fortysevendeg.ninecardslauncher.commons._

trait ImageServices {
  def createAppBitmap(term: String, info: ResolveInfo): String
  def getPath(filename: String): String
  def getImagePath(packageName: String, className: String): String
  def storeImageApp: Service[StoreImageAppRequest, StoreImageAppResponse]
}

trait ImageServicesComponent {
  val imageServices: ImageServices
}

