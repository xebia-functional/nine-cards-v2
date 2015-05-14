package com.fortysevendeg.ninecardslauncher.commons

import android.content.ContentValues

class RichContentValues(value: ContentValues) {

  def put(key: String, byteValue: Byte) = value.put(key, byteValue.asInstanceOf[java.lang.Byte])
  def put(key: String, floatValue: Float) = value.put(key, floatValue.asInstanceOf[java.lang.Float])
  def put(key: String, intValue: Int) = value.put(key, intValue.asInstanceOf[java.lang.Integer])
  def put(key: String, longValue: Long) = value.put(key, longValue.asInstanceOf[java.lang.Long])
  def put(key: String, shortValue: Short) = value.put(key, shortValue.asInstanceOf[java.lang.Short])
}

object RichContentValues {
  implicit def enrichContentValues(contentValues: ContentValues): RichContentValues = new RichContentValues(contentValues)
}