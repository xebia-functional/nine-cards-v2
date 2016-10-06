package cards.nine.services.calls

import android.database.Cursor
import cards.nine.models.Call
import cards.nine.models.types._
import cards.nine.services.contacts.Fields

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
      callType = Fields.CALL_TYPE)

  def parseNumberType(phoneType: Int): PhoneCategory =
    phoneType match {
      case Fields.PHONE_TYPE_HOME => PhoneHome
      case Fields.PHONE_TYPE_WORK => PhoneWork
      case Fields.PHONE_TYPE_MOBILE => PhoneMobile
      case _ => PhoneOther
    }

  private[this] def readCall(
    cursor: Cursor,
    number: String,
    name: String,
    numberType: PhoneCategory,
    date: String,
    callType: String) = {
      Call(
        number = cursor.getString(cursor.getColumnIndex(number)),
        name = Option(cursor.getString(cursor.getColumnIndex(name))),
        numberType = numberType,
        date = cursor.getLong(cursor.getColumnIndex(date)),
        callType = CallType(cursor.getInt(cursor.getColumnIndex(callType))))
   }
}