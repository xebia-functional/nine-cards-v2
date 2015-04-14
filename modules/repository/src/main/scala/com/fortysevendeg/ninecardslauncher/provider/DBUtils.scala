package com.fortysevendeg.ninecardslauncher.provider

import android.database.Cursor
import com.fortysevendeg.ninecardslauncher.commons.ContentResolverProvider
import com.fortysevendeg.ninecardslauncher.provider.NineCardsContentProvider._
import com.fortysevendeg.ninecardslauncher.provider.NineCardsSqlHelper._

import scala.annotation.tailrec

trait DBUtils {

  self: ContentResolverProvider =>

  def emptyAllTables = {
    contentResolver.delete(ContentUriCacheCategory, "", Array.empty)
    contentResolver.delete(ContentUriCard, "", Array.empty)
    contentResolver.delete(ContentUriCollection, "", Array.empty)
    contentResolver.delete(ContentUriGeoInfo, "", Array.empty)
  }

  def execAllVersionsDB() = (1 to DatabaseVersion) foreach { version => execVersion(version) }

  def execVersionsDB(oldVersion: Int, newVersion: Int) =
    (oldVersion + 1 to newVersion) foreach { version => execVersion(version) }

  def execVersion(version: Int) = {}

  def getEntityFromCursor[T](cursor: Option[Cursor], conversionFunction: Cursor => T): Option[T] = cursor match {
    case Some(cursorObject) if cursorObject.moveToFirst() =>
      val result = Some(conversionFunction(cursorObject))
      cursorObject.close()
      result
    case _ => None
  }

  def getListFromCursor[T](cursor: Option[Cursor], conversionFunction: Cursor => T): Seq[T] = {
    @tailrec
    def getListFromEntityLoop(cursor: Cursor, result: Seq[T]): Seq[T] = {
      cursor match {
        case validCursor if validCursor.isAfterLast => result
        case _ => {
          val entity = conversionFunction(cursor)
          cursor.moveToNext
          getListFromEntityLoop(cursor, entity +: result)
        }
      }
    }

    cursor match {
      case Some(cursorObject) if cursorObject.moveToFirst() => {
        val result = getListFromEntityLoop(cursorObject, Seq.empty[T])
        cursorObject.close()
        result
      }
      case _ => Seq.empty[T]
    }
  }
}
