package com.fortysevendeg.ninecardslauncher.process.device.models

import android.content.Intent
import android.graphics.drawable.Drawable
import com.fortysevendeg.ninecardslauncher.process.collection.models.NineCardIntent
import com.fortysevendeg.ninecardslauncher.process.commons.types.{EmailCategory, PhoneCategory, DockType, NineCardCategory}
import com.fortysevendeg.ninecardslauncher.process.device.types.{CallType, WidgetResizeMode}

case class App(
  name: String,
  packageName: String,
  className: String,
  category: NineCardCategory,
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

case class LastCallsContact(
  hasContact: Boolean,
  number: String,
  title: String,
  photoUri: Option[String] = None,
  lookupKey: Option[String] = None,
  lastCallDate: Long,
  calls: Seq[CallData])

case class CallData(
  date: Long,
  callType: CallType)

case class Contact(
  name: String,
  lookupKey: String,
  photoUri: String,
  hasPhone: Boolean,
  favorite: Boolean,
  info: Option[ContactInfo] = None)

case class ContactInfo(
  emails: Seq[ContactEmail],
  phones: Seq[ContactPhone])

case class ContactEmail(
  address: String,
  category: EmailCategory)

case class ContactPhone(
  number: String,
  category: PhoneCategory)

case class DockApp(
  name: String,
  dockType: DockType,
  intent: NineCardIntent,
  imagePath: String,
  position: Int)

case class Widget(
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

case class TermCounter(
  term: String,
  count: Int)

