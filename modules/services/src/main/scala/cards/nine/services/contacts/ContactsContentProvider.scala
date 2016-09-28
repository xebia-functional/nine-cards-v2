package com.fortysevendeg.ninecardslauncher.services.contacts

import android.database.Cursor
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.contentresolver.UriCreator
import com.fortysevendeg.ninecardslauncher.services.commons._
import com.fortysevendeg.ninecardslauncher.services.contacts.models._

object ContactsContentProvider {

  val allFields = Seq(
    Fields.LOOKUP_KEY,
    Fields.DISPLAY_NAME,
    Fields.HAS_PHONE_NUMBER,
    Fields.STARRED)

  def nameFromCursor(cursor: Cursor): String =
    cursor.getString(cursor.getColumnIndex(Fields.DISPLAY_NAME))

  def contactFromCursor(uriCreator: UriCreator, cursor: Cursor) =
    readContact(
      uriCreator = uriCreator,
      cursor = cursor,
      lookupKeyColumn = Fields.LOOKUP_KEY,
      nameColumn = Fields.DISPLAY_NAME,
      hasPhoneColumn = Fields.HAS_PHONE_NUMBER,
      starredColumn = Fields.STARRED)

  val allEmailContactFields = Seq(
    Fields.EMAIL_LOOKUP_KEY,
    Fields.EMAIL_DISPLAY_NAME,
    Fields.EMAIL_HAS_PHONE_NUMBER,
    Fields.EMAIL_STARRED)

  val allEmailFields = Seq(
    Fields.EMAIL_LOOKUP_KEY,
    Fields.EMAIL_TYPE,
    Fields.EMAIL_ADDRESS)

  def contactFromEmailCursor(uriCreator: UriCreator, cursor: Cursor) =
    readContact(
      uriCreator = uriCreator,
      cursor = cursor,
      lookupKeyColumn = Fields.EMAIL_LOOKUP_KEY,
      nameColumn = Fields.EMAIL_DISPLAY_NAME,
      hasPhoneColumn = Fields.EMAIL_HAS_PHONE_NUMBER,
      starredColumn = Fields.EMAIL_STARRED)

  def emailFromCursor(cursor: Cursor) =
    ContactEmail(
      address = cursor.getString(cursor.getColumnIndex(Fields.EMAIL_ADDRESS)),
      category = parseEmailType(cursor.getInt(cursor.getColumnIndex(Fields.EMAIL_TYPE))))

  def lookupKeyAndEmailFromCursor(cursor: Cursor): (String, ContactEmail) =
    (cursor.getString(cursor.getColumnIndex(Fields.EMAIL_LOOKUP_KEY)),
    ContactEmail(
      address = cursor.getString(cursor.getColumnIndex(Fields.EMAIL_ADDRESS)),
      category = parseEmailType(cursor.getInt(cursor.getColumnIndex(Fields.EMAIL_TYPE)))))

  def parseEmailType(phoneType: Int): EmailCategory =
    phoneType match {
      case Fields.EMAIL_TYPE_HOME => EmailHome
      case Fields.EMAIL_TYPE_WORK => EmailWork
      case _ => EmailOther
    }

  val allPhoneContactFields = Seq(
    Fields.PHONE_LOOKUP_KEY,
    Fields.PHONE_DISPLAY_NAME,
    Fields.PHONE_HAS_PHONE_NUMBER,
    Fields.PHONE_STARRED)

  val allPhoneFields = Seq(
    Fields.PHONE_LOOKUP_KEY,
    Fields.PHONE_TYPE,
    Fields.PHONE_NUMBER,
    Fields.PHONE_CUSTOM_RINGTONE)

  def contactFromPhoneCursor(uriCreator: UriCreator, cursor: Cursor) =
    readContact(
      uriCreator = uriCreator,
      cursor = cursor,
      lookupKeyColumn = Fields.PHONE_LOOKUP_KEY,
      nameColumn = Fields.PHONE_DISPLAY_NAME,
      hasPhoneColumn = Fields.PHONE_HAS_PHONE_NUMBER,
      starredColumn = Fields.PHONE_STARRED)

  def phoneFromCursor(cursor: Cursor) =
    ContactPhone(
      number = cursor.getString(cursor.getColumnIndex(Fields.PHONE_NUMBER)),
      category = parsePhoneType(cursor.getInt(cursor.getColumnIndex(Fields.PHONE_TYPE))))

  def lookupKeyAndPhoneFromCursor(cursor: Cursor): (String, ContactPhone) =
    (cursor.getString(cursor.getColumnIndex(Fields.PHONE_LOOKUP_KEY)),
    ContactPhone(
      number = cursor.getString(cursor.getColumnIndex(Fields.PHONE_NUMBER)),
      category = parsePhoneType(cursor.getInt(cursor.getColumnIndex(Fields.PHONE_TYPE)))))

  def parsePhoneType(phoneType: Int): PhoneCategory =
    phoneType match {
      case Fields.PHONE_TYPE_HOME => PhoneHome
      case Fields.PHONE_TYPE_WORK => PhoneWork
      case Fields.PHONE_TYPE_MOBILE => PhoneMobile
      case Fields.PHONE_TYPE_MAIN => PhoneMain
      case Fields.PHONE_TYPE_FAX_WORK => PhoneFaxWork
      case Fields.PHONE_TYPE_FAX_HOME => PhoneFaxHome
      case Fields.PHONE_TYPE_PAGER => PhonePager
      case _ => PhoneOther
    }

  private[this] def readContact(
    uriCreator: UriCreator,
    cursor: Cursor,
    lookupKeyColumn: String,
    nameColumn: String,
    hasPhoneColumn: String,
    starredColumn: String) = {
    val lookupKey = cursor.getString(cursor.getColumnIndex(lookupKeyColumn))
    Contact(
      lookupKey = lookupKey,
      photoUri = uriCreator.withAppendedPath(Fields.PHOTO_URI, lookupKey).toString,
      name = cursor.getString(cursor.getColumnIndex(nameColumn)),
      hasPhone = cursor.getInt(cursor.getColumnIndex(hasPhoneColumn)) > 0,
      favorite = cursor.getInt(cursor.getColumnIndex(starredColumn)) > 0)
  }
}