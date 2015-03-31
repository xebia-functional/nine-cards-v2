package com.fortysevendeg.ninecardslauncher.provider

import android.content.Context
import android.database.Cursor

import scala.annotation.tailrec

trait DBUtils {

  def emptyAllTables(context: Context) = {
    val resolver = context.getContentResolver
    resolver.delete(NineCardsContentProvider.ContentUriCacheCategory, "", Array.empty)
    resolver.delete(NineCardsContentProvider.ContentUriCard, "", Array.empty)
    resolver.delete(NineCardsContentProvider.ContentUriCollection, "", Array.empty)
    resolver.delete(NineCardsContentProvider.ContentUriGeoInfo, "", Array.empty)
  }

  def execAllVersionsDB(context: Context) =
    for (i <- 1 to NineCardsSqlHelper.DatabaseVersion) execVersion(context, i)

  def execVersionsDB(context: Context, oldVersion: Int, newVersion: Int) {
    for (i <- oldVersion + 1 to newVersion) execVersion(context, i)
  }

  def execVersion(context: Context, version: Int) {}

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
