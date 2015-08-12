package com.fortysevendeg.ninecardslauncher.services.contacts

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.services.contacts.models.Contact

object ContactsContentProvider {

  val allFields = Seq(
    Fields.CONTACT_ID,
    Fields.DISPLAY_NAME)

  def contactFromCursor(cursor: Cursor) =
    Contact(
      id = cursor.getLong(cursor.getColumnIndex(Fields.CONTACT_ID)),
      name = cursor.getString(cursor.getColumnIndex(Fields.DISPLAY_NAME)),
      image = cursor.getLong(cursor.getColumnIndex(Fields.CONTACT_ID)))
}
