package cards.nine.models

import android.graphics.Bitmap

case class SaveBitmap(
  bitmap: Bitmap,
  bitmapResize: Option[BitmapResize])

case class BitmapResize(width: Int, height: Int)

case class SaveBitmapPath(
  name: String,
  path: String)

case class ImageServicesConfig(
  colors: List[Int])
