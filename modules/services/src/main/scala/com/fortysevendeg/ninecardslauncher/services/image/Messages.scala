package com.fortysevendeg.ninecardslauncher.services.image

import android.graphics.Bitmap

case class AppPackage(
  packageName: String,
  className: String,
  name: String)

case class AppPackagePath(
  packageName: String,
  className: String,
  path: String)

case class AppWebsite(
  packageName: String,
  url: String,
  name: String)

case class AppWebsitePath(
  packageName: String,
  url: String,
  path: String)

case class SaveBitmap(
  bitmap: Bitmap,
  bitmapResize: Option[BitmapResize])

case class BitmapResize(width: Int, height: Int)

case class SaveBitmapPath(
  name: String,
  path: String)

case class ImageServicesConfig(
  colors: List[Int])
