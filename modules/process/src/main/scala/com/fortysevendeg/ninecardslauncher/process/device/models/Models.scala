package com.fortysevendeg.ninecardslauncher.process.device.models

import android.content.Intent
import android.graphics.drawable.Drawable

case class App(
  name: String,
  packageName: String,
  className: String,
  resourceIcon: Int,
  colorPrimary: String,
  dateInstalled: Double,
  dateUpdate: Double,
  version: String,
  installedFromGooglePlay: Boolean)

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

case class Contact(
  name: String,
  lookupKey: String,
  photoUri: String,
  info: Option[ContactInfo] = None)

case class ContactInfo(
  emails: Seq[ContactEmail],
  phones: Seq[ContactPhone])

case class ContactEmail(
  address: String,
  category: String)

case class ContactPhone(
  number: String,
  category: String)
