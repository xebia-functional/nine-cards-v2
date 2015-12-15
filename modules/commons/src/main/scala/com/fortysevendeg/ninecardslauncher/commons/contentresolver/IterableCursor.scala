package com.fortysevendeg.ninecardslauncher.commons.contentresolver

import android.database.Cursor

trait IterableCursor[T] {
  def count(): Int
  def moveToPosition(pos: Int): T
  def close(): Unit
}

object IterableCursor {

  implicit class RichCursor(c: Cursor) {
    private def implicitIterator[T](f: => T) = new IterableCursor[T] {
      override def count() = c.getCount
      override def moveToPosition(pos: Int): T = {
        c.moveToPosition(pos)
        f
      }
      override def close(): Unit = c.close()
    }

    private def implicitIter[T](f: => T) = implicitIterator[T](f)

    def toIterator[T](conversionFunction: Cursor => T) = implicitIter {
      conversionFunction(c)
    }

  }

}
