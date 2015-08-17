package com.fortysevendeg.ninecardslauncher.process.device.models

import android.content.Intent
import android.graphics.drawable.Drawable

case class AppCategorized(
  name: String,
  packageName: String,
  className: String,
  imagePath: Option[String],
  category: Option[String] = None,
  starRating: Option[Double] = None,
  numDownloads: Option[String] = None,
  ratingsCount: Option[Int] = None,
  commentCount: Option[Int] = None)

case class Shortcut (
  title: String,
  icon: Option[Drawable],
  intent: Intent)