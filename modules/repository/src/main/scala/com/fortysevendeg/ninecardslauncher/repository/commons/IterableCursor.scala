package com.fortysevendeg.ninecardslauncher.repository.commons

import android.database.Cursor

object IterableCursor {

  trait IterableCursorSeq[T] {
    def count(): Int
    def moveToPosition(pos: Int): T
    def close(): Unit
  }

  implicit class RichCursor(c: Cursor) {
    private def implicitIterator[T](f: => T) = new IterableCursorSeq[T] {
      override def count() = c.getCount
      override def moveToPosition(pos: Int): T = {
        c.moveToPosition(pos)
        f
      }
      override def close(): Unit = c.close()
    }

    private def implicitIter[T](f: => T) = implicitIterator[T] {
      f
    }

    def toIterator[T](conversionFunction: Cursor => T) = implicitIter {
      conversionFunction(c)
    }

  }

}
