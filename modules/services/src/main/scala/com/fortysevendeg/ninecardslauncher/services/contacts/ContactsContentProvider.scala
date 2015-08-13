package com.fortysevendeg.ninecardslauncher.services.contacts

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.services.contacts.models._

object ContactsContentProvider {

  val allFields = Seq(
    Fields.LOOKUP_KEY,
    Fields.DISPLAY_NAME,
    Fields.HAS_PHONE_NUMBER,
    Fields.STARRED)

  def contactFromCursor(cursor: Cursor) =
    Contact(
      lookupKey = cursor.getString(cursor.getColumnIndex(Fields.LOOKUP_KEY)),
      name = cursor.getString(cursor.getColumnIndex(Fields.DISPLAY_NAME)),
      hasPhone = cursor.getInt(cursor.getColumnIndex(Fields.HAS_PHONE_NUMBER)) > 0,
      favorite = cursor.getInt(cursor.getColumnIndex(Fields.STARRED)) > 0)

  val allEmailContactFields = Seq(
    Fields.EMAIL_LOOKUP_KEY,
    Fields.EMAIL_DISPLAY_NAME,
    Fields.EMAIL_HAS_PHONE_NUMBER,
    Fields.EMAIL_STARRED)

  val allEmailFields = Seq(
    Fields.EMAIL_TYPE,
    Fields.EMAIL_ADDRESS)

  def contactFromEmailCursor(cursor: Cursor) =
    Contact(
      lookupKey = cursor.getString(cursor.getColumnIndex(Fields.EMAIL_LOOKUP_KEY)),
      name = cursor.getString(cursor.getColumnIndex(Fields.EMAIL_DISPLAY_NAME)),
      hasPhone = cursor.getInt(cursor.getColumnIndex(Fields.EMAIL_HAS_PHONE_NUMBER)) > 0,
      favorite = cursor.getInt(cursor.getColumnIndex(Fields.EMAIL_STARRED)) > 0)

  def emailFromCursor(cursor: Cursor) =
    ContactEmail(
      address = cursor.getString(cursor.getColumnIndex(Fields.EMAIL_ADDRESS)),
      category = parseEmailType(cursor.getInt(cursor.getColumnIndex(Fields.EMAIL_TYPE))))

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
    Fields.PHONE_TYPE,
    Fields.PHONE_NUMBER)

  def contactFromPhoneCursor(cursor: Cursor) =
    Contact(
      lookupKey = cursor.getString(cursor.getColumnIndex(Fields.PHONE_LOOKUP_KEY)),
      name = cursor.getString(cursor.getColumnIndex(Fields.PHONE_DISPLAY_NAME)),
      hasPhone = cursor.getInt(cursor.getColumnIndex(Fields.PHONE_HAS_PHONE_NUMBER)) > 0,
      favorite = cursor.getInt(cursor.getColumnIndex(Fields.PHONE_STARRED)) > 0)

  def phoneFromCursor(cursor: Cursor) =
    ContactPhone(
      number = cursor.getString(cursor.getColumnIndex(Fields.PHONE_NUMBER)),
      category = parsePhoneType(cursor.getInt(cursor.getColumnIndex(Fields.PHONE_TYPE))))
  
  def parsePhoneType(phoneType: Int): PhoneCategory =
    phoneType match {
      case Fields.PHONE_TYPE_HOME => PhoneHome
      case Fields.PHONE_TYPE_WORK => PhoneWork
      case Fields.PHONE_TYPE_MOBILE => PhoneMobile
      case _ => PhoneOther
    }
}