package com.fortysevendeg.ninecardslauncher.commons

import android.content.{ContentResolver, ContentValues}
import android.database.Cursor
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.provider.NineCardsContentProvider._

trait ContentResolverWrapper {
  def insert(nineCardsUri: NineCardsUri, values: Map[String, Any]): Int

  def delete(nineCardsUri: NineCardsUri, where: String = "", whereParams: Seq[String] = Seq.empty): Int

  def deleteById(nineCardsUri: NineCardsUri, id: Int, where: String = "", whereParams: Seq[String] = Seq.empty): Int

  def fetch[T](nineCardsUri: NineCardsUri, projection: Seq[String], where: String = "", whereParams: Seq[String] = Seq.empty, orderBy: String = "")(f: (Cursor) => Option[T]): Option[T]

  def fetchAll[T](nineCardsUri: NineCardsUri, projection: Seq[String], where: String = "", whereParams: Seq[String] = Seq.empty, orderBy: String = "")(f: (Cursor) => Seq[T]): Seq[T]

  def findById[T](nineCardsUri: NineCardsUri, id: Int, projection: Seq[String], where: String = "", whereParams: Seq[String] = Seq.empty, orderBy: String = "")(f: (Cursor) => Option[T]): Option[T]

  def update(nineCardsUri: NineCardsUri, values: Map[String, Any], where: String = "", whereParams: Seq[String] = Seq.empty): Int

  def updateById(nineCardsUri: NineCardsUri, id: Int, values: Map[String, Any], where: String = "", whereParams: Seq[String] = Seq.empty): Int
}

class ContentResolverWrapperImpl(contentResolver: ContentResolver) extends ContentResolverWrapper {

  override def insert(
    nineCardsUri: NineCardsUri,
    values: Map[String, Any]
    ): Int = {
    val uri = contentResolver.insert(getUri(nineCardsUri), map2ContentValue(values))
    Integer.parseInt(uri.getPathSegments.get(1))
  }

  override def update(
    nineCardsUri: NineCardsUri,
    values: Map[String, Any],
    where: String = "",
    whereParams: Seq[String] = Seq.empty
    ): Int =
    contentResolver.update(getUri(nineCardsUri), map2ContentValue(values), where, whereParams.toArray)

  override def updateById(
    nineCardsUri: NineCardsUri,
    id: Int,
    values: Map[String, Any],
    where: String = "",
    whereParams: Seq[String] = Seq.empty
    ): Int =
    contentResolver.update(withAppendedPath(getUri(nineCardsUri), id.toString), map2ContentValue(values), where, whereParams.toArray)

  override def delete(
    nineCardsUri: NineCardsUri,
    where: String = "",
    whereParams: Seq[String] = Seq.empty
    ): Int =
    contentResolver.delete(getUri(nineCardsUri), where, whereParams.toArray)

  override def deleteById(
    nineCardsUri: NineCardsUri,
    id: Int,
    where: String = "",
    whereParams: Seq[String] = Seq.empty
    ): Int =
    contentResolver.delete(withAppendedPath(getUri(nineCardsUri), id.toString), where, whereParams.toArray)

  override def fetch[T](
    nineCardsUri: NineCardsUri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""
    )(f: (Cursor) => Option[T]): Option[T] =
    Option(contentResolver.query(getUri(nineCardsUri), projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => None
      case Some(cursor) => f(cursor)
    }

  override def fetchAll[T](
    nineCardsUri: NineCardsUri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""
    )(f: (Cursor) => Seq[T]): Seq[T] =
    Option(contentResolver.query(getUri(nineCardsUri), projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => Seq.empty
      case Some(cursor) => f(cursor)
    }

  override def findById[T](
    nineCardsUri: NineCardsUri,
    id: Int,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""
    )(f: (Cursor) => Option[T]): Option[T] =
    Option(contentResolver.query(withAppendedPath(getUri(nineCardsUri), id.toString), projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => None
      case Some(cursor) => f(cursor)
    }

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
