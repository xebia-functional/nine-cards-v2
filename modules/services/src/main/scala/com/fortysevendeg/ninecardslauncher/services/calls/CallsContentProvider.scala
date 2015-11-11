package com.fortysevendeg.ninecardslauncher.services.calls

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.services.calls.models._
import com.fortysevendeg.ninecardslauncher.services.commons._
import com.fortysevendeg.ninecardslauncher.services.contacts.Fields

object CallsContentProvider {

  val allFields = Seq(
    Fields.CALL_NUMBER,
    Fields.CALL_NAME,
    Fields.CALL_NUMBER_TYPE,
    Fields.CALL_DATE,
    Fields.CALL_TYPE)

  def callFromCursor(cursor: Cursor) =
    readCall(
      cursor = cursor,
      number = Fields.CALL_NUMBER,
      name = Fields.CALL_NAME,
      numberType = parseNumberType(cursor.getInt(cursor.getColumnIndex(Fields.CALL_NUMBER_TYPE))),
      date = Fields.CALL_DATE,
      callType = parseCallType(cursor.getInt(cursor.getColumnIndex(Fields.CALL_TYPE))))

  def parseNumberType(phoneType: Int): PhoneCategory =
    phoneType match {
      case Fields.PHONE_TYPE_HOME => PhoneHome
      case Fields.PHONE_TYPE_WORK => PhoneWork
      case Fields.PHONE_TYPE_MOBILE => PhoneMobile
      case _ => PhoneOther
    }

  def parseCallType(callType: Int): CallType =
    callType match {
      case Fields.CALL_INCOMING_TYPE => IncomingType
      case Fields.CALL_OUTGOING_TYPE => OutgoingType
      case Fields.CALL_MISSED_TYPE => MissedType
      case _ => OtherType
    }

  private[this] def readCall(
    cursor: Cursor,
    number: String,
    name: String,
    numberType: PhoneCategory,
    date: String,
    callType: CallType) = {
      Call(
        number = cursor.getString(cursor.getColumnIndex(number)),
        name = Option(cursor.getString(cursor.getColumnIndex(name))),
        numberType = numberType,
        date = cursor.getString(cursor.getColumnIndex(date)),
        callType = callType)
   }
}