package com.fortysevendeg.ninecardslauncher.commons

import android.content.ContentValues

class RichContentValues(value: ContentValues) {

  def put(key: String, intValue: Int) = value.put(key, intValue.asInstanceOf[java.lang.Integer])
}

object RichContentValues {
  implicit def enrichContentValues(contentValues: ContentValues): RichContentValues = new RichContentValues(contentValues)
}