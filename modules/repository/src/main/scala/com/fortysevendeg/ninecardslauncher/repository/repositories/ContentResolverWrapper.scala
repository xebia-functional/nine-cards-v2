package com.fortysevendeg.ninecardslauncher.repository.repositories

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider

trait ContentResolverWrapper {
  def insert(uri: Uri, values: Map[String, Any]): Uri
  def delete(uri: Uri, where: String = "", whereParams: Seq[String] = Seq.empty): Int
  def query(uri: Uri, projection: Seq[String], where: String = "", whereParams: Seq[String] = Seq.empty, orderBy: String = ""): Cursor
  def update(uri: Uri, values: Map[String, Any], where: String = "", whereParams: Seq[String] = Seq.empty): Int
}

trait ContentResolverWrapperComponent {
  val contentResolverWrapper: ContentResolverWrapper
}

trait ContentResolverWrapperComponentImpl extends ContentResolverWrapperComponent {
  self: ContentResolverProvider =>

  lazy val contentResolverWrapper = new ContentResolverWrapperImpl

  class ContentResolverWrapperImpl extends ContentResolverWrapper {

    override def insert(
        uri: Uri,
        values: Map[String, Any]): Uri = contentResolver.insert(uri, map2ContentValue(values))

    override def update(
        uri: Uri,
        values: Map[String, Any],
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int =
      contentResolver.update(uri, map2ContentValue(values), where, whereParams.toArray)

    override def delete(
        uri: Uri,
        where: String = "",
        whereParams: Seq[String] = Seq.empty): Int = contentResolver.delete(uri, where, whereParams.toArray)

    override def query(
        uri: Uri,
        projection: Seq[String],
        where: String = "",
        whereParams: Seq[String] = Seq.empty,
        orderBy: String = ""): Cursor = contentResolver.query(uri, projection.toArray, where, whereParams.toArray, orderBy)

    def map2ContentValue(values: Map[String, Any]) = {
      val contentValues = new ContentValues()

      values foreach {
        case (key, value: Array[Byte]) => contentValues.put(key, value)
        case (key, value: Byte) => contentValues.put(key, value.asInstanceOf[java.lang.Byte])
        case (key, value: Boolean) => contentValues.put(key, value)
        case (key, value: Float) => contentValues.put(key, value.asInstanceOf[java.lang.Float])
        case (key, value: Double) => contentValues.put(key, value)
        case (key, value: Int) => contentValues.put(key, value.asInstanceOf[java.lang.Integer])
        case (key, value: Long) => contentValues.put(key, value.asInstanceOf[java.lang.Long])
        case (key, value: Short) => contentValues.put(key, value.asInstanceOf[java.lang.Short])
        case (key, value: String) => contentValues.put(key, value)
      }

      contentValues
    }
  }

}
