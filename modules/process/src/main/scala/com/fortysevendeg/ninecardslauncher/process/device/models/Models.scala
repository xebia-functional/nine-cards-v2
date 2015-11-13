package com.fortysevendeg.ninecardslauncher.process.device.models

import android.content.Intent
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.process.device.WidgetResizeMode

case class App(
  name: String,
  packageName: String,
  className: String,
  category: String,
  imagePath: String,
  colorPrimary: String,
  dateInstalled: Long,
  dateUpdate: Long,
  version: String,
  installedFromGooglePlay: Boolean)

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

case class Widget (
  userHashCode: Option[Int],
  autoAdvanceViewId: Int,
  initialLayout: Int,
  dimensions: WidgetDimensions,
  className: String,
  packageName: String,
  resizeMode: WidgetResizeMode,
  updatePeriodMillis: Int,
  label: String,
  icon: Drawable,
  preview: Option[Drawable])

case class WidgetDimensions(
  minCellHeight: Int,
  minResizeCellHeight: Int,
  minResizeCellWidth: Int,
  minCellWidth: Int)


