package com.fortysevendeg.ninecardslauncher.commons.contentresolver

import java.util

import android.content.{ContentProviderOperation, ContentResolver, ContentValues}
import android.database.Cursor
import android.net.Uri
import android.net.Uri._
import com.fortysevendeg.ninecardslauncher.commons.javaNull

trait ContentResolverWrapper {

  def getCursor(
    uri: Uri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = ""): Cursor

  def insert(
    uri: Uri,
    values: Map[String, Any],
    notificationUri: Option[Uri] = None): Int

  def inserts(
    authority: String,
    uri: Uri,
    allValues: Seq[Map[String, Any]],
    notificationUri: Option[Uri] = None): Unit

  def delete(
    uri: Uri,
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int

  def deleteById(
    uri: Uri,
    id: Int,
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int

  def fetch[T](
    uri: Uri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Option[T]): Option[T]

  def fetchAll[T](
    uri: Uri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Seq[T]): Seq[T]

  def findById[T](
    uri: Uri,
    id: Int,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Option[T]): Option[T]

  def update(
    uri: Uri,
    values: Map[String, Any],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int

  def updateById(
    uri: Uri,
    id: Int,
    values: Map[String, Any],
    notificationUri: Option[Uri] = None): Int

  def updateByIds(
    authority: String,
    uri: Uri,
    idAndValues: Seq[(Int, Map[String, Any])],
    notificationUri: Option[Uri] = None): Seq[Int]
}

class ContentResolverWrapperImpl(contentResolver: ContentResolver)
  extends ContentResolverWrapper {

  override def getCursor(
    uri: Uri,
    projection: Seq[String],
    where: String,
    whereParams: Seq[String],
    orderBy: String): Cursor =
    contentResolver.query(uri, projection.toArray, where, whereParams.toArray, orderBy)

  override def insert(
    uri: Uri,
    values: Map[String, Any],
    notificationUri: Option[Uri] = None): Int = {
    val response = contentResolver.insert(uri, map2ContentValue(values))
    val idString = response.getPathSegments.get(1)
    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    Integer.parseInt(idString)
  }

  override def inserts(
    authority: String,
    uri: Uri,
    allValues: Seq[Map[String, Any]],
    notificationUri: Option[Uri] = None): Unit = {

    allValues.map { p => android.util.Log.d("9cards", s"data: $p")}

    val operations = allValues map { values =>
      ContentProviderOperation.newInsert(uri)
        .withValues(map2ContentValue(values))
        .build()
    }

    import scala.collection.JavaConverters._
    contentResolver.applyBatch(authority, new util.ArrayList(operations.asJava))

    notificationUri foreach (contentResolver.notifyChange(_, javaNull))

  }

  override def update(
    uri: Uri,
    values: Map[String, Any],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int = {
    val updatedRowCount = contentResolver.update(uri, map2ContentValue(values), where, whereParams.toArray)
    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    updatedRowCount
  }

  override def updateById(
    uri: Uri,
    id: Int,
    values: Map[String, Any],
    notificationUri: Option[Uri] = None): Int = {
    val updatedRowCount = contentResolver.update(withAppendedPath(uri, id.toString), map2ContentValue(values), "", Seq.empty.toArray)
    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    updatedRowCount
  }

  override def updateByIds(
    authority: String,
    uri: Uri,
    idAndValues: Seq[(Int, Map[String, Any])],
    notificationUri: Option[Uri] = None): Seq[Int] = {

    val operations = idAndValues map {
      case (id, values) =>
        ContentProviderOperation.newUpdate(withAppendedPath(uri, id.toString))
          .withValues(map2ContentValue(values))
          .build()
    }

    import scala.collection.JavaConverters._
    val result = contentResolver.applyBatch(authority, new util.ArrayList(operations.asJava))

    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    result map (_.count.toInt)
  }

  override def delete(
    uri: Uri,
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int = {
    val deletedRowCount = contentResolver.delete(uri, where, whereParams.toArray)
    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    deletedRowCount
  }

  override def deleteById(
    uri: Uri,
    id: Int,
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    notificationUri: Option[Uri] = None): Int = {
    val deletedRowCount = contentResolver.delete(withAppendedPath(uri, id.toString), where, whereParams.toArray)
    notificationUri foreach (contentResolver.notifyChange(_, javaNull))
    deletedRowCount
  }

  override def fetch[T](
    uri: Uri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Option[T]): Option[T] =
    Option(contentResolver.query(uri, projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => None
      case Some(cursor) => f(cursor)
    }

  override def fetchAll[T](
    uri: Uri,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Seq[T]): Seq[T] =
    Option(contentResolver.query(uri, projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => Seq.empty
      case Some(cursor) => f(cursor)
    }

  override def findById[T](
    uri: Uri,
    id: Int,
    projection: Seq[String],
    where: String = "",
    whereParams: Seq[String] = Seq.empty,
    orderBy: String = "")(f: (Cursor) => Option[T]): Option[T] =
    Option(contentResolver.query(withAppendedPath(uri, id.toString), projection.toArray, where, whereParams.toArray, orderBy)) match {
      case None => None
      case Some(cursor) => f(cursor)
    }

  def map2ContentValue(values: Map[String, Any]) = {
    val contentValues = new ContentValues()

    values foreach {
      case (key, `javaNull`) => contentValues.putNull(key)
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
